package com.example.ticketsystem.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ticketsystem.dto.IssueRequestDto;
import com.example.ticketsystem.dto.IssueResponseDto;
import com.example.ticketsystem.entity.IssueStatus;
import com.example.ticketsystem.entity.Tracker;
import com.example.ticketsystem.entity.User;
import com.example.ticketsystem.repository.IssueStatusRepository;
import com.example.ticketsystem.repository.ProjectRepository;
import com.example.ticketsystem.repository.TrackerRepository;
import com.example.ticketsystem.repository.UserRepository;
import com.example.ticketsystem.service.IssueService;

/**
 * チケット管理 Thymeleaf 画面表示用コントローラー
 */
@Controller
@RequestMapping("/issues")
public class IssueWebController {

    private final IssueService issueService;
    private final ProjectRepository projectRepository;
    private final TrackerRepository trackerRepository;
    private final IssueStatusRepository statusRepository;
    private final UserRepository userRepository;

    public IssueWebController(
            IssueService issueService,
            ProjectRepository projectRepository,
            TrackerRepository trackerRepository,
            IssueStatusRepository statusRepository,
            UserRepository userRepository) {
        this.issueService = issueService;
        this.projectRepository = projectRepository;
        this.trackerRepository = trackerRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
    }

    /**
     * チケット一覧画面
     * GET /issues
     */
    @GetMapping
    public String listIssues(Model model) {
        List<IssueResponseDto> issues = issueService.findAll();
        model.addAttribute("issues", issues);
        return "issues/list"; // src/main/resources/templates/issues/list.html
    }

    /**
     * チケット詳細画面
     * GET /issues/{id}
     */
    @GetMapping("/{id}")
    public String viewIssue(@PathVariable Long id, Model model) {
        return issueService.findById(id)
                .map(issue -> {
                    model.addAttribute("issue", issue);
                    return "issues/detail"; // src/main/resources/templates/issues/detail.html
                })
                .orElse("redirect:/issues");
    }

    /**
     * チケット新規作成画面の表示
     * GET /issues/new
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("issueRequestDto", new IssueRequestDto(
                null, null, null, null, null, "", "", "Normal", null, null, null
        ));
        // フォームのドロップダウン用データ
        populateFormMasterData(model);
        return "issues/create"; // src/main/resources/templates/issues/create.html
    }

    /**
     * チケット新規作成処理
     * POST /issues
     */
    @PostMapping
    public String createIssue(
            @Valid @ModelAttribute("issueRequestDto") IssueRequestDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            populateFormMasterData(model);
            return "issues/create";
        }

        issueService.create(dto);
        return "redirect:/issues";
    }

    private void populateFormMasterData(Model model) {
        model.addAttribute("projects", projectRepository.findAll());
        model.addAttribute("trackers", trackerRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
    }
    /**
     * チケット編集画面の表示
     * GET /issues/{id}/edit
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return issueService.findById(id)
                .map(issue -> {
                    // ResponseDto から RequestDto へ値を詰め替えてフォーム初期値を作成
                    IssueRequestDto dto = new IssueRequestDto(
                            issue.projectId(),
                            findTrackerIdByName(issue.trackerName()),
                            findStatusIdByName(issue.statusName()),
                            findUserIdByName(issue.authorName()),
                            findUserIdByName(issue.assigneeName()),
                            issue.subject(),
                            issue.description(),
                            issue.priority(),
                            issue.startDate(),
                            issue.dueDate(),
                            issue.estimatedHours()
                    );
                    
                    model.addAttribute("id", id);
                    model.addAttribute("issueRequestDto", dto);
                    populateFormMasterData(model); // ドロップダウン用マスターデータ設定
                    return "issues/edit"; // src/main/resources/templates/issues/edit.html
                })
                .orElse("redirect:/issues");
    }

    /**
     * チケット更新処理
     * POST /issues/{id}/edit
     */
    @PostMapping("/{id}/edit")
    public String updateIssue(
            @PathVariable Long id,
            @Valid @ModelAttribute("issueRequestDto") IssueRequestDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("id", id);
            populateFormMasterData(model);
            return "issues/edit";
        }

        issueService.update(id, dto);
        return "redirect:/issues/" + id; // 更新後は詳細画面へリダイレクト
    }

    // --- マスタID特定用のプライベートヘルパーメソッド例 ---

    private Long findTrackerIdByName(String name) {
        if (name == null) return null;
        return trackerRepository.findByName(name).map(Tracker::getId).orElse(null);
    }

    private Long findStatusIdByName(String name) {
        if (name == null) return null;
        return statusRepository.findByName(name).map(IssueStatus::getId).orElse(null);
    }

    private Long findUserIdByName(String username) {
        if (username == null) return null;
        return userRepository.findByUsername(username).map(User::getId).orElse(null);
    }

    /**
     * チケット削除処理
     * POST /issues/{id}/delete
     */
    @PostMapping("/{id}/delete")
    public String deleteIssue(@PathVariable Long id) {
        // チケットの存在確認と削除処理を実行
        issueService.delete(id);
        
        // 削除完了後はチケット一覧画面へリダイレクト
        return "redirect:/issues";
    }
}
