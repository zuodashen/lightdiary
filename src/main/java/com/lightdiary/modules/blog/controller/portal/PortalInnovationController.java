package com.lightdiary.modules.blog.controller.portal;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.model.Innovation;
import com.lightdiary.modules.blog.service.InnovationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "PortalInnovationController")
@RequestMapping("/api")
public class PortalInnovationController {

    @Autowired
    private InnovationService innovationService;

    @ApiOperation("微光实验室项目列表")
    @GetMapping("/innovations")
    public CommonResult<List<Innovation>> list() {
        return CommonResult.success(innovationService.listPublished());
    }
}
