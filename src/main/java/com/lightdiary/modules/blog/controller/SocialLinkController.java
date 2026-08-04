package com.lightdiary.modules.blog.controller;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.model.SocialLink;
import com.lightdiary.modules.blog.service.SocialLinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(tags = "SocialLinkController")
@Tag(name = "SocialLinkController", description = "社交链接管理")
@RequestMapping("/socialLink")
public class SocialLinkController {

    @Autowired
    private SocialLinkService socialLinkService;

    @ApiOperation("创建社交链接")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody SocialLink link) {
        return socialLinkService.create(link) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除社交链接")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        return socialLinkService.removeById(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("社交链接列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<List<SocialLink>> list() {
        return CommonResult.success(socialLinkService.listAll());
    }
}
