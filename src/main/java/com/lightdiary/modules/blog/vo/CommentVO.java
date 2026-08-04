package com.lightdiary.modules.blog.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentVO {
    private Long id;
    private Long articleId;
    private Long parentId;
    private String authorName;
    private String authorEmail;
    private String authorAvatar;
    private String content;
    private Date createTime;
    private List<CommentVO> children;
}
