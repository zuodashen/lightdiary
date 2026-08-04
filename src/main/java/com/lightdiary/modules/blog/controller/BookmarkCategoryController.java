package com.lightdiary.modules.blog.controller;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.mapper.BookmarkCategoryMapper;
import com.lightdiary.modules.blog.model.BookmarkCategory;
import com.lightdiary.modules.blog.service.BookmarkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Api(tags = "BookmarkCategoryController")
@Tag(name = "BookmarkCategoryController", description = "书签分类管理")
@RequestMapping("/bookmarkCategory")
public class BookmarkCategoryController {

    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private BookmarkCategoryMapper bookmarkCategoryMapper;

    @ApiOperation("创建书签分类")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String icon = (String) body.get("icon");
        Integer sortOrder = body.get("sortOrder") != null ? (Integer) body.get("sortOrder") : 0;
        return bookmarkService.createCategory(name, icon, sortOrder) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除书签分类")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        return bookmarkCategoryMapper.deleteById(id) > 0 ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("书签分类列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<List<BookmarkCategory>> list() {
        return CommonResult.success(bookmarkService.listCategories());
    }
}
