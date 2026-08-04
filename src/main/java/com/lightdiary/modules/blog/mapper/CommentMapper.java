package com.lightdiary.modules.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lightdiary.modules.blog.model.Comment;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> listByArticleId(Long articleId);
}
