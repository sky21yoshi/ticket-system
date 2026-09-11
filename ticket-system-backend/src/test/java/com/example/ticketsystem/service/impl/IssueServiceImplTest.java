package com.example.ticketsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ticketsystem.dto.IssueCreateRequest;
import com.example.ticketsystem.dto.IssueResponse;
import com.example.ticketsystem.dto.IssueUpdateRequest;
import com.example.ticketsystem.entity.Issue;
import com.example.ticketsystem.entity.IssueStatus;
import com.example.ticketsystem.entity.Journal;
import com.example.ticketsystem.entity.Project;
import com.example.ticketsystem.entity.ProjectMember;
import com.example.ticketsystem.entity.Role;
import com.example.ticketsystem.entity.Tracker;
import com.example.ticketsystem.entity.User;
import com.example.ticketsystem.exception.WorkflowViolationException;
import com.example.ticketsystem.repository.IssueRepository;
import com.example.ticketsystem.repository.IssueStatusRepository;
import com.example.ticketsystem.repository.JournalRepository;
import com.example.ticketsystem.repository.ProjectMemberRepository;
import com.example.ticketsystem.repository.ProjectRepository;
import com.example.ticketsystem.repository.TrackerRepository;
import com.example.ticketsystem.repository.UserRepository;
import com.example.ticketsystem.repository.WorkflowRepository;

@ExtendWith(MockitoExtension.class)
class IssueServiceImplTest {

    @Mock
    private IssueRepository issueRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TrackerRepository trackerRepository;
    @Mock
    private IssueStatusRepository statusRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;
    @Mock
    private WorkflowRepository workflowRepository;
    @Mock
    private JournalRepository journalRepository;

    @InjectMocks
    private IssueServiceImpl issueService;

    private Project testProject;
    private Tracker testTracker;
    private IssueStatus statusNew;
    private IssueStatus statusInProgress;
    private IssueStatus statusClosed;
    private User testAuthor;
    private User testAssignee;
    private Role testDeveloperRole;

    @BeforeEach
    void setUp() {
        testProject = Project.builder().id(1L).name("Test Project").identifier("test-proj").build();
        testTracker = Tracker.builder().id(1L).name("バグ").position(1).build();
        statusNew = IssueStatus.builder().id(1L).name("新規").isClosed(false).position(1).build();
        statusInProgress = IssueStatus.builder().id(2L).name("進行中").isClosed(false).position(2).build();
        statusClosed = IssueStatus.builder().id(3L).name("完了").isClosed(true).position(3).build();
        testAuthor = User.builder().id(1L).username("author").fullName("作成者").isAdmin(false).build();
        testAssignee = User.builder().id(2L).username("assignee").fullName("担当者").isAdmin(false).build();
        testDeveloperRole = Role.builder().id(2L).name("開発者").build();
    }

