package com.example.ticketsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.Project;

/**
 * プロジェクト（Project）リポジトリ・インターフェース
 * 
 * projects テーブルに対する CRUD 操作および検索を提供します。
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

    /**
     * プロジェクト識別子で検索を行います。
     * 
     * @param identifier プロジェクト識別子 (URLキー等)
     * @return 該当するプロジェクト（存在しない場合は Optional.empty()）
     */
    Optional<Project> findByIdentifier(String identifier);

    /**
     * プロジェクト識別子の存在確認を行います。
     * 
     * @param identifier プロジェクト識別子
     * @return 既に存在する場合は true、存在しない場合は false
     */
    boolean existsByIdentifier(String identifier);
}
