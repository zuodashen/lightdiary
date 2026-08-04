package com.lightdiary.modules.blog.controller;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.dto.NavItemParam;
import com.lightdiary.modules.blog.model.NavItem;
import com.lightdiary.modules.blog.service.NavItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(tags = "NavItemController")
@Tag(name = "NavItemController", description = "导航管理")
@RequestMapping("/navItem")
public class NavItemController {

    @Autowired
    private NavItemService navItemService;

    @ApiOperation("创建导航项")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody NavItemParam param) {
        return navItemService.create(param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("更新导航项")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody NavItemParam param) {
        return navItemService.update(id, param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除导航项")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        return navItemService.removeById(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("导航列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<List<NavItem>> list() {
        return CommonResult.success(navItemService.listAll());
    }
}
