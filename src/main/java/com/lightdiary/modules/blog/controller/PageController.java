package com.lightdiary.modules.blog.controller;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.dto.PageParam;
import com.lightdiary.modules.blog.model.BlogPage;
import com.lightdiary.modules.blog.service.BlogPageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(tags = "PageController")
@Tag(name = "PageController", description = "页面管理")
@RequestMapping("/page")
public class PageController {

    @Autowired
    private BlogPageService blogPageService;

    @ApiOperation("创建页面")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody PageParam param) {
        return blogPageService.create(param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("更新页面")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody PageParam param) {
        return blogPageService.update(id, param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除页面")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        return blogPageService.removeById(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("页面列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<List<BlogPage>> list() {
        return CommonResult.success(blogPageService.list());
    }
}
