package com.example.ticketsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ワークフロー制御違反（ステータス遷移が許可されていない場合）にスローされる例外クラス
 * HTTP Status 403 Forbidden を返却します。
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class WorkflowViolationException extends RuntimeException {

    public WorkflowViolationException(String message) {
        super(message);
    }
}

