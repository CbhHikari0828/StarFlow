package com.nextalex.count.service;

import com.nextalex.count.api.dto.req.FindUserCountReqDTO;
import com.nextalex.count.api.dto.resp.FindUserCountRspDTO;
import com.nextalex.framework.common.response.Response;

/**
 * 用户维度计数查询业务.
 *
 * @author NextAlex
 * @date 2026/07/17
 * @since 0.0.1
 */
public interface UserCountQueryService {

    /**
     * 查询用户维度计数.
     *
     * @param request 查询入参
     * @return 用户计数
     */
    Response<FindUserCountRspDTO> findUserCountData(FindUserCountReqDTO request);
}
