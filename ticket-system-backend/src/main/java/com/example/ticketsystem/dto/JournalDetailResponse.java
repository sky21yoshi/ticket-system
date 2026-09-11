package com.example.ticketsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 変更履歴明細レスポンス DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalDetailResponse {

    /** 明細ID */
    private Long id;

    /** 変更対象プロパティ名（例: subject, status, assignee 等） */
    private String property;

    /** 変更前の値 */
    private String oldValue;

    /** 変更後の値 */
    private String newValue;
}

