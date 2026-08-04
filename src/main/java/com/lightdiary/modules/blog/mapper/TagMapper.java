package com.lightdiary.modules.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lightdiary.modules.blog.model.Tag;
import com.lightdiary.modules.blog.vo.TagVO;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {

    List<TagVO> listWithArticleCount();
}
