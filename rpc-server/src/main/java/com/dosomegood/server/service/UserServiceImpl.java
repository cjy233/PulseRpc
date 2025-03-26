package com.dosomegood.server.service;

import cn.hutool.core.util.IdUtil;
import com.dosomegood.api.User;
import com.dosomegood.api.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(Long id) {
        return User.builder()
                .id(++id)
                .name(IdUtil.simpleUUID())
                .build();
    }
}
