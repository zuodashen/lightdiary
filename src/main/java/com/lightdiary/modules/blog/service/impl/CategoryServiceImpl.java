package com.lightdiary.modules.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightdiary.common.util.SlugUtils;
import com.lightdiary.modules.blog.dto.CategoryParam;
import com.lightdiary.modules.blog.mapper.CategoryMapper;
import com.lightdiary.modules.blog.model.Category;
import com.lightdiary.modules.blog.service.CategoryService;
import com.lightdiary.modules.blog.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public boolean create(CategoryParam param) {
        Category category = new Category();
        BeanUtils.copyProperties(param, category);
        category.setSlug(StrUtil.isNotBlank(param.getSlug()) ? param.getSlug() : SlugUtils.generateSlug(param.getName()));
        category.setParentId(param.getParentId() != null ? param.getParentId() : 0L);
        category.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        category.setCreateTime(new Date());
        return save(category);
    }

    @Override
    public boolean update(Long id, CategoryParam param) {
        Category category = new Category();
        BeanUtils.copyProperties(param, category);
        category.setId(id);
        if (StrUtil.isNotBlank(param.getSlug())) {
            category.setSlug(param.getSlug());
        }
        return updateById(category);
    }

    @Override
    public List<CategoryVO> listWithCount() {
        return baseMapper.listWithArticleCount();
    }

    @Override
    public Category getBySlug(String slug) {
        return getOne(new QueryWrapper<Category>().eq("slug", slug));
    }
}
