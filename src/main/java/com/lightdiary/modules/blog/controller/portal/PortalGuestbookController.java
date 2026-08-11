package com.lightdiary.modules.blog.controller.portal;

import com.lightdiary.common.api.CommonPage;
import com.lightdiary.common.api.CommonResult;
import com.lightdiary.modules.blog.dto.GuestbookParam;
import com.lightdiary.modules.blog.service.GuestbookService;
import com.lightdiary.modules.blog.vo.GuestbookVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "PortalGuestbookController")
@RequestMapping("/api/guestbook")
public class PortalGuestbookController {

    @Autowired
    private GuestbookService guestbookService;

    @ApiOperation("留言列表")
    @GetMapping
    public CommonResult<CommonPage<GuestbookVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<GuestbookVO> list = guestbookService.listPublic(pageNum, pageSize);
        long total = guestbookService.countPublic();
        int totalPage = (int) Math.ceil(total / (double) pageSize);
        CommonPage<GuestbookVO> page = new CommonPage<>();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setTotal(total);
        page.setTotalPage(Math.max(1, totalPage));
        page.setList(list);
        return CommonResult.success(page);
    }

    @ApiOperation("提交留言")
    @PostMapping
    public CommonResult submit(@RequestBody GuestbookParam param, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        return guestbookService.submit(param, ip, ua) ? CommonResult.success(null) : CommonResult.failed();
    }

    @ApiOperation("点赞留言")
    @PostMapping("/{id}/like")
    public CommonResult like(@PathVariable Long id, HttpServletRequest request) {
        return guestbookService.like(id, request.getRemoteAddr())
                ? CommonResult.success(null)
                : CommonResult.failed();
    }

    @ApiOperation("今日剩余留言次数")
    @GetMapping("/remaining")
    public CommonResult<Map<String, Integer>> remaining(HttpServletRequest request) {
        Map<String, Integer> data = new HashMap<>(1);
        data.put("remaining", guestbookService.getRemainingDaily(request.getRemoteAddr()));
        return CommonResult.success(data);
    }
}
