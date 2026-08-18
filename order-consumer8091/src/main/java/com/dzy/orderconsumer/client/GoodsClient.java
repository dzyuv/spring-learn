package com.dzy.orderconsumer.client;

import com.dzy.common.entity.ResultJSON;
import com.dzy.orderconsumer.breaker.GoodsClientBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "GOODS-PROVIDER8090",fallback = GoodsClientBreaker.class)   // 服务名
public interface GoodsClient {

    @GetMapping("/goods/get/{gid}")
    ResultJSON getById(@PathVariable Long gid);

    @GetMapping("/goods/list")
    ResultJSON list(@RequestParam("page") int page,@RequestParam("size") int size);

    @PostMapping("/goods/reduceStock")
    ResultJSON reduceStock(@RequestBody Map<String, Object> param);
}