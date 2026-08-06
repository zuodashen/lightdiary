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
@TableName("blog_innovation")
@ApiModel(value = "Innovation", description = "微光实验室创新项目")
public class Innovation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;
    private String slug;
    private String summary;
    private String description;
    private String coverImage;
    private String demoUrl;
    private String githubUrl;
    private String techStack;
    private String status;
    private Integer isFeatured;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;
}
