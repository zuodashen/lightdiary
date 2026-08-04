package com.lightdiary.modules.blog.dto;

import lombok.Data;

@Data
public class BookmarkParam {
    private Long categoryId;
    private String name;
    private String link;
    private String description;
    private String image;
    private Integer sortOrder;
}
