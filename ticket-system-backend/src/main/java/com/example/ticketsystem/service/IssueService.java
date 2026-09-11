package com.example.ticketsystem.service;

import java.util.List;
import java.util.Optional;

import com.example.ticketsystem.dto.IssueCreateRequest;
import com.example.ticketsystem.dto.IssueResponse;
import com.example.ticketsystem.dto.IssueSearchCriteria;
import com.example.ticketsystem.dto.IssueUpdateRequest;

/**
 * チケット管理 サービスインターフェース
 * 
 * チケットのCRUD、変更履歴自動記録、ステータス遷移（ワークフロー）制御、
 * および絞り込み検索のビジネスロジックを定義します。
 */
public interface IssueService {

    /**
     * 全チケット一覧を取得します。
     * 
     * @return チケットレスポンスリスト
     */
    List<IssueResponse> findAll();

    /**
     * 条件によるチケット絞り込み検索を行います (F-303)。
     * 
     * @param criteria 検索・絞り込み条件
     * @return 該当するチケットレスポンスリスト
     */
    List<IssueResponse> search(IssueSearchCriteria criteria);

    /**
     * 指定されたIDのチケット詳細（変更履歴含む）を取得します (F-301, F-302)。
     * 
     * @param id チケットID
     * @return チケットレスポンス（存在しない場合は empty）
     */
    Optional<IssueResponse> findById(Long id);

    /**
     * 新規チケットを作成・登録します (F-301)。
     * 
     * @param request 新規作成リクエストDTO
     * @return 作成されたチケットレスポンス
     * @throws IllegalArgumentException バリデーションエラーや関連エンティティが存在しない場合
     */
    IssueResponse create(IssueCreateRequest request);

    /**
     * チケット情報を更新し、変更履歴を自動記録します (F-302, F-401)。
     * 
     * @param id 更新対象のチケットID
     * @param request 更新リクエストDTO（更新者ID、コメント含む）
     * @return 更新されたチケットレスポンス（存在しない場合は empty）
     * @throws com.example.ticketsystem.exception.WorkflowViolationException ステータス遷移が許可されていない場合 (403)
     * @throws IllegalArgumentException バリデーションエラーや関連エンティティが存在しない場合
     */
    Optional<IssueResponse> update(Long id, IssueUpdateRequest request);

    /**
     * 指定されたIDのチケットを削除します。
     * 
     * @param id 削除対象のチケットID
     * @return 削除が実行された場合は true、存在しない場合は false
     */
    boolean deleteById(Long id);


    /**
     * プロジェクトに所属する全チケット一覧を取得します。
     * 
     * @param projectId プロジェクトID
     * @return チケットレスポンスリスト
     */
    List<IssueResponse> findByProjectId(Long projectId);
}