package com.lightdiary.modules.blog.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ArticleListVO {
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private List<TagRefVO> tags;
    private Integer views;
    private Integer wordCount;
    private Integer readingTime;
    private Integer isTop;
    private Date publishTime;
    private Date createTime;
}
