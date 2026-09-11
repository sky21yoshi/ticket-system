package com.example.ticketsystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * プロジェクトメンバー追加・更新リクエスト DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberRequest {

    /** 参加させるユーザーID (必須) */
    @NotNull(message = "ユーザーIDは必須です")
    private Long userId;

    /** 割り当てるロールID (必須) */
    @NotNull(message = "ロールIDは必須です")
    private Long roleId;
}

