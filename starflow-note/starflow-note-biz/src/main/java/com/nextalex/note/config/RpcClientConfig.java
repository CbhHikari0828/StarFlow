package com.nextalex.note.config;

import com.nextalex.count.api.CountHttpApi;
import com.nextalex.count.api.constant.CountApiConstants;
import com.nextalex.id.api.DistributedIdHttpApi;
import com.nextalex.id.api.constant.IdApiConstants;
import com.nextalex.kv.api.KeyValueHttpApi;
import com.nextalex.kv.api.constant.KvApiConstants;
import com.nextalex.user.api.UserHttpApi;
import com.nextalex.user.api.constant.UserApiConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

/**
 * RPC 客户端声明.
 *
 * <p>把分布式 ID 服务的 {@link DistributedIdHttpApi}、KV 键值服务的 {@link KeyValueHttpApi}、
 * 用户服务的 {@link UserHttpApi} 分别注册为对应分组的 HTTP Interface 客户端。RPC starter 的
 * {@code LoadBalancedRestClientConfigurer} 会自动为各分组设置 {@code baseUrl = lb://<group>}
 * 并挂上负载均衡与 userId 透传拦截器。
 *
 * @author NextAlex
 * @date 2026/07/09
 * @since 0.0.1
 */
@Configuration
@ImportHttpServices(group = IdApiConstants.SERVICE_NAME, types = DistributedIdHttpApi.class)
@ImportHttpServices(group = KvApiConstants.SERVICE_NAME, types = KeyValueHttpApi.class)
@ImportHttpServices(group = UserApiConstants.SERVICE_NAME, types = UserHttpApi.class)
@ImportHttpServices(group = CountApiConstants.SERVICE_NAME, types = CountHttpApi.class)
public class RpcClientConfig {
}
