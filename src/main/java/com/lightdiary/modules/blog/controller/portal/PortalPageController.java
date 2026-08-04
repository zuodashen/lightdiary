package com.lightdiary.modules.blog.controller.portal;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.model.BlogPage;
import com.lightdiary.modules.blog.service.BlogPageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "PortalPageController")
@RequestMapping("/api")
public class PortalPageController {

    @Autowired
    private BlogPageService blogPageService;

    @ApiOperation("自定义页面")
    @GetMapping("/pages/{slug}")
    public CommonResult<BlogPage> detail(@PathVariable String slug) {
        BlogPage page = blogPageService.getPublishedBySlug(slug);
        return page != null ? CommonResult.success(page) : CommonResult.failed("页面不存在");
    }
}
