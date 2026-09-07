## 1. 概要
本ドキュメントは、Redmine風チケット管理システムを構築するためのデータベース（PostgreSQL）のテーブル一覧および各テーブルの詳細設計情報です。

---

## 2. テーブル一覧 (Table Overview)

| No. | テーブル名（物理名） | テーブル名（論理名） | 概要・説明 | 主キー (PK) |
|---|---|---|---|---|
| 1 | `users` | ユーザー | システムを利用するユーザー情報 | `id` |
| 2 | `roles` | ロール（権限） | プロジェクト内における権限グループ情報 | `id` |
| 3 | `projects` | プロジェクト | チケットやメンバを管理する最上位単位 | `id` |
| 4 | `project_members` | プロジェクトメンバー | ユーザーとプロジェクトおよびロールのマッピング | `id` |
| 5 | `trackers` | トラッカー | チケットの種別（バグ、機能追加、タスク等） | `id` |
| 6 | `issue_statuses` | ステータス | チケットの状態（新規、進行中、解決、完了等） | `id` |
| 7 | `issues` | チケット | タスクやバグ等の詳細データ | `id` |
| 8 | `journals` | 変更履歴ヘッダー | チケットの更新イベント（コメント、更新者、更新日時） | `id` |
| 9 | `journal_details` | 変更履歴明細 | チケット変更時のカラム単位での変更前後の値 | `id` |
| 10 | `workflows` | ワークフロー制御 | ロール・トラッカーごとのステータス遷移許可定義 | `id` |

---

## 3. 各テーブル詳細設計 (Table Definitions)

### 3.1. `users`（ユーザー）
| カラム名（物理名） | 論理名 | データ型 | 制約 | 説明 |
|---|---|---|---|---|
| `id` | ユーザーID | `BIGSERIAL` | PRIMARY KEY | 自動採番キー |
| `username` | ユーザー名 | `VARCHAR(50)` | NOT NULL, UNIQUE | ログイン用ユーザー名 |
| `email` | メールアドレス | `VARCHAR(255)` | NOT NULL, UNIQUE | 通知・ログイン用メールアドレス |
| `password_hash` | パスワードハッシュ | `VARCHAR(255)` | NOT NULL | BCrypt等で暗号化されたパスワード |
| `full_name` | 氏名 | `VARCHAR(100)` | NOT NULL | 表示用氏名 |
| `is_admin` | システム管理者フラグ | `BOOLEAN` | DEFAULT false | システム全体の管理者権限 |
| `created_at` | 作成日時 | `TIMESTAMP` | NOT NULL, DEFAULT CURRENT_TIMESTAMP | レコード作成日時 |
| `updated_at` | 更新日時 | `TIMESTAMP` | NOT NULL, DEFAULT CURRENT_TIMESTAMP | レコード更新日時 |

---

### 3.2. `roles`（ロール）
| カラム名（物理名） | 論理名 | データ型 | 制約 | 説明 |
|---|---|---|---|---|
| `id` | ロールID | `BIGSERIAL` | PRIMARY KEY | 自動採番キー |
| `name` | ロール名 | `VARCHAR(50)` | NOT NULL, UNIQUE | 管理者, 開発者, 報告者, 閲覧者 など |
| `description` | 説明 | `TEXT` | - | ロールの役割・定義に関する説明 |

---

### 3.3. `projects`（プロジェクト）
| カラム名（物理名） | 論理名 | データ型 | 制約 | 説明 |
|---|---|---|---|---|
| `id` | プロジェクトID | `BIGSERIAL` | PRIMARY KEY | 自動採番キー |
| `name` | プロジェクト名 | `VARCHAR(100)` | NOT NULL | プロジェクトの名称 |
| `identifier` | プロジェクト識別子 | `VARCHAR(50)` | NOT NULL, UNIQUE | URLやキー指定に用いる一意な文字列 |
| `description` | 説明 | `TEXT` | - | プロジェクトの詳細説明 |
| `is_public` | 公開フラグ | `BOOLEAN` | DEFAULT true | 全ユーザー参照許可フラグ |
| `created_at` | 作成日時 | `TIMESTAMP` | NOT NULL, DEFAULT CURRENT_TIMESTAMP | レコード作成日時 |

---

### 3.4. `project_members`（プロジェクトメンバー）
| カラム名（物理名） | 論理名 | データ型 | 制約 | 説明 |
|---|---|---|---|---|
| `id` | メンバーID | `BIGSERIAL` | PRIMARY KEY | 自動採番キー |
| `project_id` | プロジェクトID | `BIGINT` | NOT NULL, FK(`projects.id`) | 参照するプロジェクト |
| `user_id` | ユーザーID | `BIGINT` | NOT NULL, FK(`users.id`) | 参照するユーザー |
| `role_id` | ロールID | `BIGINT` | NOT NULL, FK(`roles.id`) | アサインされたプロジェクト内ロール |
| *ユニーク制約* | - | - | UNIQUE(`project_id`, `user_id`, `role_id`) | 同一プロジェクト内重複アサインの防止 |

---

