package com.nextalex.oss.service.impl;

import com.nextalex.framework.common.response.Response;
import com.nextalex.oss.service.FileService;
import com.nextalex.oss.strategy.FileStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件业务实现.
 *
 * <p>面向 {@link FileStrategy} 接口调用，具体存储实现由工厂按配置选定。
 *
 * @author NextAlex
 * @date 2026/07/07
 * @since 0.0.1
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileStrategy fileStrategy;

    @Override
    public Response<?> uploadFile(MultipartFile file) {
        String url = fileStrategy.uploadFile(file);
        return Response.success(url);
    }
}
