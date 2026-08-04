package com.lightdiary.modules.blog.controller;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.dto.BookmarkParam;
import com.lightdiary.modules.blog.model.Bookmark;
import com.lightdiary.modules.blog.service.BookmarkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(tags = "BookmarkController")
@Tag(name = "BookmarkController", description = "书签管理")
@RequestMapping("/bookmark")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @ApiOperation("创建书签")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody BookmarkParam param) {
        return bookmarkService.create(param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("更新书签")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody BookmarkParam param) {
        return bookmarkService.update(id, param) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除书签")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        return bookmarkService.removeById(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("书签列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<List<Bookmark>> list() {
        return CommonResult.success(bookmarkService.list());
    }
}
