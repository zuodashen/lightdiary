package com.lightdiary.modules.blog.controller.portal;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.service.BookmarkService;
import com.lightdiary.modules.blog.vo.BookmarkGroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "PortalBookmarkController")
@RequestMapping("/api")
public class PortalBookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @ApiOperation("书签列表")
    @GetMapping("/bookmarks")
    public CommonResult<List<BookmarkGroupVO>> list() {
        return CommonResult.success(bookmarkService.listGrouped());
    }
}
