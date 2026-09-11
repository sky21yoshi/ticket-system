package com.example.ticketsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ticketsystem.dto.ProjectMemberRequest;
import com.example.ticketsystem.dto.ProjectMemberResponse;
import com.example.ticketsystem.dto.ProjectRequest;
import com.example.ticketsystem.dto.ProjectResponse;
import com.example.ticketsystem.entity.Project;
import com.example.ticketsystem.entity.ProjectMember;
import com.example.ticketsystem.entity.Role;
import com.example.ticketsystem.entity.User;
import com.example.ticketsystem.repository.ProjectMemberRepository;
import com.example.ticketsystem.repository.ProjectRepository;
import com.example.ticketsystem.repository.RoleRepository;
import com.example.ticketsystem.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project testProject;
    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testProject = Project.builder()
                .id(1L)
                .name("テストプロジェクト")
                .identifier("test-proj")
                .description("説明文")
                .isPublic(true)
                .build();

        testUser = User.builder()
                .id(10L)
                .username("yamada")
                .fullName("山田太郎")
                .build();

        testRole = Role.builder()
                .id(2L)
                .name("開発者")
                .build();
    }

    @Test
    @DisplayName("F-201: プロジェクト新規作成が成功すること")
    void testCreateProject_Success() {
        ProjectRequest request = ProjectRequest.builder()
                .name("新プロジェクト")
                .identifier("new-proj")
                .description("詳細")
                .isPublic(true)
                .build();

        when(projectRepository.existsByIdentifier("new-proj")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            p.setId(2L);
            return p;
        });

        ProjectResponse response = projectService.create(request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("新プロジェクト", response.getName());
        assertEquals("new-proj", response.getIdentifier());
    }

    @Test
    @DisplayName("F-201: プロジェクト識別子が重複している場合は例外が発生すること")
    void testCreateProject_DuplicateIdentifier() {
        ProjectRequest request = ProjectRequest.builder()
                .name("新プロジェクト")
                .identifier("test-proj")
                .build();

        when(projectRepository.existsByIdentifier("test-proj")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> projectService.create(request));
    }

    @Test
    @DisplayName("F-201: プロジェクトメンバーの追加が成功すること")
    void testAddMember_Success() {
        ProjectMemberRequest request = ProjectMemberRequest.builder()
                .userId(10L)
                .roleId(2L)
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(userRepository.findById(10L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(testRole));
        when(projectMemberRepository.findByProjectIdAndUserId(1L, 10L)).thenReturn(Optional.empty());
        when(projectMemberRepository.save(any(ProjectMember.class))).thenAnswer(invocation -> {
            ProjectMember m = invocation.getArgument(0);
            m.setId(100L);
            return m;
        });

        ProjectMemberResponse response = projectService.addOrUpdateMember(1L, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("yamada", response.getUsername());
        assertEquals("開発者", response.getRoleName());
    }

    @Test
    @DisplayName("F-201: プロジェクトメンバーの除外が成功すること")
    void testRemoveMember_Success() {
        ProjectMember member = ProjectMember.builder()
                .id(100L)
                .project(testProject)
                .user(testUser)
                .role(testRole)
                .build();

        when(projectMemberRepository.findByProjectIdAndUserId(1L, 10L)).thenReturn(Optional.of(member));

        boolean removed = projectService.removeMember(1L, 10L);

        assertTrue(removed);
        verify(projectMemberRepository).delete(member);
    }
}

