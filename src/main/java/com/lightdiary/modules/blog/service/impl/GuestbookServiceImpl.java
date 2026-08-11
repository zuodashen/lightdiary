package com.lightdiary.modules.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightdiary.common.exception.Asserts;
import com.lightdiary.common.service.RedisService;
import com.lightdiary.modules.blog.dto.GuestbookParam;
import com.lightdiary.modules.blog.mapper.GuestbookMapper;
import com.lightdiary.modules.blog.model.Guestbook;
import com.lightdiary.modules.blog.service.GuestbookService;
import com.lightdiary.modules.blog.vo.GuestbookVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GuestbookServiceImpl extends ServiceImpl<GuestbookMapper, Guestbook> implements GuestbookService {

    private static final String DAILY_KEY_PREFIX = "blog:guestbook:daily:";
    private static final String LIKE_KEY_PREFIX = "blog:guestbook:like:";

    @Value("${blog.guestbook.default-status:APPROVED}")
    private String defaultStatus;

    @Value("${blog.guestbook.daily-limit:3}")
    private int dailyLimit;

    @Value("${blog.guestbook.max-length:500}")
    private int maxLength;

    @Autowired
    private RedisService redisService;

    @Override
    public List<GuestbookVO> listPublic(Integer pageNum, Integer pageSize) {
        Page<Guestbook> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Guestbook> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "APPROVED")
                .eq("parent_id", 0)
                .orderByDesc("create_time");
        page(page, wrapper);

        List<GuestbookVO> roots = new ArrayList<>();
        for (Guestbook root : page.getRecords()) {
            GuestbookVO vo = toVo(root);
            List<Guestbook> replies = list(new QueryWrapper<Guestbook>()
                    .eq("parent_id", root.getId())
                    .eq("status", "APPROVED")
                    .orderByAsc("create_time"));
            vo.setChildren(replies.stream().map(this::toVo).collect(java.util.stream.Collectors.toList()));
            roots.add(vo);
        }
        return roots;
    }

    @Override
    public long countPublic() {
        return count(new QueryWrapper<Guestbook>()
                .eq("status", "APPROVED")
                .eq("parent_id", 0));
    }

    @Override
    public boolean submit(GuestbookParam param, String ip, String userAgent) {
        validateParam(param);
        if (getRemainingDaily(ip) <= 0) {
            Asserts.fail("今日留言次数已达上限");
        }

        Guestbook message = new Guestbook();
        message.setParentId(param.getParentId() != null ? param.getParentId() : 0L);
        message.setAuthorName(param.getAuthorName().trim());
        message.setAuthorEmail(StrUtil.isNotBlank(param.getAuthorEmail()) ? param.getAuthorEmail().trim() : null);
        message.setContent(param.getContent().trim());
        message.setLikes(0);
        message.setStatus(defaultStatus);
        message.setIpAddress(ip);
        message.setUserAgent(userAgent);
        message.setCreateTime(new Date());
        if (StrUtil.isNotBlank(param.getAuthorEmail())) {
            String hash = DigestUtil.md5Hex(param.getAuthorEmail().trim().toLowerCase());
            message.setAuthorAvatar("https://www.gravatar.com/avatar/" + hash + "?d=identicon");
        } else {
            message.setAuthorAvatar(buildLetterAvatar(param.getAuthorName()));
        }

        boolean saved = save(message);
        if (saved) {
            incrementDailyCount(ip);
        }
        return saved;
    }

    @Override
    public boolean like(Long id, String ip) {
        Guestbook existing = getById(id);
        if (existing == null || !"APPROVED".equals(existing.getStatus())) {
            return false;
        }
        String likeKey = LIKE_KEY_PREFIX + id + ":" + ip;
        if (redisService.get(likeKey) != null) {
            Asserts.fail("你已经点过赞了");
        }
        Guestbook update = new Guestbook();
        update.setId(id);
        update.setLikes((existing.getLikes() != null ? existing.getLikes() : 0) + 1);
        boolean updated = updateById(update);
        if (updated) {
            redisService.set(likeKey, "1", 365L * 24 * 60 * 60);
        }
        return updated;
    }

    @Override
    public int getRemainingDaily(String ip) {
        if (dailyLimit <= 0) {
            return Integer.MAX_VALUE;
        }
        Object count = redisService.get(dailyKey(ip));
        int used = count == null ? 0 : Integer.parseInt(count.toString());
        return Math.max(0, dailyLimit - used);
    }

    @Override
    public Page<Guestbook> listAdmin(Integer pageNum, Integer pageSize, String status) {
        Page<Guestbook> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Guestbook> wrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        return page(page, wrapper);
    }

    @Override
    public boolean approve(Long id) {
        Guestbook message = new Guestbook();
        message.setId(id);
        message.setStatus("APPROVED");
        return updateById(message);
    }

    @Override
    public boolean reject(Long id) {
        Guestbook message = new Guestbook();
        message.setId(id);
        message.setStatus("SPAM");
        return updateById(message);
    }

    @Override
    public long countApproved() {
        return count(new QueryWrapper<Guestbook>().eq("status", "APPROVED"));
    }

    private void validateParam(GuestbookParam param) {
        if (param == null || StrUtil.isBlank(param.getAuthorName())) {
            Asserts.fail("请填写昵称");
        }
        if (StrUtil.isBlank(param.getContent())) {
            Asserts.fail("请填写留言内容");
        }
        if (param.getAuthorName().trim().length() > 50) {
            Asserts.fail("昵称过长");
        }
        if (param.getContent().trim().length() > maxLength) {
            Asserts.fail("留言内容不能超过 " + maxLength + " 字");
        }
        if (param.getParentId() != null && param.getParentId() > 0) {
            Guestbook parent = getById(param.getParentId());
            if (parent == null || !"APPROVED".equals(parent.getStatus())) {
                Asserts.fail("回复的留言不存在");
            }
        }
    }

    private void incrementDailyCount(String ip) {
        if (dailyLimit <= 0) {
            return;
        }
        String key = dailyKey(ip);
        Object count = redisService.get(key);
        if (count == null) {
            redisService.set(key, "1", secondsUntilMidnight());
        } else {
            redisService.incr(key, 1);
        }
    }

    private String dailyKey(String ip) {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return DAILY_KEY_PREFIX + ip + ":" + date;
    }

    private long secondsUntilMidnight() {
        return java.time.Duration.between(
                java.time.LocalDateTime.now(),
                LocalDate.now().plusDays(1).atStartOfDay()
        ).getSeconds() + 1;
    }

    private String buildLetterAvatar(String name) {
        String letter = name.trim().substring(0, 1).toUpperCase();
        return "letter:" + letter;
    }

    private GuestbookVO toVo(Guestbook message) {
        GuestbookVO vo = new GuestbookVO();
        BeanUtils.copyProperties(message, vo);
        return vo;
    }
}
