package com.example.ticketsystem.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketsystem.dto.IssueCreateRequest;
import com.example.ticketsystem.dto.IssueResponse;
import com.example.ticketsystem.dto.IssueSearchCriteria;
import com.example.ticketsystem.dto.IssueUpdateRequest;
import com.example.ticketsystem.exception.ResourceNotFoundException;
import com.example.ticketsystem.service.IssueService;

import jakarta.validation.Valid;

/**
 * チケット（Issue）REST API コントローラー
 * Base URL: /api/issues
 */
@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    /**
     * チケット一覧・絞り込み検索 API (F-303)
     * GET /api/issues
     * 
     * クエリパラメータでプロジェクトID、トラッカーID、ステータスID、担当者、作成者、キーワードを指定可能。
     * 
     * @param projectId プロジェクトID
     * @param trackerId トラッカーID
     * @param statusId ステータスID
     * @param assigneeId 担当者ID
     * @param authorId 作成者ID
     * @param keyword キーワード
     * @param includeClosed 完了チケットを含むか
     * @return チケットレスポンスリスト
     */
    @GetMapping
    public ResponseEntity<List<IssueResponse>> searchIssues(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long trackerId,
            @RequestParam(required = false) Long statusId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean includeClosed) {

        IssueSearchCriteria criteria = IssueSearchCriteria.builder()
                .projectId(projectId)
                .trackerId(trackerId)
                .statusId(statusId)
                .assigneeId(assigneeId)
                .authorId(authorId)
                .keyword(keyword)
                .includeClosed(includeClosed)
                .build();

        List<IssueResponse> issues = issueService.search(criteria);
        return ResponseEntity.ok(issues);
    }

    /**
     * チケット詳細取得 API（変更履歴含む）
     * GET /api/issues/{id}
     * 
     * @param id チケットID
     * @return チケット詳細レスポンス
     */
    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable Long id) {
        IssueResponse issue = issueService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("チケットが見つかりません: ID=" + id));
        return ResponseEntity.ok(issue);
    }

    /**
     * チケット新規作成 API (F-301)
     * POST /api/issues
     * 
     * @param request 新規作成リクエストDTO
     * @return 作成されたチケットレスポンス (201 Created)
     */
    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(@Valid @RequestBody IssueCreateRequest request) {
        IssueResponse created = issueService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * チケット更新 API (F-302, F-401)
     * PUT /api/issues/{id}
     * 
     * 属性値の更新、ステータス遷移のワークフロー検証、変更前後の履歴 (Journal) 自動記録を行います。
     * 
     * @param id チケットID
     * @param request 更新リクエストDTO
     * @return 更新後のチケット詳細レスポンス
     */
    @PutMapping("/{id}")
    public ResponseEntity<IssueResponse> updateIssue(
            @PathVariable Long id,
            @Valid @RequestBody IssueUpdateRequest request) {
        IssueResponse updated = issueService.update(id, request)
                .orElseThrow(() -> new ResourceNotFoundException("チケットが見つかりません: ID=" + id));
        return ResponseEntity.ok(updated);
    }

    /**
     * チケット削除 API
     * DELETE /api/issues/{id}
     * 
     * @param id チケットID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        boolean deleted = issueService.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException("チケットが見つかりません: ID=" + id);
        }
        return ResponseEntity.noContent().build();
    }
}