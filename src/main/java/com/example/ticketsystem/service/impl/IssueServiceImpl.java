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
 */
@Service
@Transactional
public class IssueServiceImpl implements IssueService {

    // 各データソースにアクセスするための Repository を注入
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
     * 読み取り専用トランザクションで最適化します。
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
     * チケットの新規登録
     */
    @Override
    public IssueResponseDto create(IssueRequestDto dto) {
        Issue issue = new Issue();
        // DTOからEntityへフィールド値をマッピング（関連IDのチェック含む）
        mapDtoToEntity(dto, issue);
        
        Issue savedIssue = issueRepository.save(issue);
        return convertToDto(savedIssue);
    }

    /**
     * チケットの更新
     */
    @Override
    public Optional<IssueResponseDto> update(Long id, IssueRequestDto dto) {
        return issueRepository.findById(id).map(existingIssue -> {
            // 既存のEntityインスタンスの値を書き換え
            mapDtoToEntity(dto, existingIssue);
            Issue updatedIssue = issueRepository.save(existingIssue);
            return convertToDto(updatedIssue);
        });
    }

    /**
     * チケットの削除
     */
    @Override
    public boolean deleteById(Long id) {
        if (issueRepository.existsById(id)) {
            issueRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ==========================================
    // ヘルパーメソッド (マッピング & バリデーション)
    // ==========================================

    /**
     * リクエスト DTO から Entity へ値を設定します。
     * ID参照が必要な関連エンティティの有無を検証し、存在しない場合は例外をスローします。
     */
    private void mapDtoToEntity(IssueRequestDto dto, Issue issue) {
        // 基本フィールドの設定
        issue.setSubject(dto.subject());
        issue.setDescription(dto.description());
        issue.setPriority(dto.priority());
        issue.setStartDate(dto.startDate());
        issue.setDueDate(dto.dueDate());
        issue.setEstimatedHours(dto.estimatedHours());

        // 必須: プロジェクトの存在チェックと紐付け
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
            issue.setAssignee(null); // 担当者なしを許容
        }
    }

    /**
     * Entity のデータを レスポンス DTO へ詰め替えます。
     * Null Safe に関連オブジェクトの表示用名称を取り出します。
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