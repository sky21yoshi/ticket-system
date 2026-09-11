package com.example.ticketsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * チケット（Issue）情報レスポンス DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueResponse {

    /** チケットID */
    private Long id;

    /** 所属プロジェクトID */
    private Long projectId;

    /** 所属プロジェクト名 */
    private String projectName;

    /** 所属プロジェクト識別子 */
    private String projectIdentifier;

    /** トラッカーID */
    private Long trackerId;

    /** トラッカー名 (バグ, 機能追加, タスク等) */
    private String trackerName;

    /** ステータスID */
    private Long statusId;

    /** ステータス名 (新規, 進行中, 解決, 完了等) */
    private String statusName;

    /** 完了ステータスフラグ */
    private Boolean isClosed;

    /** 作成者ユーザーID */
    private Long authorId;

    /** 作成者ユーザー名 */
    private String authorUsername;

    /** 作成者氏名 */
    private String authorFullName;

    /** 担当者ユーザーID */
    private Long assigneeId;

    /** 担当者ユーザー名 */
    private String assigneeUsername;

    /** 担当者氏名 */
    private String assigneeFullName;

    /** 題名・タイトル */
    private String subject;

    /** 詳細説明（Markdown形式） */
    private String description;

    /** 優先度 */
    private String priority;

    /** 着手予定日 */
    private LocalDate startDate;

    /** 完了予定日（期日） */
    private LocalDate dueDate;

    /** 予定工数（時間） */
    private BigDecimal estimatedHours;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 最終更新日時 */
    private LocalDateTime updatedAt;

    /** 変更履歴リスト */
    private List<JournalResponse> journals;

    /**
     * フロントエンド互換用ゲッター（題名）
     */
    public String getTitle() {
        return this.subject;
    }
}
