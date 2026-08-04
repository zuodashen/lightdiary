package com.lightdiary.modules.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightdiary.modules.blog.dto.PageParam;
import com.lightdiary.modules.blog.model.BlogPage;

public interface BlogPageService extends IService<BlogPage> {

    boolean create(PageParam param);

    boolean update(Long id, PageParam param);

    BlogPage getPublishedBySlug(String slug);
}
