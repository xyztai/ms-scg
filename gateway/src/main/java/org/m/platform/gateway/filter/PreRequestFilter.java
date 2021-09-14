package org.m.platform.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.m.platform.gateway.props.RequestProperties;
import org.m.platform.gateway.util.UUIDUtil;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.m.platform.gateway.constant.GatewayConst.LOG_TRACE_ID;
import static org.m.platform.gateway.constant.GatewayConst.M_TRACE_ID;

/**
 * @Author tai
 * @create 2021-09-14 18:20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PreRequestFilter implements GlobalFilter, Ordered {

    private final RequestProperties requestProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 是否开启traceId追踪
        if (requestProperties.getTrace()) {
            // ID生成
            String traceId = UUIDUtil.shortUuid();
            MDC.put(LOG_TRACE_ID, traceId);
            ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                    .headers(h -> h.add(M_TRACE_ID, traceId))
                    .build();
            ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
            return chain.filter(build);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}