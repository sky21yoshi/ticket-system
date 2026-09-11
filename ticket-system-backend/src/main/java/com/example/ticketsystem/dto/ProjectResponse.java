package com.example.ticketsystem.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * プロジェクト（Project）情報レスポンス DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    /** プロジェクトID */
    private Long id;

    /** プロジェクト名 */
    private String name;

    /** プロジェクト識別子 */
    private String identifier;

    /** プロジェクト詳細説明 */
    private String description;

    /** 公開フラグ */
    private Boolean isPublic;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 最終更新日時 */
    private LocalDateTime updatedAt;
}