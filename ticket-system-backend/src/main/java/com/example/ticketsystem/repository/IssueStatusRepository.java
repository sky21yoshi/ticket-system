package com.example.ticketsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.IssueStatus;

/**
 * チケットステータス（IssueStatus）リポジトリ・インターフェース
 * 
 * issue_statuses テーブルに対するアクセスを提供します。
 */
@Repository
public interface IssueStatusRepository extends JpaRepository<IssueStatus, Long> {

    /**
     * ステータス名で検索します。
     * 
     * @param name ステータス名
     * @return 該当するステータス
     */
    Optional<IssueStatus> findByName(String name);

    /**
     * 表示順序順に全ステータスを取得します。
     * 
     * @return ソートされたステータスのリスト
     */
    List<IssueStatus> findAllByOrderByPositionAsc();
}
