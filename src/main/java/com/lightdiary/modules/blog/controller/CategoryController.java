package com.lightdiary.modules.blog.controller;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.dto.CategoryParam;
import com.lightdiary.modules.blog.model.Category;
import com.lightdiary.modules.blog.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(tags = "CategoryController")
@Tag(name = "CategoryController", description = "分类管理")
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation("创建分类")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody CategoryParam param) {
        return categoryService.create(param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("更新分类")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody CategoryParam param) {
        return categoryService.update(id, param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除分类")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        return categoryService.removeById(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("分类列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<List<Category>> list() {
        return CommonResult.success(categoryService.list());
    }
}
