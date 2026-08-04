package com.lightdiary.modules.blog.dto;

import lombok.Data;

@Data
public class PageParam {
    private String title;
    private String slug;
    private String content;
    private String template;
    private String status;
    private Integer sortOrder;
}
