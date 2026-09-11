package com.example.ticketsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.Workflow;

/**
 * ワークフロー制御（Workflow）リポジトリ・インターフェース
 * 
 * workflows テーブルに対するアクセスを提供します。
 */
@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    /**
     * 指定されたロール、トラッカー、変更前ステータス、変更後ステータスの組み合わせが存在するか判定します。
     * 
     * @param roleId ロールID
     * @param trackerId トラッカーID
     * @param oldStatusId 変更前ステータスID
     * @param newStatusId 変更後ステータスID
     * @return 遷移が許可されている場合は true
     */
    boolean existsByRoleIdAndTrackerIdAndOldStatusIdAndNewStatusId(
            Long roleId, Long trackerId, Long oldStatusId, Long newStatusId);

    /**
     * 複数のロール候補のいずれかでステータス遷移が許可されているか判定します。
     * 
     * @param roleIds ユーザーがプロジェクト内で保持するロールIDのリスト
     * @param trackerId トラッカーID
     * @param oldStatusId 変更前ステータスID
     * @param newStatusId 変更後ステータスID
     * @return いずれかのロールで許可されていれば true
     */
    boolean existsByRoleIdInAndTrackerIdAndOldStatusIdAndNewStatusId(
            List<Long> roleIds, Long trackerId, Long oldStatusId, Long newStatusId);

    /**
     * 指定されたロールおよびトラッカーで定義されているワークフロールール一覧を取得します。
     * 
     * @param roleId ロールID
     * @param trackerId トラッカーID
     * @return ワークフロールールのリスト
     */
    List<Workflow> findByRoleIdAndTrackerId(Long roleId, Long trackerId);
}

