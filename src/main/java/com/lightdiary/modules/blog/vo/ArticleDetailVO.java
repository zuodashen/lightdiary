package com.lightdiary.modules.blog.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleDetailVO extends ArticleListVO {
    private String content;
    private String contentHtml;
    private Boolean allowComment;
    private Date updateTime;
}
