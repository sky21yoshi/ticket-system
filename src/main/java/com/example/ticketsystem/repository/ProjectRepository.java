package com.example.ticketsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.Project;

/**
 * プロジェクト（Project）リポジトリ・インターフェース
 * 
 * Project エンティティ（主キー型: Long）に対する永続化処理を担当します。
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * プロジェクト名で検索を行います。
     * 
     * @param name プロジェクト名
     * @return 該当するプロジェクト（存在しない場合は Optional.empty()）
     */
    Optional<Project> findByName(String name);
}
