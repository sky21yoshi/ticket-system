package com.example.ticketsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * プロジェクト（Project）作成・更新リクエスト DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequest {

    /**
     * プロジェクト名（必須・最大100文字）
     */
    @NotBlank(message = "プロジェクト名は必須です")
    @Size(max = 100, message = "プロジェクト名は100文字以内で入力してください")
    private String name;

    /**
     * プロジェクト識別子（必須・最大50文字・英数字およびハイフン/アンダースコア）
     */
    @NotBlank(message = "プロジェクト識別子は必須です")
    @Size(max = 50, message = "プロジェクト識別子は50文字以内で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "識別子は半角英数字、ハイフン、アンダースコアのみ使用可能です")
    private String identifier;

    /**
     * プロジェクト詳細説明
     */
    private String description;

    /**
     * 公開フラグ（true: 公開, false: 非公開 / デフォルト: true）
     */
    @Builder.Default
    private Boolean isPublic = true;
}