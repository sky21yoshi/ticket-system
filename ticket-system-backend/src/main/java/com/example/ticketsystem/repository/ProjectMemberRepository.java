package com.example.ticketsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.ProjectMember;

/**
 * プロジェクトメンバー（ProjectMember）リポジトリ・インターフェース
 * 
 * project_members テーブルに対する CRUD 操作を提供します。
 */
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    /**
     * 特定のプロジェクトに所属するメンバー一覧を取得します。
     * 
     * @param projectId プロジェクトID
     * @return プロジェクトメンバーのリスト
     */
    List<ProjectMember> findByProjectId(Long projectId);

    /**
     * 特定のユーザーが参加しているプロジェクトメンバー一覧を取得します。
     * 
     * @param userId ユーザーID
     * @return プロジェクトメンバーのリスト
     */
    List<ProjectMember> findByUserId(Long userId);

    /**
     * 特定のプロジェクト・ユーザーに対するメンバー情報を取得します。
     * 
     * @param projectId プロジェクトID
     * @param userId ユーザーID
     * @return 該当するメンバー情報（存在しない場合は Optional.empty()）
     */
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    /**
     * 指定されたプロジェクト・ユーザー・ロールの組み合わせが存在するか確認します。
     * 
     * @param projectId プロジェクトID
     * @param userId ユーザーID
     * @param roleId ロールID
     * @return 存在する場合は true
     */
    boolean existsByProjectIdAndUserIdAndRoleId(Long projectId, Long userId, Long roleId);
}

