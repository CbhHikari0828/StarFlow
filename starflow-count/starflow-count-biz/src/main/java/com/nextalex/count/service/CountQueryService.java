package com.nextalex.count.service;

import com.nextalex.count.api.dto.req.FindNoteCountReqDTO;
import com.nextalex.count.api.dto.req.FindNoteCountsByIdsReqDTO;
import com.nextalex.count.api.dto.resp.FindNoteCountRspDTO;
import com.nextalex.framework.common.response.Response;

import java.util.List;

/**
 * 计数查询业务.
 *
 * @author NextAlex
 * @date 2026/07/15
 * @since 0.0.1
 */
public interface CountQueryService {

    /**
     * 查询笔记维度计数.
     *
     * @param request 查询入参
     * @return 笔记计数
     */
    Response<FindNoteCountRspDTO> findNoteCountById(FindNoteCountReqDTO request);

    /**
     * 批量查询笔记维度计数.
     *
     * @param request 查询入参（笔记 ID 集合）
     * @return 各笔记的计数集合
     */
    Response<List<FindNoteCountRspDTO>> findNotesCountData(FindNoteCountsByIdsReqDTO request);
}
