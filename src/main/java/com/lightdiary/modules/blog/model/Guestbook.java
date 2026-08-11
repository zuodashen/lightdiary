package com.lightdiary.modules.blog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("blog_guestbook")
@ApiModel(value = "Guestbook", description = "留言板")
public class Guestbook implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long parentId;
    private String authorName;
    private String authorEmail;
    private String authorAvatar;
    private String content;
    private Integer likes;
    private String status;
    private String ipAddress;
    private String userAgent;
    private Date createTime;
}
