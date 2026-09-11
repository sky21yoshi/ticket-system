package com.example.ticketsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.Role;

/**
 * ロール（Role）リポジトリ・インターフェース
 * 
 * roles テーブルに対する CRUD 操作を提供します。
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * ロール名で検索します。
     * 
     * @param name ロール名 (例: "システム管理者", "開発者", "閲覧者")
     * @return 該当するロール（存在しない場合は Optional.empty()）
     */
    Optional<Role> findByName(String name);
}

