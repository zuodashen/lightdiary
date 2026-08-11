package com.lightdiary.modules.blog.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GuestbookVO {
    private Long id;
    private Long parentId;
    private String authorName;
    private String authorEmail;
    private String authorAvatar;
    private String content;
    private Integer likes;
    private Date createTime;
    private List<GuestbookVO> children;
}
