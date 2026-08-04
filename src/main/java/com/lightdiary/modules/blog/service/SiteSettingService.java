package com.lightdiary.modules.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightdiary.modules.blog.model.SiteSetting;

import java.util.List;
import java.util.Map;

public interface SiteSettingService extends IService<SiteSetting> {

    Map<String, String> getPublicSettings();

    boolean updateBatch(Map<String, String> settings);

    List<SiteSetting> listAll();
}
