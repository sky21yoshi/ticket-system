package com.example.ticketsystem.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * チケット（Issue）エンティティ
 * 
 * システムの中心となるチケット情報を保持し、プロジェクト、トラッカー、
 * ステータス、ユーザー（作成者・担当者）との多対一リレーションを構築します。
 */
@Entity
@Table(name = "issues")
@Getter
@Setter
@NoArgsConstructor
public class Issue {

    /** チケットID (主キー・自動採番) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 属するプロジェクト (多対一 / 必須) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /** 種別・トラッカー (多対一 / 任意) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracker_id")
    private Tracker tracker;

    /** ステータス (多対一 / 任意) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private IssueStatus status;

    /** 作成者 (多対一 / 任意) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    /** 担当者 (多対一 / 任意・未割り当て可) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    /** 件名 (必須・最大255文字) */
    @Column(nullable = false, length = 255)
    private String subject;

    /** 詳細説明 (TEXT型) */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 優先度 (例: Low, Normal, High) */
    @Column(length = 50)
    private String priority;

    /** 開始予定日 */
    @Column(name = "start_date")
    private LocalDate startDate;

    /** 期日 */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /** 予定工数 (時間単位 / 丸め誤差を防ぐため BigDecimal 型を採用) */
    @Column(name = "estimated_hours", precision = 5, scale = 2)
    private BigDecimal estimatedHours;

    /** レコード作成日時 (自動設定) */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** レコード最終更新日時 (自動設定) */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
