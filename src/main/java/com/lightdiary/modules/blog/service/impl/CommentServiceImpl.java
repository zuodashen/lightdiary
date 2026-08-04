package com.lightdiary.modules.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightdiary.modules.blog.dto.CommentParam;
import com.lightdiary.modules.blog.mapper.CommentConfigMapper;
import com.lightdiary.modules.blog.mapper.CommentMapper;
import com.lightdiary.modules.blog.model.Comment;
import com.lightdiary.modules.blog.model.CommentConfig;
import com.lightdiary.modules.blog.service.CommentService;
import com.lightdiary.modules.blog.vo.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.*;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Value("${blog.comment.default-status:PENDING}")
    private String defaultStatus;

    @Autowired
    private CommentConfigMapper commentConfigMapper;

    @Override
    public Page<Comment> listAdmin(Integer pageNum, Integer pageSize, String status) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        return page(page, wrapper);
    }

    @Override
    public boolean approve(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus("APPROVED");
        return updateById(comment);
    }

    @Override
    public boolean reject(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus("SPAM");
        return updateById(comment);
    }

    @Override
    public List<CommentVO> listByArticle(Long articleId) {
        List<Comment> comments = baseMapper.listByArticleId(articleId);
        return buildTree(comments);
    }

    @Override
    public boolean submit(Long articleId, CommentParam param, String ip, String userAgent) {
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setParentId(param.getParentId() != null ? param.getParentId() : 0L);
        comment.setAuthorName(param.getAuthorName());
        comment.setAuthorEmail(param.getAuthorEmail());
        comment.setContent(param.getContent());
        comment.setStatus(defaultStatus);
        comment.setIpAddress(ip);
        comment.setUserAgent(userAgent);
        comment.setCreateTime(new Date());
        if (StrUtil.isNotBlank(param.getAuthorEmail())) {
            String hash = cn.hutool.crypto.digest.DigestUtil.md5Hex(param.getAuthorEmail().trim().toLowerCase());
            comment.setAuthorAvatar("https://www.gravatar.com/avatar/" + hash + "?d=identicon");
        }
        return save(comment);
    }

    @Override
    public CommentConfig getConfig() {
        List<CommentConfig> configs = commentConfigMapper.selectList(
                new QueryWrapper<CommentConfig>().eq("enabled", 1).last("LIMIT 1"));
        return configs.isEmpty() ? null : configs.get(0);
    }

    @Override
    public boolean updateConfig(CommentConfig config) {
        if (config.getId() == null) {
            return commentConfigMapper.insert(config) > 0;
        }
        return commentConfigMapper.updateById(config) > 0;
    }

    private List<CommentVO> buildTree(List<Comment> comments) {
        Map<Long, CommentVO> map = new LinkedHashMap<>();
        for (Comment comment : comments) {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(comment, vo);
            vo.setChildren(new ArrayList<>());
            map.put(vo.getId(), vo);
        }
        List<CommentVO> roots = new ArrayList<>();
        for (CommentVO vo : map.values()) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                roots.add(vo);
            } else {
                CommentVO parent = map.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                } else {
                    roots.add(vo);
                }
            }
        }
        return roots;
    }
}
