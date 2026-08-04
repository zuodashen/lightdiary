package com.lightdiary.modules.blog.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TagVO {
    private Long id;
    private String name;
    private String slug;
    private Integer articleCount;
    private Date createTime;
}
