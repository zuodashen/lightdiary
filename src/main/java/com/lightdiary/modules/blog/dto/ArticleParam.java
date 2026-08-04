package com.lightdiary.modules.blog.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ArticleParam {

    @ApiModelProperty("文章标题")
    private String title;

    @ApiModelProperty("URL标识")
    private String slug;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("Markdown内容")
    private String content;

    @ApiModelProperty("封面图")
    private String coverImage;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("标签ID列表")
    private List<Long> tagIds;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("是否置顶")
    private Integer isTop;

    @ApiModelProperty("是否允许评论")
    private Integer allowComment;
}
