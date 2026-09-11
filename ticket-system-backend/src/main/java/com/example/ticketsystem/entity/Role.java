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
 * ロール（権限）エンティティ
 * roles テーブルに対応（プロジェクト内における権限グループ情報）
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    /**
     * ロールID (主キー / 自動採番)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ロール名 (管理者, 開発者, 報告者, 閲覧者 など)
     */
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    /**
     * ロールの説明
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
