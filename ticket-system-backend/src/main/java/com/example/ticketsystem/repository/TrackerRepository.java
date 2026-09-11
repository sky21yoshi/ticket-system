package com.example.ticketsystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.Tracker;

/**
 * トラッカー（Tracker）リポジトリ・インターフェース
 * 
 * trackers テーブルに対するアクセスを提供します。
 */
@Repository
public interface TrackerRepository extends JpaRepository<Tracker, Long> {

    /**
     * トラッカー名で検索します。
     * 
     * @param name トラッカー名
     * @return 該当するトラッカー
     */
    Optional<Tracker> findByName(String name);

    /**
     * 表示順序順に全トラッカーを取得します。
     * 
     * @return ソートされたトラッカーのリスト
     */
    List<Tracker> findAllByOrderByPositionAsc();
}
