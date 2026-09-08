-- ==========================================
-- チケット管理システム (Redmine風) DDL スクリプト
-- 対応 DB: PostgreSQL
-- ==========================================

-- 1. 既存テーブルの削除 (依存関係を考慮した逆順削除)
DROP TABLE IF EXISTS workflows CASCADE;
DROP TABLE IF EXISTS journal_details CASCADE;
DROP TABLE IF EXISTS journals CASCADE;
DROP TABLE IF EXISTS issues CASCADE;
DROP TABLE IF EXISTS issue_statuses CASCADE;
DROP TABLE IF EXISTS trackers CASCADE;
DROP TABLE IF EXISTS project_members CASCADE;
DROP TABLE IF EXISTS projects CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ==========================================
-- 2. テーブル作成 (CREATE TABLE)
-- ==========================================

-- 2.1 ユーザーテーブル (users)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2.2 ロールテーブル (roles)
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- 2.3 プロジェクトテーブル (projects)
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    identifier VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2.4 プロジェクトメンバーテーブル (project_members)
CREATE TABLE project_members (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_pm_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT,
    CONSTRAINT uk_project_user_role UNIQUE (project_id, user_id, role_id)
);

-- 2.5 トラッカーテーブル (trackers)
CREATE TABLE trackers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    position INT NOT NULL DEFAULT 0
);

-- 2.6 ステータステーブル (issue_statuses)
CREATE TABLE issue_statuses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    position INT NOT NULL DEFAULT 0
);

-- 2.7 チケットテーブル (issues)
CREATE TABLE issues (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    tracker_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    assignee_id BIGINT,
    subject VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    start_date DATE,
    due_date DATE,
    estimated_hours NUMERIC(5, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_issues_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_issues_tracker FOREIGN KEY (tracker_id) REFERENCES trackers(id) ON DELETE RESTRICT,
    CONSTRAINT fk_issues_status FOREIGN KEY (status_id) REFERENCES issue_statuses(id) ON DELETE RESTRICT,
    CONSTRAINT fk_issues_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_issues_assignee FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 2.8 変更履歴ヘッダーテーブル (journals)
CREATE TABLE journals (
    id BIGSERIAL PRIMARY KEY,
    issue_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_journals_issue FOREIGN KEY (issue_id) REFERENCES issues(id) ON DELETE CASCADE,
    CONSTRAINT fk_journals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

-- 2.9 変更履歴明細テーブル (journal_details)
CREATE TABLE journal_details (
    id BIGSERIAL PRIMARY KEY,
    journal_id BIGINT NOT NULL,
    property VARCHAR(50) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    CONSTRAINT fk_jd_journal FOREIGN KEY (journal_id) REFERENCES journals(id) ON DELETE CASCADE
);

-- 2.10 ワークフロー制御テーブル (workflows)
CREATE TABLE workflows (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    tracker_id BIGINT NOT NULL,
    old_status_id BIGINT NOT NULL,
    new_status_id BIGINT NOT NULL,
    CONSTRAINT fk_wf_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_wf_tracker FOREIGN KEY (tracker_id) REFERENCES trackers(id) ON DELETE CASCADE,
    CONSTRAINT fk_wf_old_status FOREIGN KEY (old_status_id) REFERENCES issue_statuses(id) ON DELETE CASCADE,
    CONSTRAINT fk_wf_new_status FOREIGN KEY (new_status_id) REFERENCES issue_statuses(id) ON DELETE CASCADE,
    CONSTRAINT uk_workflow_rule UNIQUE (role_id, tracker_id, old_status_id, new_status_id)
);

-- ==========================================
-- 3. インデックスの作成 (パフォーマンス最適化)
-- ==========================================

CREATE INDEX idx_issues_project_id ON issues(project_id);
CREATE INDEX idx_issues_status_id ON issues(status_id);
CREATE INDEX idx_issues_assignee_id ON issues(assignee_id);
CREATE INDEX idx_journals_issue_id ON journals(issue_id);
CREATE INDEX idx_journal_details_journal_id ON journal_details(journal_id);

-- ==========================================
-- 4. 初期マスターデータ投入 (Optional Seed Data)
-- ==========================================

INSERT INTO roles (name, description) VALUES
('システム管理者', 'すべての操作が可能'),
('開発者', 'チケットの作成、編集、割り当てが可能'),
('閲覧者', 'チケットの閲覧のみ可能');

INSERT INTO trackers (name, position) VALUES
('バグ', 1),
('機能追加', 2),
('タスク', 3);

INSERT INTO issue_statuses (name, is_closed, position) VALUES
('新規', false, 1),
('進行中', false, 2),
('解決', false, 3),
('完了', true, 4),
('却下', true, 5);