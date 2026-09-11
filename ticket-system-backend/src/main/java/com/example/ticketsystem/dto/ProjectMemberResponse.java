package com.example.ticketsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * プロジェクトメンバー情報レスポンス DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberResponse {

    /** メンバーシップID */
    private Long id;

    /** プロジェクトID */
    private Long projectId;

    /** ユーザーID */
    private Long userId;

    /** ユーザー名 */
    private String username;

    /** ユーザー氏名 */
    private String userFullName;

    /** ロールID */
    private Long roleId;

    /** ロール名 */
    private String roleName;
}

