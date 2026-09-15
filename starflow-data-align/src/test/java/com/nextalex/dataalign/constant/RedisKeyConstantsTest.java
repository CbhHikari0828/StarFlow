package com.nextalex.dataalign.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link RedisKeyConstants} 单元测试.
 *
 * <p>重点锁定「计数缓存 Key/Field 镜像」与 {@code starflow-count} 的
 * {@code RedisKeyConstants} 逐字节一致——回写缓存的目标 Key 一旦漂移，
 * 计数服务将永远读不到对齐后的值。此处以硬编码期望值固化契约。
 *
 * @author NextAlex
 * @date 2026/07/11
 * @since 0.0.1
 */
class RedisKeyConstantsTest {

    @Test
    @DisplayName("计数缓存 Key 必须与 starflow-count 一致")
    void countKeys_mirrorCountService() {
        assertEquals("starflow:count:count:user:1", RedisKeyConstants.buildCountUserKey(1L));
        assertEquals("starflow:count:count:note:1", RedisKeyConstants.buildCountNoteKey(1L));
    }

    @Test
    @DisplayName("计数 Hash Field 必须与 starflow-count 一致")
    void countFields_mirrorCountService() {
        assertEquals("fansTotal", RedisKeyConstants.FIELD_FANS_TOTAL);
        assertEquals("followingTotal", RedisKeyConstants.FIELD_FOLLOWING_TOTAL);
        assertEquals("likeTotal", RedisKeyConstants.FIELD_LIKE_TOTAL);
        assertEquals("collectTotal", RedisKeyConstants.FIELD_COLLECT_TOTAL);
        assertEquals("noteTotal", RedisKeyConstants.FIELD_NOTE_TOTAL);
        assertEquals("commentTotal", RedisKeyConstants.FIELD_COMMENT_TOTAL);
    }

    @Test
    @DisplayName("布隆过滤器 Key 位于本服务命名空间且按日创建")
    void bloomKeys_namespacedByDay() {
        String date = "20260711";
        assertEquals("starflow:dataAlign:note:like:noteIds:" + date,
                RedisKeyConstants.buildBloomNoteLikeNoteIdKey(date));
        assertEquals("starflow:dataAlign:note:like:userIds:" + date,
                RedisKeyConstants.buildBloomNoteLikeUserIdKey(date));
        assertEquals("starflow:dataAlign:note:collect:noteIds:" + date,
                RedisKeyConstants.buildBloomNoteCollectNoteIdKey(date));
        assertEquals("starflow:dataAlign:note:collect:userIds:" + date,
                RedisKeyConstants.buildBloomNoteCollectUserIdKey(date));
        assertEquals("starflow:dataAlign:user:note:operate:" + date,
                RedisKeyConstants.buildBloomNoteOperateUserIdKey(date));
        assertEquals("starflow:dataAlign:user:follow:" + date,
                RedisKeyConstants.buildBloomUserFollowKey(date));
        assertEquals("starflow:dataAlign:user:fans:" + date,
                RedisKeyConstants.buildBloomUserFansKey(date));
        assertEquals("starflow:dataAlign:note:comment:noteIds:" + date,
                RedisKeyConstants.buildBloomNoteCommentNoteIdKey(date));
    }
}
