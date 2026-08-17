package com.dzy.goodsprovider8090.controller;

import com.dzy.common.entity.Goods;
import com.dzy.common.entity.ResultJSON;
import com.dzy.goodsprovider8090.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RefreshScope
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsMapper goodsMapper;

    @GetMapping("/get/{gid}")
    public ResultJSON getGoodById(@PathVariable long gid) {
        Goods goods = goodsMapper.selectById(gid);
        if (goods != null) {
            return ResultJSON.success(goods);
        }
        return ResultJSON.error(404, "商品不存在");
    }

    @GetMapping("/list")
    public ResultJSON getGoods(@RequestParam int page, @RequestParam int size) {
        List<Goods> all = goodsMapper.selectAll();
        int start = (page - 1) * size;
        int end = Math.min(start + size, all.size());

        Map<String, Object> map = new HashMap<>();
        map.put("list", all.subList(start, end));
        map.put("total", all.size());
        map.put("page", page);
        map.put("size", size);
        return ResultJSON.success(map);
    }

    @PostMapping("/reduceStock")
    public ResultJSON reduceStock(@RequestBody Map<String, Object> param) {
        Long gid = Long.valueOf(param.get("gid").toString());
        int count = Integer.parseInt(param.get("count").toString());

        Goods goods = goodsMapper.selectById(gid);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        if (goods.getStock() < count) {
            throw new RuntimeException("库存不足，当前库存：" + goods.getStock());
        }
        // 扣减库存
        goodsMapper.updateStock(gid, goods.getStock() - count);
        return ResultJSON.success("扣减成功");
    }
}