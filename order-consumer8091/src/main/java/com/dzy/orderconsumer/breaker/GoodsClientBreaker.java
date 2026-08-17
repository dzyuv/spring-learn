package com.dzy.orderconsumer.breaker;


import com.dzy.common.entity.ResultJSON;
import com.dzy.orderconsumer.client.GoodsClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GoodsClientBreaker implements GoodsClient {

    @Override
    public ResultJSON getById(Long gid) {
        return ResultJSON.error(503, "商品服务不可用");
    }

    @Override
    public ResultJSON list(int page, int size) {
        return ResultJSON.error(503, "商品服务不可用");
    }

    @Override
    public ResultJSON reduceStock(Map<String, Object> param) {
        return null;
    }
}
