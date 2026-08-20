package com.dzy.orderconsumer.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;

import com.dzy.common.entity.ResultJSON;
import com.dzy.orderconsumer.client.GoodsClient;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RefreshScope
@RequestMapping("/order")
@EnableDiscoveryClient
public class OrderController {

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/sendMsg")
    public ResultJSON sendMsg(@RequestParam String message) {
        // 发送消息到 Topic: test-topic
        rocketMQTemplate.convertAndSend("test-topic", message);
        System.out.println("消息已发送: " + message);
        return ResultJSON.success("消息发送成功: " + message);
    }

//    @GetMapping("/getGoods/{gid}")
//    @SentinelResource(value = "getGoods", fallback = "getGoodsFallback")
//    public ResultJSON getGoods(@PathVariable Long gid) {
//        return goodsClient.getById(gid);
//    }
//
//    public ResultJSON getGoodsFallback(Long id, Throwable e) {
//        System.out.println("降级触发，异常信息：" + e.getMessage());
//        return ResultJSON.error(503, "商品服务挂了，请稍后重试");
//    }
    @GetMapping("/getGoods/{gid}")
    public ResultJSON getGoods(@PathVariable Long gid) {
        // 1. 模拟异常测试（异常比例）：当传入 id=0 时，故意抛出异常
        if (gid == 0) {
            throw new RuntimeException("模拟商品服务异常");
        }

        // 2. 模拟慢调用测试（慢调用比例）：每个请求强制休眠 3 秒，超过 RT 阈值
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return goodsClient.getById(gid);
    }


    @GetMapping("/goodsList")
    public ResultJSON goodsList(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "5") int size) {
        return goodsClient.list(page, size);
    }

    @PostMapping("/create")
    @GlobalTransactional(name = "create-order", rollbackFor = Exception.class)
    public ResultJSON createOrder(@RequestBody Map<String, Object> param) {
        Long goodsId = Long.valueOf(param.get("goodsId").toString());
        int count = Integer.parseInt(param.get("count").toString());

        System.out.println("订单创建，商品ID：" + goodsId + "，数量：" + count);

        Map<String, Object> req = new HashMap<>();
        req.put("gid", goodsId);
        req.put("count", count);
        goodsClient.reduceStock(req);

        return ResultJSON.success("下单成功");
    }
}