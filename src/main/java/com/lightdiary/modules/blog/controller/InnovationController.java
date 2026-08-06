package com.lightdiary.modules.blog.controller;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.dto.InnovationParam;
import com.lightdiary.modules.blog.model.Innovation;
import com.lightdiary.modules.blog.service.InnovationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(tags = "InnovationController")
@Tag(name = "InnovationController", description = "微光实验室管理")
@RequestMapping("/innovation")
public class InnovationController {

    @Autowired
    private InnovationService innovationService;

    @ApiOperation("创建项目")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody InnovationParam param) {
        return innovationService.create(param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("更新项目")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody InnovationParam param) {
        return innovationService.update(id, param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除项目")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        return innovationService.removeById(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("项目列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<List<Innovation>> list() {
        return CommonResult.success(innovationService.list(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Innovation>()
                        .orderByDesc("is_featured")
                        .orderByAsc("sort_order")
                        .orderByDesc("create_time")));
    }
}
