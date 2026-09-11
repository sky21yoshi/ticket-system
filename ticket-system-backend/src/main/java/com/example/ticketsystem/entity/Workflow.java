package com.example.ticketsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ワークフロー制御エンティティ
 * workflows テーブルに対応（ロール・トラッカーごとのステータス遷移許可定義）
 */
@Entity
@Table(
    name = "workflows",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_workflow_rule", columnNames = {"role_id", "tracker_id", "old_status_id", "new_status_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workflow {

    /**
     * ワークフローID (主キー / 自動採番)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 対象ロール
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /**
     * 対象トラッカー
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracker_id", nullable = false)
    private Tracker tracker;

    /**
     * 遷移元（変更前）ステータス
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_status_id", nullable = false)
    private IssueStatus oldStatus;

    /**
     * 遷移先（変更後）ステータス
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_status_id", nullable = false)
    private IssueStatus newStatus;
}

