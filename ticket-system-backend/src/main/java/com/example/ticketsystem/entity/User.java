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
 * ユーザーエンティティ
 * users テーブルに対応（システム利用ユーザー情報）
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * ユーザーID (主キー / 自動採番)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ログイン用ユーザー名 (一意)
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 通知・ログイン用メールアドレス (一意)
     */
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * パスワードハッシュ (BCrypt等で暗号化)
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * 表示用氏名
     */
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    /**
     * システム管理者フラグ (全プロジェクトの閲覧・操作権限を持つか)
     */
    @Column(name = "is_admin", nullable = false)
    @Builder.Default
    private Boolean isAdmin = false;

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