package com.example.ticketsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.Issue;

/**
 * チケット（Issue）リポジトリ・インターフェース
 * 
 * Spring Data JPA の JpaRepository および JpaSpecificationExecutor を継承し、
 * Issue エンティティに対する CRUD 操作および動的検索・絞り込みを提供します。
 */
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    /**
     * 特定のプロジェクトIDに紐づくチケット一覧を取得します。
     * 
     * @param projectId プロジェクトID
     * @return 該当するチケットのリスト
     */
    List<Issue> findByProjectId(Long projectId);

    /**
     * 特定の担当者（ユーザーID）に割り当てられたチケット一覧を取得します。
     * 
     * @param assigneeId 担当者のユーザーID
     * @return 該当するチケットのリスト
     */
    List<Issue> findByAssigneeId(Long assigneeId);

    /**
     * 特定の作成者（ユーザーID）が作成したチケット一覧を取得します。
     * 
     * @param authorId 作成者のユーザーID
     * @return 該当するチケットのリスト
     */
    List<Issue> findByAuthorId(Long authorId);
}
