package com.lightdiary.modules.blog.controller.portal;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.model.CommentConfig;
import com.lightdiary.modules.blog.model.NavItem;
import com.lightdiary.modules.blog.model.SocialLink;
import com.lightdiary.modules.blog.service.CommentService;
import com.lightdiary.modules.blog.service.NavItemService;
import com.lightdiary.modules.blog.service.SiteSettingService;
import com.lightdiary.modules.blog.service.SocialLinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "PortalSiteController")
@RequestMapping("/api")
public class PortalSiteController {

    @Autowired
    private NavItemService navItemService;
    @Autowired
    private SocialLinkService socialLinkService;
    @Autowired
    private SiteSettingService siteSettingService;
    @Autowired
    private CommentService commentService;

    @ApiOperation("导航菜单")
    @GetMapping("/nav")
    public CommonResult<List<NavItem>> nav() {
        return CommonResult.success(navItemService.listAll());
    }

    @ApiOperation("社交链接")
    @GetMapping("/social")
    public CommonResult<List<SocialLink>> social() {
        return CommonResult.success(socialLinkService.listAll());
    }

    @ApiOperation("站点设置")
    @GetMapping("/settings")
    public CommonResult<Map<String, String>> settings() {
        return CommonResult.success(siteSettingService.getPublicSettings());
    }

    @ApiOperation("评论系统配置")
    @GetMapping("/comment/config")
    public CommonResult<CommentConfig> commentConfig() {
        return CommonResult.success(commentService.getConfig());
    }
}
