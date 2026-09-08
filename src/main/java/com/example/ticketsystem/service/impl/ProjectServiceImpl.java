package com.example.ticketsystem.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ticketsystem.entity.Project;
import com.example.ticketsystem.repository.ProjectRepository;
import com.example.ticketsystem.service.ProjectService;

/**
 * プロジェクト管理 サービス実装クラス
 * 
 * JPA Repository を使用してデータベース操作を行い、トランザクション境界を制御します。
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    // コンストラクタインジェクション (推奨される DI パターン)
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * 全プロジェクトを取得します。
     * 読み取り専用トランザクションを設定してパフォーマンスを最適化します。
     */
    @Override
    @Transactional(readOnly = true)
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    /**
     * 指定されたIDのプロジェクトを取得します。
     * 読み取り専用トランザクションで検索を実行します。
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    /**
     * 新規プロジェクトを登録します。
     */
    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    /**
     * 指定されたIDのプロジェクト情報を更新します。
     * 存在チェックを行い、存在する対象に対してのみフィールド値を更新します。
     */
    @Override
    public Optional<Project> update(Long id, Project projectDetails) {
        return projectRepository.findById(id).map(existingProject -> {
            // 既存のエンティティの値を書き換え
            existingProject.setName(projectDetails.getName());
            existingProject.setDescription(projectDetails.getDescription());
            
            // ダーティチェック（自動更新）機能も働きますが、明示的に save を実行・返却します
            return projectRepository.save(existingProject);
        });
    }

    /**
     * 指定されたIDのプロジェクトを削除します。
     * 事前に存在有無を確認することで、存在しないIDに対する不要な例外発生を防ぎます。
     */
    @Override
    public boolean deleteById(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
