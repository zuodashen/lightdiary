package com.lightdiary.modules.blog.dto;

import lombok.Data;

@Data
public class CategoryParam {
    private String name;
    private String slug;
    private String description;
    private Long parentId;
    private Integer sortOrder;
    private String icon;
}
