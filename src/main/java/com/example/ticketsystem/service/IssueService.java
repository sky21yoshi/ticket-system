package com.example.ticketsystem.service;

import java.util.List;
import java.util.Optional;

import com.example.ticketsystem.dto.IssueRequestDto;
import com.example.ticketsystem.dto.IssueResponseDto;

/**
 * チケット管理 サービスインターフェース
 * 
 * チケットの作成・参照・更新・削除処理の仕様を定義します。
 * コントローラー層はこのインターフェースに依存することで、具体的な実装と分離されます。
 */
public interface IssueService {

    /**
     * 全チケットを取得します。
     * 
     * @return チケットレスポンス DTO のリスト
     */
    List<IssueResponseDto> findAll();

    /**
     * 指定されたIDのチケットを取得します。
     * 
     * @param id チケットID
     * @return 見つかった場合は Optional<IssueResponseDto>、存在しない場合は Optional.empty()
     */
    Optional<IssueResponseDto> findById(Long id);

    /**
     * 新規チケットを作成します。
     * 
     * @param dto リクエスト DTO
     * @return 作成されたチケットのレスポンス DTO
     * @throws IllegalArgumentException 指定された関連ID (プロジェクト等) が存在しない場合
     */
    IssueResponseDto create(IssueRequestDto dto);

    /**
     * 指定されたIDのチケット情報を更新します。
     * 
     * @param id 更新対象のチケットID
     * @param dto 更新内容を含むリクエスト DTO
     * @return 更新された場合は Optional<IssueResponseDto>、対象が存在しない場合は Optional.empty()
     * @throws IllegalArgumentException 指定された関連ID (プロジェクト等) が存在しない場合
     */
    Optional<IssueResponseDto> update(Long id, IssueRequestDto dto);

    /**
     * 指定されたIDのチケットを削除します。
     * 
     * @param id 削除対象のチケットID
     * @return 削除が実行された場合は true、対象が存在しない場合は false
     */
    boolean deleteById(Long id);
}
