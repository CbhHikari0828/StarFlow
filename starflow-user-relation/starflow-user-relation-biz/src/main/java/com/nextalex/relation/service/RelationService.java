package com.nextalex.relation.service;

import com.nextalex.framework.common.response.PageResponse;
import com.nextalex.framework.common.response.Response;
import com.nextalex.relation.model.vo.FindFansListReqVO;
import com.nextalex.relation.model.vo.FindFansUserRspVO;
import com.nextalex.relation.model.vo.FindFollowingListReqVO;
import com.nextalex.relation.model.vo.FindFollowingUserRspVO;
import com.nextalex.relation.model.vo.FollowUserReqVO;
import com.nextalex.relation.model.vo.UnfollowUserReqVO;

/**
 * 用户关系业务.
 *
 * @author NextAlex
 * @date 2026/07/10
 * @since 0.0.1
 */
public interface RelationService {

    /**
     * 关注用户.
     *
     * @param followUserReqVO 关注请求（含被关注用户 ID）
     * @return 统一响应
     */
    Response<?> follow(FollowUserReqVO followUserReqVO);

    /**
     * 取关用户.
     *
     * @param unfollowUserReqVO 取关请求（含被取关用户 ID）
     * @return 统一响应
     */
    Response<?> unfollow(UnfollowUserReqVO unfollowUserReqVO);

    /**
     * 分页查询关注列表.
     *
     * @param findFollowingListReqVO 查询请求（含目标用户 ID、页码）
     * @return 关注用户分页列表
     */
    PageResponse<FindFollowingUserRspVO> findFollowingList(FindFollowingListReqVO findFollowingListReqVO);

    /**
     * 分页查询粉丝列表.
     *
     * @param findFansListReqVO 查询请求（含目标用户 ID、页码）
     * @return 粉丝用户分页列表
     */
    PageResponse<FindFansUserRspVO> findFansList(FindFansListReqVO findFansListReqVO);
}
