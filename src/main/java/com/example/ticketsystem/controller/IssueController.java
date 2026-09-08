package com.example.ticketsystem.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketsystem.dto.IssueRequestDto;
import com.example.ticketsystem.dto.IssueResponseDto;
import com.example.ticketsystem.service.IssueService;

/**
 * チケット管理 REST API コントローラー
 * 
 * チケットの作成・取得・更新・削除（CRUD）リクエストをルーティングし、
 * IssueService を呼び出して結果を HTTP レスポンスとして返却します。
 */
@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    // コンストラクタインジェクション (推奨される DI パターン)
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    /**
     * チケット一覧取得 API
     * GET /api/issues
     * 
     * @return 全チケットのレスポンス DTO リスト (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<IssueResponseDto>> getAllIssues() {
        List<IssueResponseDto> issues = issueService.findAll();
        return ResponseEntity.ok(issues);
    }

    /**
     * チケット詳細取得 API
     * GET /api/issues/{id}
     * 
     * @param id 取得対象のチケットID
     * @return 存在する場合はチケット DTO (200 OK)、存在しない場合は 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseDto> getIssueById(@PathVariable Long id) {
        return issueService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * チケット新規作成 API
     * POST /api/issues
     * 
     * @param dto リクエストボディ (JSON)。@Valid により入力チェックを実施
     * @return 作成されたチケット DTO (201 Created)
     */
    @PostMapping
    public ResponseEntity<IssueResponseDto> createIssue(@Valid @RequestBody IssueRequestDto dto) {
        IssueResponseDto createdIssue = issueService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIssue);
    }

    /**
     * チケット更新 API
     * PUT /api/issues/{id}
     * 
     * @param id  更新対象のチケットID
     * @param dto 更新内容を含むリクエストボディ
     * @return 更新後のチケット DTO (200 OK)、対象が存在しない場合は 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<IssueResponseDto> updateIssue(
            @PathVariable Long id,
            @Valid @RequestBody IssueRequestDto dto) {

        return issueService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * チケット削除 API
     * DELETE /api/issues/{id}
     * 
     * @param id 削除対象のチケットID
     * @return 削除成功時は 204 No Content、対象が存在しない場合は 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        if (issueService.deleteById(id)) {
            // 削除成功時はボディなしの 204 を返す
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
