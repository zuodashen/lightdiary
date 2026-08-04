package com.lightdiary.modules.blog.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ArticleQueryParam {

    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @ApiModelProperty("每页数量")
    private Integer pageSize = 10;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("标签ID")
    private Long tagId;

    @ApiModelProperty("关键词")
    private String keyword;

    @ApiModelProperty("状态")
    private String status;
}
