package com.dzy.goodsprovider8090.services.impl;

import com.dzy.common.entity.Goods;
import com.dzy.common.exception.BusinessException;
import com.dzy.goodsprovider8090.mapper.GoodsMapper;
import com.dzy.goodsprovider8090.services.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodServiceImpl implements GoodService {
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public Goods getById(long gid) {
        if(gid > 0 ){
            return goodsMapper.selectById(gid);
        }
        else{
            throw new BusinessException("无商品id");
        }
    }
}
