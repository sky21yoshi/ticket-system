package com.example.ticketsystem.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 変更履歴ヘッダーレスポンス DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalResponse {

    /** 履歴ID */
    private Long id;

    /** 更新ユーザーID */
    private Long userId;

    /** 更新ユーザー名 */
    private String username;

    /** 更新ユーザー氏名 */
    private String userFullName;

    /** 変更時コメント */
    private String notes;

    /** 履歴作成日時 */
    private LocalDateTime createdAt;

    /** 変更明細リスト */
    private List<JournalDetailResponse> details;
}

