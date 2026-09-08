package com.example.ticketsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.IssueStatus;

/**
 * チケットステータス（IssueStatus）リポジトリ・インターフェース
 * 
 * IssueStatus エンティティ（主キー型: Long）に対するアクセスを提供します。
 */
@Repository
public interface IssueStatusRepository extends JpaRepository<IssueStatus, Long> {

    /**
     * ステータス名（例: "New", "In Progress", "Closed"）で検索します。
     * 
     * @param name ステータス名
     * @return 該当するステータス
     */
    Optional<IssueStatus> findByName(String name);
}
