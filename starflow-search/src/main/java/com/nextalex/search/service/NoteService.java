package com.nextalex.search.service;

import com.nextalex.framework.common.response.PageResponse;
import com.nextalex.search.model.vo.SearchNoteReqVO;
import com.nextalex.search.model.vo.SearchNoteRspVO;

/**
 * 笔记搜索业务.
 *
 * @author NextAlex
 * @date 2026/07/13
 * @since 0.0.1
 */
public interface NoteService {

    /**
     * 搜索笔记。
     *
     * @param searchNoteReqVO 搜索入参
     * @return 分页搜索结果
     */
    PageResponse<SearchNoteRspVO> searchNote(SearchNoteReqVO searchNoteReqVO);
}
