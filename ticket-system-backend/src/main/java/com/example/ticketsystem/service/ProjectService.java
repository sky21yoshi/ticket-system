package com.example.ticketsystem.service;

import java.util.List;
import java.util.Optional;

import com.example.ticketsystem.dto.ProjectMemberRequest;
import com.example.ticketsystem.dto.ProjectMemberResponse;
import com.example.ticketsystem.dto.ProjectRequest;
import com.example.ticketsystem.dto.ProjectResponse;

/**
 * プロジェクト管理 サービスインターフェース
 * 
 * プロジェクトのCRUDおよびメンバー管理のビジネスロジックを定義します。
 */
public interface ProjectService {

    /**
     * 全プロジェクト一覧を取得します。
     * 
     * @return プロジェクトレスポンスリスト
     */
    List<ProjectResponse> findAll();

    /**
     * ID指定でプロジェクト詳細を取得します。
     * 
     * @param id プロジェクトID
     * @return プロジェクトレスポンス（存在しない場合は empty）
     */
    Optional<ProjectResponse> findById(Long id);

    /**
     * プロジェクト識別子でプロジェクト詳細を取得します。
     * 
     * @param identifier プロジェクト識別子
     * @return プロジェクトレスポンス（存在しない場合は empty）
     */
    Optional<ProjectResponse> findByIdentifier(String identifier);

    /**
     * 新規プロジェクトを作成します。
     * 
     * @param request 作成リクエストDTO
     * @return 作成されたプロジェクトレスポンス
     * @throws IllegalArgumentException 識別子が重複している場合
     */
    ProjectResponse create(ProjectRequest request);

    /**
     * プロジェクト情報を更新します。
     * 
     * @param id 更新対象のプロジェクトID
     * @param request 更新リクエストDTO
     * @return 更新されたプロジェクトレスポンス
     */
    Optional<ProjectResponse> update(Long id, ProjectRequest request);

    /**
     * プロジェクトを削除します。
     * 
     * @param id プロジェクトID
     * @return 削除成否
     */
    boolean deleteById(Long id);

    /**
     * 指定プロジェクトのメンバー一覧を取得します。
     * 
     * @param projectId プロジェクトID
     * @return メンバーレスポンスリスト
     */
    List<ProjectMemberResponse> getMembers(Long projectId);

    /**
     * プロジェクトにメンバーを追加（またはロール変更）します。
     * 
     * @param projectId プロジェクトID
     * @param request メンバーリクエストDTO
     * @return 登録されたメンバーレスポンス
     */
    ProjectMemberResponse addOrUpdateMember(Long projectId, ProjectMemberRequest request);

    /**
     * プロジェクトからメンバーを除外します。
     * 
     * @param projectId プロジェクトID
     * @param userId ユーザーID
     * @return 削除成否
     */
    boolean removeMember(Long projectId, Long userId);
}
