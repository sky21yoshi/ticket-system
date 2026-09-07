package com.example.ticketsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IssueDto {

    // 作成・更新リクエスト用 Record
    public record CreateRequest(
        @NotNull Long projectId,
        @NotNull Long trackerId,
        @NotNull Long statusId,
        @NotNull Long authorId,
        Long assigneeId,
        @NotBlank String subject,
        String description,
        String priority,
        LocalDate startDate,
        LocalDate dueDate,
        BigDecimal estimatedHours
    ) {}

    // レスポンス用 Record
    public record Response(
        Long id,
        Long projectId,
        String projectName,
        String trackerName,
        String statusName,
        String authorName,
        String assigneeName,
        String subject,
        String description,
        String priority,
        LocalDate startDate,
        LocalDate dueDate,
        BigDecimal estimatedHours,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}
}