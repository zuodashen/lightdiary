package com.lightdiary.modules.blog.controller;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Api(tags = "TagController")
@io.swagger.v3.oas.annotations.tags.Tag(name = "TagController", description = "标签管理")
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @ApiOperation("创建标签")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody Map<String, String> body) {
        return tagService.create(body.get("name")) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除标签")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        return tagService.removeById(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("标签列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<List<com.lightdiary.modules.blog.model.Tag>> list() {
        return CommonResult.success(tagService.list());
    }
}
