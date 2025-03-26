package com.dosomegood.rpc.loadbalance.impl;

import cn.hutool.core.util.RandomUtil;
import com.dosomegood.rpc.loadbalance.LoadBalance;

import java.util.List;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> list) {
        return RandomUtil.randomEle(list);
    }
}
