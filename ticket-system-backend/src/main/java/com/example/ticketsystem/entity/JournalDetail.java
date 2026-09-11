package com.example.ticketsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 変更履歴明細エンティティ
 * journal_details テーブルに対応（チケット変更時のカラム単位での変更前後の値）
 */
@Entity
@Table(name = "journal_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalDetail {

    /**
     * 明細ID (主キー / 自動採番)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 親となる履歴ヘッダー
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", nullable = false)
    private Journal journal;

    /**
     * 変更対象プロパティ名（例: subject, status_id, assignee_id 等）
     */
    @Column(name = "property", nullable = false, length = 50)
    private String property;

    /**
     * 変更前の値（文字列表現）
     */
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    /**
     * 変更後の値（文字列表現）
     */
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;
}

