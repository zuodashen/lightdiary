package com.lightdiary.modules.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lightdiary.modules.blog.dto.CommentParam;
import com.lightdiary.modules.blog.model.Comment;
import com.lightdiary.modules.blog.model.CommentConfig;
import com.lightdiary.modules.blog.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {

    Page<Comment> listAdmin(Integer pageNum, Integer pageSize, String status);

    boolean approve(Long id);

    boolean reject(Long id);

    List<CommentVO> listByArticle(Long articleId);

    boolean submit(Long articleId, CommentParam param, String ip, String userAgent);

    CommentConfig getConfig();

    boolean updateConfig(CommentConfig config);
}
