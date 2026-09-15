package com.nextalex.comment.config;

import com.nextalex.id.api.DistributedIdHttpApi;
import com.nextalex.id.api.constant.IdApiConstants;
import com.nextalex.kv.api.KeyValueHttpApi;
import com.nextalex.kv.api.constant.KvApiConstants;
import com.nextalex.note.api.NoteHttpApi;
import com.nextalex.note.api.constant.NoteApiConstants;
import com.nextalex.count.api.CountHttpApi;
import com.nextalex.count.api.constant.CountApiConstants;
import com.nextalex.user.api.UserHttpApi;
import com.nextalex.user.api.constant.UserApiConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

/**
 * RPC 客户端声明（HTTP Interface + LoadBalancer）.
 *
 * @author NextAlex
 * @date 2026/07/14
 * @since 0.0.1
 */
@Configuration
@ImportHttpServices(group = IdApiConstants.SERVICE_NAME, types = DistributedIdHttpApi.class)
@ImportHttpServices(group = KvApiConstants.SERVICE_NAME, types = KeyValueHttpApi.class)
@ImportHttpServices(group = NoteApiConstants.SERVICE_NAME, types = NoteHttpApi.class)
@ImportHttpServices(group = CountApiConstants.SERVICE_NAME, types = CountHttpApi.class)
@ImportHttpServices(group = UserApiConstants.SERVICE_NAME, types = UserHttpApi.class)
public class RpcClientConfig {
}
