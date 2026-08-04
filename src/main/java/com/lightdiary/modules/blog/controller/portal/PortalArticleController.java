package com.lightdiary.modules.blog.controller.portal;

import com.lightdiary.common.api.CommonPage;
import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.dto.ArticleQueryParam;
import com.lightdiary.modules.blog.service.ArticleService;
import com.lightdiary.modules.blog.service.CategoryService;
import com.lightdiary.modules.blog.service.TagService;
import com.lightdiary.modules.blog.vo.ArchiveVO;
import com.lightdiary.modules.blog.vo.ArticleDetailVO;
import com.lightdiary.modules.blog.vo.ArticleListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "PortalArticleController")
@RequestMapping("/api")
public class PortalArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TagService tagService;

    @ApiOperation("文章分页列表")
    @GetMapping("/articles")
    public CommonResult<CommonPage<ArticleListVO>> list(ArticleQueryParam param) {
        if (param.getPageNum() == null) {
            param.setPageNum(1);
        }
        if (param.getPageSize() == null) {
            param.setPageSize(10);
        }
        return CommonResult.success(CommonPage.restPage(articleService.listPublished(param)));
    }

    @ApiOperation("文章详情")
    @GetMapping("/articles/{slug}")
    public CommonResult<ArticleDetailVO> detail(@PathVariable String slug) {
        ArticleDetailVO detail = articleService.getPublishedBySlug(slug);
        return detail != null ? CommonResult.success(detail) : CommonResult.failed("文章不存在");
    }

    @ApiOperation("热门文章")
    @GetMapping("/articles/hot")
    public CommonResult<List<ArticleListVO>> hot(@RequestParam(defaultValue = "5") Integer limit) {
        return CommonResult.success(articleService.listHot(limit));
    }

    @ApiOperation("文章归档")
    @GetMapping("/articles/archives")
    public CommonResult<List<ArchiveVO>> archives() {
        return CommonResult.success(articleService.listArchives());
    }

    @ApiOperation("分类列表")
    @GetMapping("/categories")
    public CommonResult<?> categories() {
        return CommonResult.success(categoryService.listWithCount());
    }

    @ApiOperation("标签列表")
    @GetMapping("/tags")
    public CommonResult<?> tags() {
        return CommonResult.success(tagService.listWithCount());
    }
}
