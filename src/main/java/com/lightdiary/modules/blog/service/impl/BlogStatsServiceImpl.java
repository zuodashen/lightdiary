package com.lightdiary.modules.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.lightdiary.common.service.RedisService;
import com.lightdiary.modules.blog.mapper.ArticleMapper;
import com.lightdiary.modules.blog.model.Article;
import com.lightdiary.modules.blog.service.BlogStatsService;
import com.lightdiary.modules.blog.service.CategoryService;
import com.lightdiary.modules.blog.service.SiteSettingService;
import com.lightdiary.modules.blog.service.TagService;
import com.lightdiary.modules.blog.vo.SiteStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class BlogStatsServiceImpl implements BlogStatsService {

    private static final String VIEW_KEY_PREFIX = "blog:article:views:";

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;
    @Autowired
    private SiteSettingService siteSettingService;
    @Autowired
    private RedisService redisService;

    @Override
    public SiteStatsVO getSiteStats() {
        SiteStatsVO stats = new SiteStatsVO();
        stats.setArticleCount(articleMapper.countPublished());
        stats.setTotalWords(articleMapper.sumPublishedWordCount());
        stats.setCategoryCount(categoryService.listWithCount().size());
        stats.setTagCount(tagService.listWithCount().size());
        stats.setTotalViews(calculateTotalViews());
        stats.setRunningDays(calculateRunningDays());
        return stats;
    }

    private long calculateTotalViews() {
        long total = articleMapper.sumPublishedViews();
        List<Article> published = articleMapper.listPublishedIds();
        for (Article article : published) {
            Object cached = redisService.get(VIEW_KEY_PREFIX + article.getId());
            if (cached != null) {
                total += Long.parseLong(cached.toString());
            }
        }
        return total;
    }

    private long calculateRunningDays() {
        Map<String, String> settings = siteSettingService.getPublicSettings();
        String startTime = settings.get("site_start_time");
        if (StrUtil.isBlank(startTime)) {
            return 0L;
        }
        try {
            LocalDateTime start = LocalDateTime.parse(startTime.trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return Math.max(0, ChronoUnit.DAYS.between(start.toLocalDate(), LocalDate.now()));
        } catch (Exception e) {
            return 0L;
        }
    }
}
