package com.dzy.gateway.filter;

import com.dzy.gateway.util.GatewayJwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private final GatewayJwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 白名单路径（POST /users 用于注册）
    private static final Set<String> WHITE_LIST = Set.of("/users/login");
    private static final String REGISTER_PATH = "/users";
    private static final HttpMethod REGISTER_METHOD = HttpMethod.POST;

    public AuthFilter(GatewayJwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // 1. 白名单放行
        if (WHITE_LIST.contains(path) || (REGISTER_PATH.equals(path) && REGISTER_METHOD == method)) {
            return chain.filter(exchange);
        }

        // 2. 提取 Token
        String token = extractToken(exchange);
        if (token == null || !jwtUtil.validateToken(token)) {
            return unauthorized(exchange, "请先登录");
        }

        // 3. 解析身份信息
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);

        // 4. 向下游传递身份头
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", userId.toString())
                .header("X-Username", username)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private String extractToken(ServerWebExchange exchange) {
        // 优先从标准 Authorization: Bearer xxx 提取，否则回退到自定义 token 头
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return exchange.getRequest().getHeaders().getFirst("token");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(Map.of("code", 401, "msg", message));
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100; // 高优先级
    }
}