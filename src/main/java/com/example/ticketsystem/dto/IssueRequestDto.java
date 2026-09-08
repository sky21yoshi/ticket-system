package com.example.ticketsystem.dto;

import java.math.BigDecimal; // Precision保持のため Double から BigDecimal へ変更
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * チケット作成・更新リクエスト用 DTO (Record)
 * 
 * クライアントからの JSON リクエストを受け取り、バリデーションを実施します。
 * エンティティ層 (Issue) の型定義に合わせ、予定工数は BigDecimal を採用しています。
 */
public record IssueRequestDto(

        // --- 関連エンティティのID参照 ---

        @NotNull(message = "プロジェクトIDは必須です")
        Long projectId,     // 紐付くプロジェクトのID (必須)

        Long trackerId,     // トラッカーID (バグ、機能要望など)

        Long statusId,      // ステータスID (新規、進行中、解決など)

        Long authorId,      // 作成者のユーザーID

        Long assigneeId,    // 担当者のユーザーID (未割り当ての場合は null を許容)


        // --- チケット基本情報 ---

        @NotBlank(message = "件名は必須です")
        @Size(max = 255, message = "件名は255文字以内で入力してください")
        String subject,     // チケットの件名 (必須・最大255文字)

        String description, // 詳細説明テキスト

        String priority,    // 優先度 (例: "Low", "Normal", "High", "Urgent")


        // --- スケジュール・作業時間 ---

        LocalDate startDate, // 開始予定日 (YYYY-MM-DD)

        LocalDate dueDate,   // 期日 (YYYY-MM-DD)

        // 浮動小数点数の丸め誤差を防ぎ、JPA Entity (Issue) の型と一致させるため BigDecimal に統一
        BigDecimal estimatedHours // 予定工数 (時間単位)
) {}
