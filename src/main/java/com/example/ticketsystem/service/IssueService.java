package com.example.ticketsystem.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketsystem.dto.IssueDto;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final TrackerRepository trackerRepository;
    private final IssueStatusRepository statusRepository;
    private final UserRepository userRepository;

    public List<IssueDto.Response> findAll() {
        return issueRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public IssueDto.Response findById(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found: " + id));
        return toResponse(issue);
    }

    @Transactional
    public IssueDto.Response create(IssueDto.CreateRequest req) {
        Project project = projectRepository.findById(req.projectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        Tracker tracker = trackerRepository.findById(req.trackerId())
                .orElseThrow(() -> new EntityNotFoundException("Tracker not found"));
        IssueStatus status = statusRepository.findById(req.statusId())
                .orElseThrow(() -> new EntityNotFoundException("Status not found"));
        User author = userRepository.findById(req.authorId())
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        User assignee = req.assigneeId() != null ?
                userRepository.findById(req.assigneeId()).orElse(null) : null;

        Issue issue = new Issue();
        issue.setProject(project);
        issue.setTracker(tracker);
        issue.setStatus(status);
        issue.setAuthor(author);
        issue.setAssignee(assignee);
        issue.setSubject(req.subject());
        issue.setDescription(req.description());
        if (req.priority() != null) issue.setPriority(req.priority());
        issue.setStartDate(req.startDate());
        issue.setDueDate(req.dueDate());
        issue.setEstimatedHours(req.estimatedHours());

        return toResponse(issueRepository.save(issue));
    }

    @Transactional
    public void delete(Long id) {
        if (!issueRepository.existsById(id)) {
            throw new EntityNotFoundException("Issue not found: " + id);
        }
        issueRepository.deleteById(id);
    }

    private IssueDto.Response toResponse(Issue issue) {
        return new IssueDto.Response(
                issue.getId(),
                issue.getProject().getId(),
                issue.getProject().getName(),
                issue.getTracker().getName(),
                issue.getStatus().getName(),
                issue.getAuthor().getFullName(),
                issue.getAssignee() != null ? issue.getAssignee().getFullName() : null,
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