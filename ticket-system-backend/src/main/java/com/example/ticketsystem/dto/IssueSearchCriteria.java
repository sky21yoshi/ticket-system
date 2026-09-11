package com.example.ticketsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * チケット検索・絞り込み条件 DTO
 * GET /api/issues のクエリパラメータを受け取ります。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueSearchCriteria {

    /** プロジェクトIDでの絞り込み */
    private Long projectId;

    /** トラッカーIDでの絞り込み */
    private Long trackerId;

    /** ステータスIDでの絞り込み */
    private Long statusId;

    /** 担当者ユーザーIDでの絞り込み */
    private Long assigneeId;

    /** 作成者ユーザーIDでの絞り込み */
    private Long authorId;

    /** キーワード検索（題名または説明に対する部分一致） */
    private String keyword;

    /** 完了チケットを含むか (nullまたはfalseの場合は未完了のみ取得などの制御用) */
    private Boolean includeClosed;
}

