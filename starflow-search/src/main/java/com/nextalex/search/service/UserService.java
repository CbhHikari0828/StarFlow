package com.nextalex.search.service;

import com.nextalex.framework.common.response.PageResponse;
import com.nextalex.search.model.vo.SearchUserReqVO;
import com.nextalex.search.model.vo.SearchUserRspVO;

/**
 * 用户搜索业务.
 *
 * @author NextAlex
 * @date 2026/07/13
 * @since 0.0.1
 */
public interface UserService {

    /**
     * 搜索用户。
     *
     * @param searchUserReqVO 搜索入参
     * @return 分页搜索结果
     */
    PageResponse<SearchUserRspVO> searchUser(SearchUserReqVO searchUserReqVO);
}
