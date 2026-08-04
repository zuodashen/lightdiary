package com.lightdiary.modules.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lightdiary.modules.blog.model.Category;
import com.lightdiary.modules.blog.vo.CategoryVO;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {

    List<CategoryVO> listWithArticleCount();
}
