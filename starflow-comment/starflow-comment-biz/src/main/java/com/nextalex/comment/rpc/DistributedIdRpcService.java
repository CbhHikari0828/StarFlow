package com.nextalex.comment.rpc;

import com.nextalex.framework.common.response.Response;
import com.nextalex.id.api.DistributedIdHttpApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 分布式 ID 服务调用封装（生成评论 ID）.
 *
 * @author NextAlex
 * @date 2026/07/14
 * @since 0.0.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedIdRpcService {

    private final DistributedIdHttpApi distributedIdHttpApi;

    /**
     * 生成评论 ID.
     *
     * @return 成功返回 ID；失败返回 {@code null}
     */
    public Long generateCommentId() {
        Response<Long> response = distributedIdHttpApi.generateCommentId();
        if (Objects.isNull(response) || !response.isSuccess() || Objects.isNull(response.getData())) {
            log.error("==> 调用分布式 ID 服务生成 comment-id 失败, response: {}", response);
            return null;
        }
        return response.getData();
    }
}
