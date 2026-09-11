package com.example.ticketsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * チケット（Issue）更新リクエスト DTO
 * PUT /api/issues/{id} で送信されてくる JSON を受け取ります。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueUpdateRequest {

    /**
     * 更新操作を行うユーザーID (必須: 権限判定および変更履歴記録用)
     */
    @NotNull(message = "更新者ユーザーIDは必須です")
    private Long updatedByUserId;

    /**
     * トラッカーID (任意)
     */
    private Long trackerId;

    /**
     * 変更後ステータスID (任意: ワークフロー判定対象)
     */
    private Long statusId;

    /**
     * 担当者ユーザーID (任意: null指定で担当者解除)
     */
    private Long assigneeId;

    /**
     * 題名・タイトル (必須・最大255文字)
     */
    @NotBlank(message = "題名は必須です")
    @Size(max = 255, message = "題名は255文字以内で入力してください")
    private String subject;

    /**
     * 詳細説明（Markdown形式）
     */
    private String description;

    /**
     * 優先度（低, 通常, 高, 緊急）
     */
    private String priority;

    /**
     * 着手予定日
     */
    private LocalDate startDate;

    /**
     * 完了予定日（期日）
     */
    private LocalDate dueDate;

    /**
     * 予定工数（単位: 時間）
     */
    @DecimalMin(value = "0.00", message = "予定工数は0以上である必要があります")
    @Digits(integer = 3, fraction = 2, message = "予定工数は整数部3桁、小数部2桁以内で指定してください")
    private BigDecimal estimatedHours;

    /**
     * 更新時のコメント (任意)
     */
    private String notes;
}
