package com.lightdiary.modules.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightdiary.common.service.RedisService;
import com.lightdiary.modules.blog.mapper.SocialLinkMapper;
import com.lightdiary.modules.blog.model.SocialLink;
import com.lightdiary.modules.blog.service.SocialLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocialLinkServiceImpl extends ServiceImpl<SocialLinkMapper, SocialLink> implements SocialLinkService {

    private static final String CACHE_KEY = "blog:social";

    @Autowired
    private RedisService redisService;

    @Override
    public boolean create(SocialLink link) {
        boolean saved = save(link);
        if (saved) {
            redisService.del(CACHE_KEY);
        }
        return saved;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SocialLink> listAll() {
        Object cached = redisService.get(CACHE_KEY);
        if (cached instanceof List) {
            return (List<SocialLink>) cached;
        }
        List<SocialLink> list = list(new QueryWrapper<SocialLink>().orderByAsc("sort_order"));
        redisService.set(CACHE_KEY, list, 86400);
        return list;
    }
}
