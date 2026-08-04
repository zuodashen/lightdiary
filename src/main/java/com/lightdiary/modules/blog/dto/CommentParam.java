package com.lightdiary.modules.blog.dto;

import lombok.Data;

@Data
public class CommentParam {
    private String authorName;
    private String authorEmail;
    private String content;
    private Long parentId;
}
