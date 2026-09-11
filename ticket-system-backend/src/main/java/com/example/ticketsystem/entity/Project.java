package com.example.ticketsystem.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * プロジェクトエンティティ
 * projects テーブルに対応（チケットやメンバーを管理する最上位単位）
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    /**
     * プロジェクトID (主キー / 自動採番)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * プロジェクト名
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * プロジェクト識別子 (一意な英数字記号、URL等で使用)
     */
    @Column(name = "identifier", nullable = false, unique = true, length = 50)
    private String identifier;

    /**
     * プロジェクトの説明
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 公開フラグ (true: 全ユーザー参照可能, false: メンバーのみ参照可能)
     */
    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = true;

    /**
     * レコード作成日時
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * レコード更新日時
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
