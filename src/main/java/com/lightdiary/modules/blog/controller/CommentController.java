package com.lightdiary.modules.blog.controller;

import com.lightdiary.common.api.CommonPage;
import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.model.Comment;
import com.lightdiary.modules.blog.model.CommentConfig;
import com.lightdiary.modules.blog.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Api(tags = "CommentController")
@Tag(name = "CommentController", description = "评论管理")
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("评论列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<Comment>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        return CommonResult.success(CommonPage.restPage(commentService.listAdmin(pageNum, pageSize, status)));
    }

    @ApiOperation("审核通过")
    @PostMapping("/approve/{id}")
    @ResponseBody
    public CommonResult approve(@PathVariable Long id) {
        return commentService.approve(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("拒绝评论")
    @PostMapping("/reject/{id}")
    @ResponseBody
    public CommonResult reject(@PathVariable Long id) {
        return commentService.reject(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除评论")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        return commentService.removeById(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("获取评论系统配置")
    @GetMapping("/config")
    @ResponseBody
    public CommonResult<CommentConfig> getConfig() {
        return CommonResult.success(commentService.getConfig());
    }

    @ApiOperation("更新评论系统配置")
    @PostMapping("/config/update")
    @ResponseBody
    public CommonResult updateConfig(@RequestBody CommentConfig config) {
        return commentService.updateConfig(config) ? CommonResult.success(null) : CommonResult.failed();
    }
}
