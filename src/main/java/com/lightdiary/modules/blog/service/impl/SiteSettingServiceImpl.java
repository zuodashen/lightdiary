package com.lightdiary.modules.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightdiary.common.service.RedisService;
import com.lightdiary.modules.blog.mapper.SiteSettingMapper;
import com.lightdiary.modules.blog.model.SiteSetting;
import com.lightdiary.modules.blog.service.SiteSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SiteSettingServiceImpl extends ServiceImpl<SiteSettingMapper, SiteSetting> implements SiteSettingService {

    private static final String CACHE_KEY = "blog:settings";

    @Autowired
    private RedisService redisService;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getPublicSettings() {
        Object cached = redisService.get(CACHE_KEY);
        if (cached instanceof Map) {
            return (Map<String, String>) cached;
        }
        List<SiteSetting> settings = list();
        Map<String, String> map = new HashMap<>();
        for (SiteSetting setting : settings) {
            map.put(setting.getSettingKey(), setting.getSettingValue());
        }
        redisService.set(CACHE_KEY, map, 86400);
        return map;
    }

    @Override
    public boolean updateBatch(Map<String, String> settings) {
        if (settings == null || settings.isEmpty()) {
            return false;
        }
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            SiteSetting existing = getOne(new QueryWrapper<SiteSetting>().eq("setting_key", entry.getKey()));
            if (existing != null) {
                existing.setSettingValue(entry.getValue());
                updateById(existing);
            } else {
                SiteSetting setting = new SiteSetting();
                setting.setSettingKey(entry.getKey());
                setting.setSettingValue(entry.getValue());
                save(setting);
            }
        }
        redisService.del(CACHE_KEY);
        return true;
    }

    @Override
    public List<SiteSetting> listAll() {
        return list();
    }
}
