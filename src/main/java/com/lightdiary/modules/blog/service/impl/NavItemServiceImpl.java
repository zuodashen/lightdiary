package com.lightdiary.modules.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightdiary.common.service.RedisService;
import com.lightdiary.modules.blog.dto.NavItemParam;
import com.lightdiary.modules.blog.mapper.NavItemMapper;
import com.lightdiary.modules.blog.model.NavItem;
import com.lightdiary.modules.blog.service.NavItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NavItemServiceImpl extends ServiceImpl<NavItemMapper, NavItem> implements NavItemService {

    private static final String CACHE_KEY = "blog:nav";

    @Autowired
    private RedisService redisService;

    @Override
    public boolean create(NavItemParam param) {
        NavItem item = new NavItem();
        BeanUtils.copyProperties(param, item);
        item.setParentId(param.getParentId() != null ? param.getParentId() : 0L);
        item.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        item.setIsExternal(param.getIsExternal() != null ? param.getIsExternal() : 0);
        boolean saved = save(item);
        if (saved) {
            redisService.del(CACHE_KEY);
        }
        return saved;
    }

    @Override
    public boolean update(Long id, NavItemParam param) {
        NavItem item = new NavItem();
        BeanUtils.copyProperties(param, item);
        item.setId(id);
        boolean updated = updateById(item);
        if (updated) {
            redisService.del(CACHE_KEY);
        }
        return updated;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<NavItem> listAll() {
        Object cached = redisService.get(CACHE_KEY);
        if (cached instanceof List) {
            return (List<NavItem>) cached;
        }
        List<NavItem> list = list(new QueryWrapper<NavItem>().orderByAsc("sort_order"));
        redisService.set(CACHE_KEY, list, 86400);
        return list;
    }
}
