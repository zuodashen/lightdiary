package com.lightdiary.modules.blog.vo;

import lombok.Data;

@Data
public class SiteStatsVO {
    private Long articleCount;
    private Long totalViews;
    private Long totalWords;
    private Integer categoryCount;
    private Integer tagCount;
    private Long runningDays;
    private Long messageCount;
}
