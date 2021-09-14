package org.m.platform.gateway.filter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.m.platform.gateway.constant.GatewayConst.TRACE_ID;

/**
 * @Author tai
 * @create 2021-09-14 16:59
 */
@Slf4j
@Component
@AllArgsConstructor
public class RequestLogFilter implements GlobalFilter, Ordered {

    private static final String START_TIME = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 构建成一条长日志，打印请求信息
        StringBuilder reqLog = new StringBuilder(300);
        reqLog.append("\n\n================ Gateway Request Start  ================\n");
        reqLog.append("===> {}: {}, traceId = {}\n");
        // 请求参数
        List<Object> reqArgs = new ArrayList<>();
        String requestMethod = exchange.getRequest().getMethodValue();
        reqArgs.add(requestMethod);
        String requestUrl = exchange.getRequest().getURI().getRawPath();
        reqArgs.add(requestUrl);
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID);
        reqArgs.add(traceId);
        // 请求头
        HttpHeaders headers = exchange.getRequest().getHeaders();
        headers.forEach((headerName, headerValue) -> {
            reqLog.append("=== Headers ===  {}: {}\n");
            reqArgs.add(headerName);
            reqArgs.add(StringUtils.collectionToCommaDelimitedString(headerValue));
        });
        reqLog.append("================ Gateway Request End =================\n");
        // 打印请求信息
        log.info(reqLog.toString(), reqArgs.toArray());

        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            Long startTime = exchange.getAttribute(START_TIME);
            long executeTime = 0L;
            if (startTime != null) {
                executeTime = (System.currentTimeMillis() - startTime);
            }

            // 构建成一条长日志，打印返回信息
            StringBuilder responseLog = new StringBuilder(300);
            responseLog.append("\n\n================ Gateway Response Start  ================\n");
            responseLog.append("<=== {} {}: {}: {}\n");
            // 参数
            List<Object> responseArgs = new ArrayList<>();
            responseArgs.add(response.getStatusCode().value());
            responseArgs.add(requestMethod);
            responseArgs.add(requestUrl);
            responseArgs.add(executeTime + "ms");

            // 响应头
            HttpHeaders httpHeaders = response.getHeaders();
            httpHeaders.forEach((headerName, headerValue) -> {
                responseLog.append("=== Headers ===  {}: {}\n");
                responseArgs.add(headerName);
                responseArgs.add(StringUtils.collectionToCommaDelimitedString(headerValue));
            });

            responseLog.append("================  Gateway Response End  =================\n");
            // 打印响应信息
            log.info(responseLog.toString(), responseArgs.toArray());
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
