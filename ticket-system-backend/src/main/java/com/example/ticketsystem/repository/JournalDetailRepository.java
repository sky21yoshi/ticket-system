package com.example.ticketsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.JournalDetail;

/**
 * 変更履歴明細（JournalDetail）リポジトリ・インターフェース
 * 
 * journal_details テーブルに対するアクセスを提供します。
 */
@Repository
public interface JournalDetailRepository extends JpaRepository<JournalDetail, Long> {

    /**
     * 特定の履歴ヘッダーに紐づく明細一覧を取得します。
     * 
     * @param journalId 履歴ID
     * @return 明細リスト
     */
    List<JournalDetail> findByJournalId(Long journalId);
}

