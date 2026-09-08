package com.example.ticketsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.Issue;

/**
 * チケット（Issue）リポジトリ・インターフェース
 * 
 * Spring Data JPA の JpaRepository を継承し、
 * Issue エンティティ（主キー型: Long）に対する標準的な CRUD 操作およびカスタムクエリを提供します。
 */
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

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
}
