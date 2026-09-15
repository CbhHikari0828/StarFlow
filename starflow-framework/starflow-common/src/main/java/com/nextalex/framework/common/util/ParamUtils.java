package com.nextalex.framework.common.util;

import java.util.regex.Pattern;

/**
 * 参数校验工具.
 *
 * <p>提供昵称、starflow 号、字符串长度的通用校验规则，供各业务服务复用。
 *
 * @author NextAlex
 * @date 2026/07/07
 * @since 0.0.1
 */
public final class ParamUtils {

    private ParamUtils() {
    }

    // ===== 昵称：2-24 字符，禁用特殊字符 =====
    private static final int NICKNAME_MIN_LENGTH = 2;
    private static final int NICKNAME_MAX_LENGTH = 24;
    private static final Pattern NICKNAME_SPECIAL_CHARS = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    /**
     * 校验昵称：长度 2-24，且不含特殊字符.
     *
     * @param nickname 昵称
     * @return 合法返回 {@code true}
     */
    public static boolean checkNickname(String nickname) {
        if (nickname == null
                || nickname.length() < NICKNAME_MIN_LENGTH
                || nickname.length() > NICKNAME_MAX_LENGTH) {
            return false;
        }
        return !NICKNAME_SPECIAL_CHARS.matcher(nickname).find();
    }

    // ===== starflow 号：6-15 字符，仅英文/数字/下划线 =====
    private static final int STARFLOW_ID_MIN_LENGTH = 6;
    private static final int STARFLOW_ID_MAX_LENGTH = 15;
    private static final Pattern STARFLOW_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    /**
     * 校验 starflow 号：长度 6-15，仅英文、数字、下划线.
     *
     * @param starflowId starflow 号
     * @return 合法返回 {@code true}
     */
    public static boolean checkStarFlowId(String starflowId) {
        if (starflowId == null
                || starflowId.length() < STARFLOW_ID_MIN_LENGTH
                || starflowId.length() > STARFLOW_ID_MAX_LENGTH) {
            return false;
        }
        return STARFLOW_ID_PATTERN.matcher(starflowId).matches();
    }

    /**
     * 校验字符串长度：非空且不超过上限.
     *
     * @param str       字符串
     * @param maxLength 长度上限
     * @return 合法返回 {@code true}
     */
    public static boolean checkLength(String str, int maxLength) {
        return str != null && !str.isEmpty() && str.length() <= maxLength;
    }
}
