package com.dzy.common.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomeBlockExceptionHandler implements BlockExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       BlockException e) throws IOException {
        // 设置响应状态码（默认 429 Too Many Requests）
        response.setStatus(429);
        response.setContentType("application/json;charset=UTF-8");

        // 构建统一的错误响应体
        Map<String, Object> result = new HashMap<>();
        result.put("code", 429);
        result.put("timestamp", System.currentTimeMillis());

        // 根据异常类型给出更具体的提示
        if (e instanceof FlowException) {
            result.put("message", "接口限流，请稍后重试");
        } else if (e instanceof DegradeException) {
            result.put("message", "服务熔断降级，请稍后重试");
        } else if (e instanceof ParamFlowException) {
            result.put("message", "热点参数限流");
        } else if (e instanceof SystemBlockException) {
            result.put("message", "系统保护触发，请稍后重试");
        } else if (e instanceof AuthorityException) {
            result.put("message", "授权规则不允许访问");
        } else {
            result.put("message", "请求被限流");
        }
        // 写入响应
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}