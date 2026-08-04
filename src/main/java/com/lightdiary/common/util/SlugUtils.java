package com.lightdiary.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;

public final class SlugUtils {

    private SlugUtils() {
    }

    public static String generateSlug(String title) {
        if (StrUtil.isBlank(title)) {
            return "post";
        }
        String pinyin = PinyinUtil.getPinyin(title, "-");
        return pinyin.toLowerCase()
                .replaceAll("[^a-z0-9\\-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