    @Test
    @DisplayName("F-301: チケット新規作成が成功すること")
    void testCreateIssue_Success() {
        IssueCreateRequest request = IssueCreateRequest.builder()
                .projectId(1L)
                .trackerId(1L)
                .authorId(1L)
                .subject("ログイン不具合の修正")
                .description("ログイン時にエラーが発生する")
                .priority("HIGH")
                .startDate(LocalDate.of(2026, 9, 1))
                .dueDate(LocalDate.of(2026, 9, 15))
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(trackerRepository.findById(1L)).thenReturn(Optional.of(testTracker));
        when(statusRepository.findByName("新規")).thenReturn(Optional.of(statusNew));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(issueRepository.save(any(Issue.class))).thenAnswer(invocation -> {
            Issue issue = invocation.getArgument(0);
            issue.setId(100L);
            return issue;
        });

        IssueResponse response = issueService.create(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("ログイン不具合の修正", response.getSubject());
        assertEquals("新規", response.getStatusName());
        assertEquals("バグ", response.getTrackerName());
    }

    @Test
    @DisplayName("F-301: 期日が開始日より前の場合は例外がスローされること")
    void testCreateIssue_InvalidDateRange() {
        IssueCreateRequest request = IssueCreateRequest.builder()
                .projectId(1L)
                .trackerId(1L)
                .authorId(1L)
                .subject("日付不正チケット")
                .startDate(LocalDate.of(2026, 9, 20))
                .dueDate(LocalDate.of(2026, 9, 10))
                .build();

        assertThrows(IllegalArgumentException.class, () -> issueService.create(request));
    }

    @Test
    @DisplayName("F-302: チケット更新時に変更差分とコメントが Journal に記録されること")
    void testUpdateIssue_CreatesJournal() {
        Issue existingIssue = Issue.builder()
                .id(10L)
                .project(testProject)
                .tracker(testTracker)
                .status(statusNew)
                .author(testAuthor)
                .assignee(null)
                .subject("旧タイトル")
                .description("旧詳細")
                .priority("NORMAL")
                .build();

        IssueUpdateRequest request = IssueUpdateRequest.builder()
                .updatedByUserId(1L)
                .subject("新タイトル")
                .description("旧詳細")
                .statusId(2L) // 新規 -> 進行中
                .assigneeId(2L)
                .priority("HIGH")
                .notes("着手します")
                .build();

        ProjectMember member = ProjectMember.builder()
                .project(testProject)
                .user(testAuthor)
                .role(testDeveloperRole)
                .build();

        when(issueRepository.findById(10L)).thenReturn(Optional.of(existingIssue));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testAssignee));
        when(statusRepository.findById(2L)).thenReturn(Optional.of(statusInProgress));
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 1L)).thenReturn(Optional.of(member));
        // 開発者ロールで新規(1) -> 進行中(2) への遷移は許可
        when(workflowRepository.existsByRoleIdAndTrackerIdAndOldStatusIdAndNewStatusId(2L, 1L, 1L, 2L)).thenReturn(true);
        when(issueRepository.save(any(Issue.class))).thenReturn(existingIssue);
        when(journalRepository.findByIssueIdOrderByCreatedAtAsc(10L)).thenReturn(Collections.emptyList());

        issueService.update(10L, request);

        // Journal が保存されたことを検証
        ArgumentCaptor<Journal> journalCaptor = ArgumentCaptor.forClass(Journal.class);
        verify(journalRepository).save(journalCaptor.capture());
        Journal savedJournal = journalCaptor.getValue();

        assertEquals("着手します", savedJournal.getNotes());
        // 差分（status_id, subject, priority, assignee_id）が記録されていること
        assertEquals(4, savedJournal.getDetails().size());
        assertTrue(savedJournal.getDetails().stream().anyMatch(d -> d.getProperty().equals("subject") && d.getOldValue().equals("旧タイトル") && d.getNewValue().equals("新タイトル")));
        assertTrue(savedJournal.getDetails().stream().anyMatch(d -> d.getProperty().equals("status_id") && d.getOldValue().equals("新規") && d.getNewValue().equals("進行中")));
    }

    @Test
    @DisplayName("F-401: ワークフローで許可されていないステータス遷移時に WorkflowViolationException が発生すること")
    void testUpdateIssue_WorkflowViolation() {
        Issue existingIssue = Issue.builder()
                .id(10L)
                .project(testProject)
                .tracker(testTracker)
                .status(statusNew)
                .author(testAuthor)
                .subject("ワークフローテスト")
                .build();

        IssueUpdateRequest request = IssueUpdateRequest.builder()
                .updatedByUserId(1L)
                .subject("ワークフローテスト")
                .statusId(3L) // 新規 -> 完了（未許可の遷移を想定）
                .build();

        ProjectMember member = ProjectMember.builder()
                .project(testProject)
                .user(testAuthor)
                .role(testDeveloperRole)
                .build();

        when(issueRepository.findById(10L)).thenReturn(Optional.of(existingIssue));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(statusRepository.findById(3L)).thenReturn(Optional.of(statusClosed));
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 1L)).thenReturn(Optional.of(member));
        // 開発者ロールで新規(1) -> 完了(3) への直接遷移は不許可
        when(workflowRepository.existsByRoleIdAndTrackerIdAndOldStatusIdAndNewStatusId(2L, 1L, 1L, 3L)).thenReturn(false);

        assertThrows(WorkflowViolationException.class, () -> issueService.update(10L, request));
    }
}

