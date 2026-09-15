package com.nextalex.kv.controller;

import com.nextalex.framework.common.response.Response;
import com.nextalex.kv.api.dto.req.AddNoteContentReqDTO;
import com.nextalex.kv.api.dto.req.DeleteNoteContentReqDTO;
import com.nextalex.kv.api.dto.req.FindNoteContentReqDTO;
import com.nextalex.kv.api.dto.resp.FindNoteContentRspDTO;
import com.nextalex.kv.service.NoteContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 笔记内容存储控制器.
 *
 * @author NextAlex
 * @date 2026/07/08
 * @since 0.0.1
 */
@RestController
@RequestMapping("/kv")
@Slf4j
@RequiredArgsConstructor
public class NoteContentController {

    private final NoteContentService noteContentService;

    /**
     * 新增笔记内容.
     */
    @PostMapping("/note/content/add")
    public Response<?> addNoteContent(@Validated @RequestBody AddNoteContentReqDTO addNoteContentReqDTO) {
        return noteContentService.addNoteContent(addNoteContentReqDTO);
    }

    /**
     * 查询笔记内容.
     */
    @PostMapping("/note/content/find")
    public Response<FindNoteContentRspDTO> findNoteContent(@Validated @RequestBody FindNoteContentReqDTO findNoteContentReqDTO) {
        return noteContentService.findNoteContent(findNoteContentReqDTO);
    }

    /**
     * 删除笔记内容.
     */
    @PostMapping("/note/content/delete")
    public Response<?> deleteNoteContent(@Validated @RequestBody DeleteNoteContentReqDTO deleteNoteContentReqDTO) {
        return noteContentService.deleteNoteContent(deleteNoteContentReqDTO);
    }
}
