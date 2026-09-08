package com.example.ticketsystem.service;

import java.util.List;
import java.util.Optional;

import com.example.ticketsystem.entity.Project;

/**
 * プロジェクト管理 サービスインターフェース
 * 
 * プロジェクトの作成・参照・更新・削除処理の仕様を定義します。
 * コントローラー層はこのインターフェースに依存することで、具体的な実装と分離されます。
 */
public interface ProjectService {

    /**
     * 全プロジェクトを取得します。
     * 
     * @return プロジェクトのリスト
     */
    List<Project> findAll();

    /**
     * 指定されたIDのプロジェクトを取得します。
     * 
     * @param id プロジェクトID
     * @return 見つかった場合は Optional<Project>、存在しない場合は Optional.empty()
     */
    Optional<Project> findById(Long id);

    /**
     * 新規プロジェクトを作成・保存します。
     * 
     * @param project 保存するプロジェクトエンティティ
     * @return 保存されたプロジェクトエンティティ（生成されたIDを含む）
     */
    Project save(Project project);

    /**
     * 指定されたIDのプロジェクト情報を更新します。
     * 
     * @param id 更新対象のプロジェクトID
     * @param projectDetails 更新内容を含むプロジェクトエンティティ
     * @return 更新された場合は Optional<Project>、対象が存在しない場合は Optional.empty()
     */
    Optional<Project> update(Long id, Project projectDetails);

    /**
     * 指定されたIDのプロジェクトを削除します。
     * 
     * @param id 削除対象のプロジェクトID
     * @return 削除が実行された場合は true、対象が存在しない場合は false
     */
    boolean deleteById(Long id);
}
