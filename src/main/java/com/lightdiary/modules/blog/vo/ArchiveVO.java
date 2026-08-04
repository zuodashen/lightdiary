package com.lightdiary.modules.blog.vo;

import lombok.Data;

import java.util.List;

@Data
public class ArchiveVO {
    private Integer year;
    private Integer month;
    private Integer count;
    private List<ArticleListVO> articles;
}
