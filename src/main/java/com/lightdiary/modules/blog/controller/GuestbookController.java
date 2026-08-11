package com.lightdiary.modules.blog.controller;

import com.lightdiary.common.api.CommonPage;
import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.model.Guestbook;
import com.lightdiary.modules.blog.service.GuestbookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Api(tags = "GuestbookController")
@Tag(name = "GuestbookController", description = "留言板管理")
@RequestMapping("/guestbook")
public class GuestbookController {

    @Autowired
    private GuestbookService guestbookService;

    @ApiOperation("留言列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<Guestbook>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        return CommonResult.success(CommonPage.restPage(guestbookService.listAdmin(pageNum, pageSize, status)));
    }

    @ApiOperation("审核通过")
    @PostMapping("/approve/{id}")
    @ResponseBody
    public CommonResult approve(@PathVariable Long id) {
        return guestbookService.approve(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("拒绝留言")
    @PostMapping("/reject/{id}")
    @ResponseBody
    public CommonResult reject(@PathVariable Long id) {
        return guestbookService.reject(id) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("删除留言")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        return guestbookService.removeById(id) ? CommonResult.success(null) : CommonResult.failed();
    }
}
