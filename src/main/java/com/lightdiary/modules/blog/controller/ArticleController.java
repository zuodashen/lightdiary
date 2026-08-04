package com.lightdiary.modules.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lightdiary.common.api.CommonPage;
import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.dto.ArticleParam;
import com.lightdiary.modules.blog.dto.ArticleQueryParam;
import com.lightdiary.modules.blog.model.Article;
import com.lightdiary.modules.blog.service.ArticleService;
import com.lightdiary.modules.blog.vo.ArticleDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Api(tags = "ArticleController")
@Tag(name = "ArticleController", description = "文章管理")
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("创建文章")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody ArticleParam param) {
        return articleService.create(param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("更新文章")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody ArticleParam param) {
        return articleService.update(id, param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除文章")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        return articleService.delete(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("文章列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<Article>> list(ArticleQueryParam param) {
        Page<Article> page = articleService.listAdmin(param);
        return CommonResult.success(CommonPage.restPage(page));
    }

    @ApiOperation("文章详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<ArticleDetailVO> getItem(@PathVariable Long id) {
        ArticleDetailVO detail = articleService.getDetail(id);
        return detail != null ? CommonResult.success(detail) : CommonResult.failed("文章不存在");
    }

    @ApiOperation("发布文章")
    @PostMapping("/publish/{id}")
    @ResponseBody
    public CommonResult publish(@PathVariable Long id) {
        return articleService.publish(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("转为草稿")
    @PostMapping("/draft/{id}")
    @ResponseBody
    public CommonResult draft(@PathVariable Long id) {
        return articleService.draft(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("置顶/取消置顶")
    @PostMapping("/top/{id}")
    @ResponseBody
    public CommonResult top(@PathVariable Long id) {
        return articleService.toggleTop(id) ? CommonResult.success(null) : CommonResult.failed();
    }
}
