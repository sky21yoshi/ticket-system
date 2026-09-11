package com.example.ticketsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.Journal;

/**
 * 変更履歴（Journal）リポジトリ・インターフェース
 * 
 * journals テーブルに対するアクセスを提供します。
 */
@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {

    /**
     * 特定のチケットに対する変更履歴一覧を投稿日時の昇順（古い順）で取得します。
     * 
     * @param issueId チケットID
     * @return 履歴リスト
     */
    List<Journal> findByIssueIdOrderByCreatedAtAsc(Long issueId);

    /**
     * 特定のチケットに対する変更履歴一覧を投稿日時の降順（新しい順）で取得します。
     * 
     * @param issueId チケットID
     * @return 履歴リスト
     */
    List<Journal> findByIssueIdOrderByCreatedAtDesc(Long issueId);
}

