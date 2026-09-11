package com.example.ticketsystem.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketsystem.dto.ProjectMemberRequest;
import com.example.ticketsystem.dto.ProjectMemberResponse;
import com.example.ticketsystem.dto.ProjectRequest;
import com.example.ticketsystem.dto.ProjectResponse;
import com.example.ticketsystem.entity.Project;
import com.example.ticketsystem.entity.ProjectMember;
import com.example.ticketsystem.entity.Role;
import com.example.ticketsystem.entity.User;
import com.example.ticketsystem.exception.ResourceNotFoundException;
import com.example.ticketsystem.repository.ProjectMemberRepository;
import com.example.ticketsystem.repository.ProjectRepository;
import com.example.ticketsystem.repository.RoleRepository;
import com.example.ticketsystem.repository.UserRepository;
import com.example.ticketsystem.service.ProjectService;

/**
 * プロジェクト管理 サービス実装クラス
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public ProjectServiceImpl(
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            UserRepository userRepository,
            RoleRepository roleRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectResponse> findById(Long id) {
        return projectRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectResponse> findByIdentifier(String identifier) {
        return projectRepository.findByIdentifier(identifier)
                .map(this::convertToResponse);
    }

    @Override
    public ProjectResponse create(ProjectRequest request) {
        if (projectRepository.existsByIdentifier(request.getIdentifier())) {
            throw new IllegalArgumentException("指定されたプロジェクト識別子は既に使用されています: " + request.getIdentifier());
        }

        Project project = Project.builder()
                .name(request.getName())
                .identifier(request.getIdentifier())
                .description(request.getDescription())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .build();

        Project saved = projectRepository.save(project);
        return convertToResponse(saved);
    }

    @Override
    public Optional<ProjectResponse> update(Long id, ProjectRequest request) {
        return projectRepository.findById(id).map(existingProject -> {
            if (!existingProject.getIdentifier().equals(request.getIdentifier())) {
                if (projectRepository.existsByIdentifier(request.getIdentifier())) {
                    throw new IllegalArgumentException("指定されたプロジェクト識別子は既に使用されています: " + request.getIdentifier());
                }
                existingProject.setIdentifier(request.getIdentifier());
            }

            existingProject.setName(request.getName());
            existingProject.setDescription(request.getDescription());
            if (request.getIsPublic() != null) {
                existingProject.setIsPublic(request.getIsPublic());
            }

            Project saved = projectRepository.save(existingProject);
            return convertToResponse(saved);
        });
    }

    @Override
    public boolean deleteById(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getMembers(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("プロジェクトが見つかりません: ID=" + projectId);
        }

        return projectMemberRepository.findByProjectId(projectId).stream()
                .map(this::convertToMemberResponse)
                .toList();
    }

    @Override
    public ProjectMemberResponse addOrUpdateMember(Long projectId, ProjectMemberRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("プロジェクトが見つかりません: ID=" + projectId));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません: ID=" + request.getUserId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("ロールが見つかりません: ID=" + request.getRoleId()));

        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, user.getId())
                .orElseGet(() -> ProjectMember.builder()
                        .project(project)
                        .user(user)
                        .build());

        member.setRole(role);
        ProjectMember saved = projectMemberRepository.save(member);
        return convertToMemberResponse(saved);
    }

    @Override
    public boolean removeMember(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .map(member -> {
                    projectMemberRepository.delete(member);
                    return true;
                }).orElse(false);
    }

    private ProjectResponse convertToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .identifier(project.getIdentifier())
                .description(project.getDescription())
                .isPublic(project.getIsPublic())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    private ProjectMemberResponse convertToMemberResponse(ProjectMember member) {
        return ProjectMemberResponse.builder()
                .id(member.getId())
                .projectId(member.getProject().getId())
                .userId(member.getUser().getId())
                .username(member.getUser().getUsername())
                .userFullName(member.getUser().getFullName())
                .roleId(member.getRole().getId())
                .roleName(member.getRole().getName())
                .build();
    }
}