### 3.5. `trackers`（トラッカー）
| カラム名（物理名） | 論理名 | データ型 | 制約 | 説明 |
|---|---|---|---|---|
| `id` | トラッカーID | `BIGSERIAL` | PRIMARY KEY | 自動採番キー |
| `name` | トラッカー名 | `VARCHAR(50)` | NOT NULL, UNIQUE | バグ, 機能追加, タスク, サポート など |
| `position` | 表示順 | `INT` | DEFAULT 0 | 画面上の表示順序 |

---

### 3.6. `issue_statuses`（ステータス）
| カラム名（物理名） | 論理名 | データ型 | 制約 | 説明 |
|---|---|---|---|---|
| `id` | ステータスID | `BIGSERIAL` | PRIMARY KEY | 自動採番キー |
| `name` | ステータス名 | `VARCHAR(50)` | NOT NULL, UNIQUE | 新規, 進行中, 解決, 完了, 却下 など |
| `is_closed` | 完了フラグ | `BOOLEAN` | DEFAULT false | 該当ステータスが「終了状態」か否か |
| `position` | 表示順 | `INT` | DEFAULT 0 | 画面上の表示順序 |

---

### 3.7. `issues`（チケット）
| カラム名（物理名） | 論理名 | データ型 | 制約 | 説明 |
|---|---|---|---|---|
| `id` | チケットID | `BIGSERIAL` | PRIMARY KEY | 自動採番キー |
| `project_id` | プロジェクトID | `BIGINT` | NOT NULL, FK(`projects.id`) | 所属プロジェクト |
| `tracker_id` | トラッカーID | `BIGINT` | NOT NULL, FK(`trackers.id`) | チケット種別 |
| `status_id` | ステータスID | `BIGINT` | NOT NULL, FK(`issue_statuses.id`) | 現在のステータス |
| `author_id` | 作成者ID | `BIGINT` | NOT NULL, FK(`users.id`) | チケット作成ユーザー |
| `assignee_id` | 担当者ID | `BIGINT` | FK(`users.id`) | アサインされた担当ユーザー |
| `subject` | 題名 | `VARCHAR(255)` | NOT NULL | チケットのタイトル |
| `description` | 説明 | `TEXT` | - | 詳細内容（Markdown形式） |
| `priority` | 優先度 | `VARCHAR(20)` | DEFAULT 'NORMAL' | 低, 通常, 高, 緊急 |
| `start_date` | 開始日 | `DATE` | - | 着手予定日 |
| `due_date` | 期日 | `DATE` | - | 完了予定日 |
| `estimated_hours` | 予定工数 | `NUMERIC(5,2)` | - | 予定時間（単位：時間） |
| `created_at` | 作成日時 | `TIMESTAMP` | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 作成日時 |
| `updated_at` | 更新日時 | `TIMESTAMP` | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 最終更新日時 |

---

### 3.8. `journals`（変更履歴ヘッダー）
| カラム名（物理名） | 論理名 | データ型 | 制約 | 説明 |
|---|---|---|---|---|
| `id` | 履歴ID | `BIGSERIAL` | PRIMARY KEY | 自動採番キー |
| `issue_id` | チケットID | `BIGINT` | NOT NULL, FK(`issues.id`) | 対象のチケット |
| `user_id` | 更新者ID | `BIGINT` | NOT NULL, FK(`users.id`) | 変更を行ったユーザー |
| `notes` | コメント | `TEXT` | - | 変更時に投稿されたコメント |
| `created_at` | 投稿日時 | `TIMESTAMP` | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 履歴作成日時 |

---

### 3.9. `journal_details`（変更履歴明細）
| カラム名（物理名） | 論理名 | データ型 | 制約 | 説明 |
|---|---|---|---|---|
| `id` | 明細ID | `BIGSERIAL` | PRIMARY KEY | 自動採番キー |
| `journal_id` | 履歴ID | `BIGINT` | NOT NULL, FK(`journals.id`) | 親となる履歴ヘッダー |
| `property` | 変更属性 | `VARCHAR(50)` | NOT NULL | 変更対象項目（例: `status_id`, `assignee_id`） |
| `old_value` | 変更前データ | `TEXT` | - | 変更前の値 |
| `new_value` | 変更後データ | `TEXT` | - | 変更後の値 |

---

### 3.10. `workflows`（ワークフロー制御）
| カラム名（物理名） | 論理名 | データ型 | 制約 | 説明 |
|---|---|---|---|---|
| `id` | ワークフローID | `BIGSERIAL` | PRIMARY KEY | 自動採番キー |
| `role_id` | ロールID | `BIGINT` | NOT NULL, FK(`roles.id`) | 対象ロール |
| `tracker_id` | トラッカーID | `BIGINT` | NOT NULL, FK(`trackers.id`) | 対象トラッカー |
| `old_status_id` | 変更前ステータス | `BIGINT` | NOT NULL, FK(`issue_statuses.id`) | 遷移元ステータス |
| `new_status_id` | 変更後ステータス | `BIGINT` | NOT NULL, FK(`issue_statuses.id`) | 遷移先ステータス |
| *ユニーク制約* | - | - | UNIQUE(`role_id`, `tracker_id`, `old_status_id`, `new_status_id`) | 重複定義の防止 |

---
