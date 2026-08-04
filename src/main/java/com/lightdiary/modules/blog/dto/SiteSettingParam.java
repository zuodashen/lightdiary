package com.lightdiary.modules.blog.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SiteSettingParam {
    private Map<String, String> settings;
}
