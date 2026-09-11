package com.example.ticketsystem.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketsystem.dto.IssueCreateRequest;
import com.example.ticketsystem.dto.IssueResponse;
import com.example.ticketsystem.dto.IssueSearchCriteria;
import com.example.ticketsystem.dto.IssueUpdateRequest;
import com.example.ticketsystem.dto.JournalDetailResponse;
import com.example.ticketsystem.dto.JournalResponse;
import com.example.ticketsystem.entity.Issue;
import com.example.ticketsystem.entity.IssueStatus;
import com.example.ticketsystem.entity.Journal;
import com.example.ticketsystem.entity.JournalDetail;
import com.example.ticketsystem.entity.Project;
import com.example.ticketsystem.entity.ProjectMember;
import com.example.ticketsystem.entity.Tracker;
import com.example.ticketsystem.entity.User;
import com.example.ticketsystem.exception.ResourceNotFoundException;
import com.example.ticketsystem.exception.WorkflowViolationException;
import com.example.ticketsystem.repository.IssueRepository;
import com.example.ticketsystem.repository.IssueStatusRepository;
import com.example.ticketsystem.repository.JournalRepository;
import com.example.ticketsystem.repository.ProjectMemberRepository;
import com.example.ticketsystem.repository.ProjectRepository;
import com.example.ticketsystem.repository.TrackerRepository;
import com.example.ticketsystem.repository.UserRepository;
import com.example.ticketsystem.repository.WorkflowRepository;
import com.example.ticketsystem.service.IssueService;

import jakarta.persistence.criteria.Predicate;

/**
 * チケット管理 サービス実装クラス
 * 
 * チケットの作成、更新、変更履歴（Journal）自動記録、
 * ワークフロー制御（権限によるステータス遷移判定）、および動的絞り込み検索を担当します。
 */
