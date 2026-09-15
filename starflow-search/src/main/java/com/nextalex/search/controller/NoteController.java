package com.nextalex.search.controller;

import com.nextalex.framework.biz.operationlog.aspect.ApiOperationLog;
import com.nextalex.framework.common.response.PageResponse;
import com.nextalex.search.model.vo.SearchNoteReqVO;
import com.nextalex.search.model.vo.SearchNoteRspVO;
import com.nextalex.search.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 笔记搜索控制器.
 *
 * @author NextAlex
 * @date 2026/07/13
 * @since 0.0.1
 */
@RestController
@RequestMapping("/search")
@Slf4j
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping("/note")
    @ApiOperationLog(description = "搜索笔记")
    public PageResponse<SearchNoteRspVO> searchNote(@RequestBody @Valid SearchNoteReqVO searchNoteReqVO) {
        return noteService.searchNote(searchNoteReqVO);
    }
}
