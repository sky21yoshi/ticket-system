package com.example.ticketsystem.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

/**
 * APIエラー発生時に統一された形式で返却するための DTO
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null のフィールドは JSON に含めない設定
public class ErrorResponse {

    /** 発生タイムスタンプ */
    private LocalDateTime timestamp;

    /** HTTP ステータスコード（例: 400, 404, 500） */
    private int status;

    /** エラー種別（例: "Not Found", "Bad Request"） */
    private String error;

    /** エラーメッセージ（例: "指定されたIDの課題が見つかりません"） */
    private String message;

    /** フィールドごとのバリデーションエラー詳細（@Valid エラー時のみ使用） */
    private Map<String, String> fieldErrors;
}
