package com.lightdiary.modules.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightdiary.modules.blog.dto.CategoryParam;
import com.lightdiary.modules.blog.model.Category;
import com.lightdiary.modules.blog.vo.CategoryVO;

import java.util.List;

public interface CategoryService extends IService<Category> {

    boolean create(CategoryParam param);

    boolean update(Long id, CategoryParam param);

    List<CategoryVO> listWithCount();

    Category getBySlug(String slug);
}
