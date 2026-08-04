package com.lightdiary.modules.blog.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long parentId;
    private Integer sortOrder;
    private String icon;
    private Integer articleCount;
    private Date createTime;
}
