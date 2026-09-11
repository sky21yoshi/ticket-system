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
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketsystem.dto.ProjectMemberRequest;
import com.example.ticketsystem.dto.ProjectMemberResponse;
import com.example.ticketsystem.dto.ProjectRequest;
import com.example.ticketsystem.dto.ProjectResponse;
import com.example.ticketsystem.exception.ResourceNotFoundException;
import com.example.ticketsystem.service.ProjectService;

import jakarta.validation.Valid;

/**
 * プロジェクト（Project）REST API コントローラー
 * Base URL: /api/projects
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * プロジェクト一覧取得 API
     * GET /api/projects
     * 
     * @return プロジェクトレスポンスリスト
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.findAll();
        return ResponseEntity.ok(projects);
    }

    /**
     * ID指定 プロジェクト詳細取得 API
     * GET /api/projects/{id}
     * 
     * @param id プロジェクトID
     * @return プロジェクト詳細レスポンス
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse project = projectService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("プロジェクトが見つかりません: ID=" + id));
        return ResponseEntity.ok(project);
    }

    /**
     * 識別子指定 プロジェクト詳細取得 API
     * GET /api/projects/identifier/{identifier}
     * 
     * @param identifier プロジェクト識別子
     * @return プロジェクト詳細レスポンス
     */
    @GetMapping("/identifier/{identifier}")
    public ResponseEntity<ProjectResponse> getProjectByIdentifier(@PathVariable String identifier) {
        ProjectResponse project = projectService.findByIdentifier(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("プロジェクトが見つかりません: 識別子=" + identifier));
        return ResponseEntity.ok(project);
    }

    /**
     * 新規プロジェクト作成 API (F-201)
     * POST /api/projects
     * 
     * @param request プロジェクト作成リクエスト
     * @return 作成されたプロジェクト詳細 (201 Created)
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse created = projectService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * プロジェクト更新 API
     * PUT /api/projects/{id}
     * 
     * @param id プロジェクトID
     * @param request プロジェクト更新リクエスト
     * @return 更新されたプロジェクト詳細
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        ProjectResponse updated = projectService.update(id, request)
                .orElseThrow(() -> new ResourceNotFoundException("プロジェクトが見つかりません: ID=" + id));
        return ResponseEntity.ok(updated);
    }

    /**
     * プロジェクト削除 API
     * DELETE /api/projects/{id}
     * 
     * @param id プロジェクトID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        boolean deleted = projectService.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException("プロジェクトが見つかりません: ID=" + id);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * プロジェクトメンバー一覧取得 API
     * GET /api/projects/{id}/members
     * 
     * @param id プロジェクトID
     * @return メンバーレスポンスリスト
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<ProjectMemberResponse>> getProjectMembers(@PathVariable Long id) {
        List<ProjectMemberResponse> members = projectService.getMembers(id);
        return ResponseEntity.ok(members);
    }

    /**
     * プロジェクトメンバー追加・ロール設定 API
     * POST /api/projects/{id}/members
     * 
     * @param id プロジェクトID
     * @param request メンバー追加リクエスト
     * @return 登録されたメンバー情報 (201 Created)
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<ProjectMemberResponse> addMember(
            @PathVariable Long id,
            @Valid @RequestBody ProjectMemberRequest request) {
        ProjectMemberResponse member = projectService.addOrUpdateMember(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    /**
     * プロジェクトメンバー除外 API
     * DELETE /api/projects/{id}/members/{userId}
     * 
     * @param id プロジェクトID
     * @param userId ユーザーID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long id,
            @PathVariable Long userId) {
        boolean removed = projectService.removeMember(id, userId);
        if (!removed) {
            throw new ResourceNotFoundException("メンバーシップが見つかりません: プロジェクトID=" + id + ", ユーザーID=" + userId);
        }
        return ResponseEntity.noContent().build();
    }
}