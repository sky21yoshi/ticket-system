package com.example.ticketsystem.dto;

import java.math.BigDecimal; // Issue Entity の BigDecimal 型とコンストラクタ引数を一致させるために追加
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * チケット情報レスポンス用 DTO (Record)
 * 
 * 画面表示やAPIクライアントでの利便性を高めるため、
 * IDだけでなく関連オブジェクトの名称 (プロジェクト名、担当者名など) も含めて返却します。
 */
public record IssueResponseDto(

        // --- 識別子 ---
        Long id,                // チケットID (主キー)


        // --- 関連エンティティの情報 ---
        Long projectId,         // プロジェクトID
        String projectName,     // プロジェクト名 (画面表示用)
        String trackerName,     // トラッカー名 (例: Bug, Feature)
        String statusName,      // ステータス表示名 (例: New, In Progress)
        String authorName,      // 作成者のユーザー名
        String assigneeName,    // 担当者のユーザー名 (未割り当て時は null)


        // --- チケット基本情報 ---
        String subject,         // 件名
        String description,     // 詳細説明
        String priority,        // 優先度


        // --- スケジュール・作業時間 ---
        LocalDate startDate,    // 開始予定日
        LocalDate dueDate,      // 期日

        // Issue Entity および IssueRequestDto と一致させた予定工数フィールド
        BigDecimal estimatedHours, // 予定工数 (時間単位)


        // --- システムメタデータ ---
        LocalDateTime createdAt, // 作成日時 (ISO-8601形式)
        LocalDateTime updatedAt  // 最終更新日時 (ISO-8601形式)
) {}
