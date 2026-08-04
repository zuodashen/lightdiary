package com.lightdiary.modules.blog.controller.portal;

import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.dto.CommentParam;
import com.lightdiary.modules.blog.service.CommentService;
import com.lightdiary.modules.blog.vo.CommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "PortalCommentController")
@RequestMapping("/api")
public class PortalCommentController {

    @Autowired
    private CommentService commentService;

    @ApiOperation("文章评论列表")
    @GetMapping("/articles/{id}/comments")
    public CommonResult<List<CommentVO>> list(@PathVariable Long id) {
        return CommonResult.success(commentService.listByArticle(id));
    }

    @ApiOperation("提交评论")
    @PostMapping("/articles/{id}/comments")
    public CommonResult submit(@PathVariable Long id, @RequestBody CommentParam param, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        return commentService.submit(id, param, ip, ua) ? CommonResult.success(null) : CommonResult.failed();
    }
}
