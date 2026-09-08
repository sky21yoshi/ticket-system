package com.example.ticketsystem.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketsystem.dto.IssueRequestDto;
import com.example.ticketsystem.dto.IssueResponseDto;
import com.example.ticketsystem.entity.Issue;
import com.example.ticketsystem.entity.IssueStatus;
import com.example.ticketsystem.entity.Project;
import com.example.ticketsystem.entity.Tracker;
import com.example.ticketsystem.entity.User;
import com.example.ticketsystem.repository.IssueRepository;
import com.example.ticketsystem.repository.IssueStatusRepository;
import com.example.ticketsystem.repository.ProjectRepository;
import com.example.ticketsystem.repository.TrackerRepository;
import com.example.ticketsystem.repository.UserRepository;
import com.example.ticketsystem.service.IssueService;

/**
 * チケット管理 サービス実装クラス
 * 
 * JPA Repository を介したデータベース操作、トランザクション境界制御、
 * および DTO ↔ Entity 相互変換ロジックを担当します。
 */
@Service
@Transactional
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final TrackerRepository trackerRepository;
    private final IssueStatusRepository statusRepository;
    private final UserRepository userRepository;

    // コンストラクタインジェクション
    public IssueServiceImpl(
            IssueRepository issueRepository,
            ProjectRepository projectRepository,
            TrackerRepository trackerRepository,
            IssueStatusRepository statusRepository,
            UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.trackerRepository = trackerRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
    }

    /**
     * 全チケット取得
     */
    @Override
    @Transactional(readOnly = true)
    public List<IssueResponseDto> findAll() {
        return issueRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * ID指定による単一チケット取得
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<IssueResponseDto> findById(Long id) {
        return issueRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * 新規チケット作成
     */
    @Override
    public IssueResponseDto create(IssueRequestDto dto) {
        Issue issue = new Issue();
        mapDtoToEntity(dto, issue);
        
        Issue savedIssue = issueRepository.save(issue);
        return convertToDto(savedIssue);
    }

    /**
     * チケット更新
     */
    @Override
    public Optional<IssueResponseDto> update(Long id, IssueRequestDto dto) {
        return issueRepository.findById(id).map(existingIssue -> {
            mapDtoToEntity(dto, existingIssue);
            Issue updatedIssue = issueRepository.save(existingIssue);
            return convertToDto(updatedIssue);
        });
    }

    /**
     * チケット削除 (REST API用: 成否を boolean で返却)
     */
    @Override
    public boolean deleteById(Long id) {
        if (issueRepository.existsById(id)) {
            issueRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * チケット削除 (Web用: 存在しない場合は例外をスロー)
     */
    @Override
    public void delete(Long id) {
        if (!issueRepository.existsById(id)) {
            throw new IllegalArgumentException("Issue not found with id: " + id);
        }
        issueRepository.deleteById(id);
    }

    // ==========================================
    // ヘルパーメソッド (マッピング & バリデーション)
    // ==========================================

    /**
     * DTO から Entity へのフィールド値設定と、関連IDの存在チェックを行います。
     */
    private void mapDtoToEntity(IssueRequestDto dto, Issue issue) {
        issue.setSubject(dto.subject());
        issue.setDescription(dto.description());
        issue.setPriority(dto.priority());
        issue.setStartDate(dto.startDate());
        issue.setDueDate(dto.dueDate());
        issue.setEstimatedHours(dto.estimatedHours());

        // 必須: プロジェクトの存在チェック
        if (dto.projectId() != null) {
            Project project = projectRepository.findById(dto.projectId())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found. ID: " + dto.projectId()));
            issue.setProject(project);
        }

        // 任意: トラッカーの紐付け
        if (dto.trackerId() != null) {
            Tracker tracker = trackerRepository.findById(dto.trackerId())
                    .orElseThrow(() -> new IllegalArgumentException("Tracker not found. ID: " + dto.trackerId()));
            issue.setTracker(tracker);
        } else {
            issue.setTracker(null);
        }

        // 任意: ステータスの紐付け
        if (dto.statusId() != null) {
            IssueStatus status = statusRepository.findById(dto.statusId())
                    .orElseThrow(() -> new IllegalArgumentException("Status not found. ID: " + dto.statusId()));
            issue.setStatus(status);
        } else {
            issue.setStatus(null);
        }

        // 任意: 作成者の紐付け
        if (dto.authorId() != null) {
            User author = userRepository.findById(dto.authorId())
                    .orElseThrow(() -> new IllegalArgumentException("Author user not found. ID: " + dto.authorId()));
            issue.setAuthor(author);
        } else {
            issue.setAuthor(null);
        }

        // 任意: 担当者の紐付け
        if (dto.assigneeId() != null) {
            User assignee = userRepository.findById(dto.assigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee user not found. ID: " + dto.assigneeId()));
            issue.setAssignee(assignee);
        } else {
            issue.setAssignee(null);
        }
    }

    /**
     * Entity から レスポンス DTO へ変換します。
     */
    private IssueResponseDto convertToDto(Issue issue) {
        return new IssueResponseDto(
                issue.getId(),
                issue.getProject() != null ? issue.getProject().getId() : null,
                issue.getProject() != null ? issue.getProject().getName() : null,
                issue.getTracker() != null ? issue.getTracker().getName() : null,
                issue.getStatus() != null ? issue.getStatus().getName() : null,
                issue.getAuthor() != null ? issue.getAuthor().getUsername() : null,
                issue.getAssignee() != null ? issue.getAssignee().getUsername() : null,
                issue.getSubject(),
                issue.getDescription(),
                issue.getPriority(),
                issue.getStartDate(),
                issue.getDueDate(),
                issue.getEstimatedHours(),
                issue.getCreatedAt(),
                issue.getUpdatedAt()
        );
    }
}