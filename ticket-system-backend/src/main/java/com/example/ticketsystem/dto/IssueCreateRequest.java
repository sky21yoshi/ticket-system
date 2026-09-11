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
 * チケット（Issue）新規作成リクエスト DTO
 * POST /api/issues で送信されてくる JSON を受け取ります。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueCreateRequest {

    /**
     * 所属プロジェクトID (必須)
     */
    @NotNull(message = "プロジェクトIDは必須です")
    private Long projectId;

    /**
     * トラッカーID (必須: バグ、機能追加、タスク等)
     */
    @NotNull(message = "トラッカーIDは必須です")
    private Long trackerId;

    /**
     * ステータスID (任意、未指定時はデフォルト「新規」)
     */
    private Long statusId;

    /**
     * 作成者ユーザーID (必須)
     */
    @NotNull(message = "作成者ユーザーIDは必須です")
    private Long authorId;

    /**
     * 担当者ユーザーID (任意)
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
     * 優先度（低, 通常, 高, 緊急 / デフォルト: NORMAL）
     */
    @Builder.Default
    private String priority = "NORMAL";

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
}
