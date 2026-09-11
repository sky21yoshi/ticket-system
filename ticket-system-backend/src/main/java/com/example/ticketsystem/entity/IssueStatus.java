package com.example.ticketsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * チケットステータスエンティティ
 * issue_statuses テーブルに対応（チケットの状態: 新規、進行中、解決、完了、却下等）
 */
@Entity
@Table(name = "issue_statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueStatus {

    /**
     * ステータスID (主キー / 自動採番)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ステータス名 (一意)
     */
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 完了フラグ (該当ステータスが終了状態か否か)
     */
    @Column(name = "is_closed", nullable = false)
    @Builder.Default
    private Boolean isClosed = false;

    /**
     * 表示順序
     */
    @Column(name = "position", nullable = false)
    @Builder.Default
    private Integer position = 0;
}