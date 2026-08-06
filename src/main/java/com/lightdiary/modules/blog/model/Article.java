package com.lightdiary.modules.blog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("blog_article")
@ApiModel(value = "Article", description = "文章表")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("文章标题")
    private String title;

    @ApiModelProperty("URL友好标识")
    private String slug;

    @ApiModelProperty("文章摘要")
    private String summary;

    @ApiModelProperty("Markdown原始内容")
    private String content;

    @ApiModelProperty("渲染后的HTML")
    private String contentHtml;

    @ApiModelProperty("封面图URL")
    private String coverImage;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("状态：DRAFT/PUBLISHED")
    private String status;

    @ApiModelProperty("浏览量")
    private Integer views;

    @ApiModelProperty("正文字数")
    private Integer wordCount;

    @ApiModelProperty("预计阅读时长(分钟)")
    private Integer readingTime;

    @ApiModelProperty("是否置顶")
    private Integer isTop;

    @ApiModelProperty("是否允许评论")
    private Integer allowComment;

    @ApiModelProperty("发布时间")
    private Date publishTime;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
