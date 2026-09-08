package com.example.ticketsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.User;

/**
 * ユーザー（User）リポジトリ・インターフェース
 * 
 * User エンティティ（主キー型: Long）に対するアクセスを提供します。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ユーザー名で検索を行います。
     * 
     * @param username ユーザー名
     * @return 該当するユーザー
     */
    Optional<User> findByUsername(String username);

    /**
     * メールアドレスで検索を行います。
     * 
     * @param email メールアドレス
     * @return 該当するユーザー
     */
    Optional<User> findByEmail(String email);
}
