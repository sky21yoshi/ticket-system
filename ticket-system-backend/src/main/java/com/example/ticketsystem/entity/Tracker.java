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
 * トラッカーエンティティ
 * trackers テーブルに対応（チケットの種別: バグ、機能追加、タスク等）
 */
@Entity
@Table(name = "trackers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tracker {

    /**
     * トラッカーID (主キー / 自動採番)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * トラッカー名 (一意)
     */
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 表示順序
     */
    @Column(name = "position", nullable = false)
    @Builder.Default
    private Integer position = 0;
}