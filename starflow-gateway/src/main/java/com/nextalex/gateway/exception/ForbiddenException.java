package com.nextalex.gateway.exception;

import com.nextalex.gateway.enums.ResponseCodeEnum;
import lombok.Getter;

/**
 * 网关权限不足异常.
 *
 * <p>用户已登录，但不具备访问目标资源所需的角色 / 权限时抛出，统一返回 403。
 *
 * @author NextAlex
 * @date 2026/07/07
 * @since 0.0.1
 */
@Getter
public class ForbiddenException extends RuntimeException {

    private final String errorCode;

    public ForbiddenException() {
        super(ResponseCodeEnum.FORBIDDEN.getErrorMessage());
        this.errorCode = ResponseCodeEnum.FORBIDDEN.getErrorCode();
    }
}
