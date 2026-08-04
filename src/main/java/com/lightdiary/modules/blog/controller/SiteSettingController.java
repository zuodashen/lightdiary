package com.lightdiary.modules.blog.controller;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.dto.SiteSettingParam;
import com.lightdiary.modules.blog.model.SiteSetting;
import com.lightdiary.modules.blog.service.SiteSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(tags = "SiteSettingController")
@Tag(name = "SiteSettingController", description = "站点设置管理")
@RequestMapping("/siteSetting")
public class SiteSettingController {

    @Autowired
    private SiteSettingService siteSettingService;

    @ApiOperation("设置列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<List<SiteSetting>> list() {
        return CommonResult.success(siteSettingService.listAll());
    }

    @ApiOperation("批量更新设置")
    @PostMapping("/update")
    @ResponseBody
    public CommonResult update(@RequestBody SiteSettingParam param) {
        return siteSettingService.updateBatch(param.getSettings()) ? CommonResult.success(null) : CommonResult.failed();
    }
}