@Service
@Transactional
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final TrackerRepository trackerRepository;
    private final IssueStatusRepository statusRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final WorkflowRepository workflowRepository;
    private final JournalRepository journalRepository;

    public IssueServiceImpl(
            IssueRepository issueRepository,
            ProjectRepository projectRepository,
            TrackerRepository trackerRepository,
            IssueStatusRepository statusRepository,
            UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository,
            WorkflowRepository workflowRepository,
            JournalRepository journalRepository) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.trackerRepository = trackerRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.workflowRepository = workflowRepository;
        this.journalRepository = journalRepository;
    }

    /**
     * 全チケット取得
     */
    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> findAll() {
        return issueRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * プロジェクトは以下の全チケットを取得
     */
    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> findByProjectId(Long projectId) {
        return issueRepository.findByProjectId(projectId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * チケット一覧・絞り込み検索 (F-303)
     */
    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> search(IssueSearchCriteria criteria) {
        Specification<Issue> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getProjectId() != null) {
                predicates.add(cb.equal(root.get("project").get("id"), criteria.getProjectId()));
            }
            if (criteria.getTrackerId() != null) {
                predicates.add(cb.equal(root.get("tracker").get("id"), criteria.getTrackerId()));
            }
            if (criteria.getStatusId() != null) {
                predicates.add(cb.equal(root.get("status").get("id"), criteria.getStatusId()));
            }
            if (criteria.getAssigneeId() != null) {
                predicates.add(cb.equal(root.get("assignee").get("id"), criteria.getAssigneeId()));
            }
            if (criteria.getAuthorId() != null) {
                predicates.add(cb.equal(root.get("author").get("id"), criteria.getAuthorId()));
            }
            if (criteria.getKeyword() != null && !criteria.getKeyword().isBlank()) {
                String pattern = "%" + criteria.getKeyword().trim() + "%";
                Predicate subjectMatch = cb.like(cb.lower(root.get("subject")), pattern.toLowerCase());
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern.toLowerCase());
                predicates.add(cb.or(subjectMatch, descMatch));
            }
            if (Boolean.FALSE.equals(criteria.getIncludeClosed())) {
                predicates.add(cb.isFalse(root.get("status").get("isClosed")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return issueRepository.findAll(spec).stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * ID指定によるチケット詳細取得（変更履歴を含む）
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<IssueResponse> findById(Long id) {
        return issueRepository.findById(id)
                .map(this::convertToResponseWithJournals);
    }

    /**
     * 新規チケット作成 (F-301)
     */
    @Override
    public IssueResponse create(IssueCreateRequest request) {
        validateDateRange(request.getStartDate(), request.getDueDate());

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("プロジェクトが見つかりません: ID=" + request.getProjectId()));

        Tracker tracker = trackerRepository.findById(request.getTrackerId())
                .orElseThrow(() -> new ResourceNotFoundException("トラッカーが見つかりません: ID=" + request.getTrackerId()));

        IssueStatus status;
        if (request.getStatusId() != null) {
            status = statusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("ステータスが見つかりません: ID=" + request.getStatusId()));
        } else {
            status = statusRepository.findByName("新規")
                    .orElseGet(() -> statusRepository.findAllByOrderByPositionAsc().stream().findFirst()
                            .orElseThrow(() -> new IllegalStateException("初期ステータスがマスタに存在しません")));
        }

        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("作成者ユーザーが見つかりません: ID=" + request.getAuthorId()));

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("担当者ユーザーが見つかりません: ID=" + request.getAssigneeId()));
        }

        Issue issue = Issue.builder()
                .project(project)
                .tracker(tracker)
                .status(status)
                .author(author)
                .assignee(assignee)
                .subject(request.getSubject())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : "NORMAL")
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .estimatedHours(request.getEstimatedHours())
                .build();

        Issue saved = issueRepository.save(issue);
        return convertToResponse(saved);
    }

    /**
     * チケット更新 & 変更履歴 (Journal) 自動記録 (F-302, F-401)
     */
    @Override
    public Optional<IssueResponse> update(Long id, IssueUpdateRequest request) {
        return issueRepository.findById(id).map(existingIssue -> {
            validateDateRange(request.getStartDate(), request.getDueDate());

            User updatingUser = userRepository.findById(request.getUpdatedByUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("更新操作ユーザーが見つかりません: ID=" + request.getUpdatedByUserId()));

            List<JournalDetail> details = new ArrayList<>();

            // 1. トラッカー変更の検出
            if (request.getTrackerId() != null && !Objects.equals(existingIssue.getTracker().getId(), request.getTrackerId())) {
                Tracker newTracker = trackerRepository.findById(request.getTrackerId())
                        .orElseThrow(() -> new ResourceNotFoundException("トラッカーが見つかりません: ID=" + request.getTrackerId()));
                addDetail(details, "tracker_id", existingIssue.getTracker().getName(), newTracker.getName());
                existingIssue.setTracker(newTracker);
            }

            // 2. ステータス変更の検証 (F-401 ワークフロー制御) と変更履歴
            if (request.getStatusId() != null && !Objects.equals(existingIssue.getStatus().getId(), request.getStatusId())) {
                IssueStatus newStatus = statusRepository.findById(request.getStatusId())
                        .orElseThrow(() -> new ResourceNotFoundException("ステータスが見つかりません: ID=" + request.getStatusId()));

                validateStatusTransition(updatingUser, existingIssue, newStatus);

                addDetail(details, "status_id", existingIssue.getStatus().getName(), newStatus.getName());
                existingIssue.setStatus(newStatus);
            }

            // 3. 題名 (subject) の変更検出
            if (!Objects.equals(existingIssue.getSubject(), request.getSubject())) {
                addDetail(details, "subject", existingIssue.getSubject(), request.getSubject());
                existingIssue.setSubject(request.getSubject());
            }

            // 4. 詳細説明 (description) の変更検出
            if (!Objects.equals(existingIssue.getDescription(), request.getDescription())) {
                addDetail(details, "description", existingIssue.getDescription(), request.getDescription());
                existingIssue.setDescription(request.getDescription());
            }

            // 5. 優先度 (priority) の変更検出
            if (request.getPriority() != null && !Objects.equals(existingIssue.getPriority(), request.getPriority())) {
                addDetail(details, "priority", existingIssue.getPriority(), request.getPriority());
                existingIssue.setPriority(request.getPriority());
            }

            // 6. 担当者 (assignee) の変更検出
            Long currentAssigneeId = existingIssue.getAssignee() != null ? existingIssue.getAssignee().getId() : null;
            if (!Objects.equals(currentAssigneeId, request.getAssigneeId())) {
                User newAssignee = null;
                String oldAssigneeName = existingIssue.getAssignee() != null ? existingIssue.getAssignee().getFullName() : "未設定";
                String newAssigneeName = "未設定";

                if (request.getAssigneeId() != null) {
                    newAssignee = userRepository.findById(request.getAssigneeId())
                            .orElseThrow(() -> new ResourceNotFoundException("担当者ユーザーが見つかりません: ID=" + request.getAssigneeId()));
                    newAssigneeName = newAssignee.getFullName();
                }

                addDetail(details, "assignee_id", oldAssigneeName, newAssigneeName);
                existingIssue.setAssignee(newAssignee);
            }

            // 7. 開始日 (startDate) の変更検出
            if (!Objects.equals(existingIssue.getStartDate(), request.getStartDate())) {
                addDetail(details, "start_date",
                        existingIssue.getStartDate() != null ? existingIssue.getStartDate().toString() : null,
                        request.getStartDate() != null ? request.getStartDate().toString() : null);
                existingIssue.setStartDate(request.getStartDate());
            }

            // 8. 期日 (dueDate) の変更検出
            if (!Objects.equals(existingIssue.getDueDate(), request.getDueDate())) {
                addDetail(details, "due_date",
                        existingIssue.getDueDate() != null ? existingIssue.getDueDate().toString() : null,
                        request.getDueDate() != null ? request.getDueDate().toString() : null);
                existingIssue.setDueDate(request.getDueDate());
            }

            // 9. 予定工数 (estimatedHours) の変更検出
            if (!Objects.equals(existingIssue.getEstimatedHours(), request.getEstimatedHours())) {
                addDetail(details, "estimated_hours",
                        existingIssue.getEstimatedHours() != null ? existingIssue.getEstimatedHours().toString() : null,
                        request.getEstimatedHours() != null ? request.getEstimatedHours().toString() : null);
                existingIssue.setEstimatedHours(request.getEstimatedHours());
            }

            // 10. 変更明細またはコメントがある場合、Journal (変更履歴) を作成
            boolean hasNotes = request.getNotes() != null && !request.getNotes().isBlank();
            if (!details.isEmpty() || hasNotes) {
                Journal journal = Journal.builder()
                        .issue(existingIssue)
                        .user(updatingUser)
                        .notes(hasNotes ? request.getNotes().trim() : null)
                        .build();

                for (JournalDetail detail : details) {
                    detail.setJournal(journal);
                }
                journal.setDetails(details);

                journalRepository.save(journal);
            }

            Issue updated = issueRepository.save(existingIssue);
            return convertToResponseWithJournals(updated);
        });
    }

    /**
     * チケット削除
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
    // 内部バリデーション & ヘルパーメソッド
    // ==========================================

    private void validateDateRange(LocalDate startDate, LocalDate dueDate) {
        if (startDate != null && dueDate != null && dueDate.isBefore(startDate)) {
            throw new IllegalArgumentException("期日には開始日以降の日付を指定してください: 開始日=" + startDate + ", 期日=" + dueDate);
        }
    }

    private void validateStatusTransition(User user, Issue issue, IssueStatus newStatus) {
        if (Boolean.TRUE.equals(user.getIsAdmin())) {
            return;
        }

        Long projectId = issue.getProject().getId();
        Optional<ProjectMember> memberOpt = projectMemberRepository.findByProjectIdAndUserId(projectId, user.getId());

        if (memberOpt.isEmpty()) {
            throw new WorkflowViolationException("プロジェクト「" + issue.getProject().getName() + "」のメンバーではないため、ステータスを変更できません");
        }

        Long roleId = memberOpt.get().getRole().getId();
        Long trackerId = issue.getTracker().getId();
        Long oldStatusId = issue.getStatus().getId();
        Long newStatusId = newStatus.getId();

        boolean isAllowed = workflowRepository.existsByRoleIdAndTrackerIdAndOldStatusIdAndNewStatusId(
                roleId, trackerId, oldStatusId, newStatusId);

        if (!isAllowed) {
            throw new WorkflowViolationException(String.format(
                    "ロール「%s」では、トラッカー「%s」においてステータス「%s」から「%s」への遷移は許可されていません",
                    memberOpt.get().getRole().getName(),
                    issue.getTracker().getName(),
                    issue.getStatus().getName(),
                    newStatus.getName()));
        }
    }

    private void addDetail(List<JournalDetail> list, String property, String oldValue, String newValue) {
        list.add(JournalDetail.builder()
                .property(property)
                .oldValue(oldValue)
                .newValue(newValue)
                .build());
    }

    private IssueResponse convertToResponse(Issue issue) {
        return IssueResponse.builder()
                .id(issue.getId())
                .projectId(issue.getProject() != null ? issue.getProject().getId() : null)
                .projectName(issue.getProject() != null ? issue.getProject().getName() : null)
                .projectIdentifier(issue.getProject() != null ? issue.getProject().getIdentifier() : null)
                .trackerId(issue.getTracker() != null ? issue.getTracker().getId() : null)
                .trackerName(issue.getTracker() != null ? issue.getTracker().getName() : null)
                .statusId(issue.getStatus() != null ? issue.getStatus().getId() : null)
                .statusName(issue.getStatus() != null ? issue.getStatus().getName() : null)
                .isClosed(issue.getStatus() != null ? issue.getStatus().getIsClosed() : false)
                .authorId(issue.getAuthor() != null ? issue.getAuthor().getId() : null)
                .authorUsername(issue.getAuthor() != null ? issue.getAuthor().getUsername() : null)
                .authorFullName(issue.getAuthor() != null ? issue.getAuthor().getFullName() : null)
                .assigneeId(issue.getAssignee() != null ? issue.getAssignee().getId() : null)
                .assigneeUsername(issue.getAssignee() != null ? issue.getAssignee().getUsername() : null)
                .assigneeFullName(issue.getAssignee() != null ? issue.getAssignee().getFullName() : null)
                .subject(issue.getSubject())
                .description(issue.getDescription())
                .priority(issue.getPriority())
                .startDate(issue.getStartDate())
                .dueDate(issue.getDueDate())
                .estimatedHours(issue.getEstimatedHours())
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .build();
    }

    private IssueResponse convertToResponseWithJournals(Issue issue) {
        IssueResponse response = convertToResponse(issue);

        List<Journal> journals = journalRepository.findByIssueIdOrderByCreatedAtAsc(issue.getId());
        List<JournalResponse> journalResponses = journals.stream().map(journal -> {
            List<JournalDetailResponse> detailResponses = journal.getDetails().stream().map(detail ->
                    JournalDetailResponse.builder()
                            .id(detail.getId())
                            .property(detail.getProperty())
                            .oldValue(detail.getOldValue())
                            .newValue(detail.getNewValue())
                            .build()
            ).toList();

            return JournalResponse.builder()
                    .id(journal.getId())
                    .userId(journal.getUser().getId())
                    .username(journal.getUser().getUsername())
                    .userFullName(journal.getUser().getFullName())
                    .notes(journal.getNotes())
                    .createdAt(journal.getCreatedAt())
                    .details(detailResponses)
                    .build();
        }).toList();

        response.setJournals(journalResponses);
        return response;
    }
}
