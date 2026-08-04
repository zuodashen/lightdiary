package com.lightdiary.modules.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightdiary.common.service.MarkdownService;
import com.lightdiary.common.util.SlugUtils;
import com.lightdiary.modules.blog.dto.PageParam;
import com.lightdiary.modules.blog.mapper.BlogPageMapper;
import com.lightdiary.modules.blog.model.BlogPage;
import com.lightdiary.modules.blog.service.BlogPageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BlogPageServiceImpl extends ServiceImpl<BlogPageMapper, BlogPage> implements BlogPageService {

    @Autowired
    private MarkdownService markdownService;

    @Override
    public boolean create(PageParam param) {
        BlogPage page = buildPage(param, new BlogPage());
        page.setCreateTime(new Date());
        page.setUpdateTime(new Date());
        return save(page);
    }

    @Override
    public boolean update(Long id, PageParam param) {
        BlogPage existing = getById(id);
        if (existing == null) {
            return false;
        }
        BlogPage page = buildPage(param, existing);
        page.setId(id);
        page.setUpdateTime(new Date());
        return updateById(page);
    }

    @Override
    public BlogPage getPublishedBySlug(String slug) {
        return getOne(new QueryWrapper<BlogPage>().eq("slug", slug).eq("status", "PUBLISHED"));
    }

    private BlogPage buildPage(PageParam param, BlogPage page) {
        page.setTitle(param.getTitle());
        page.setSlug(StrUtil.isNotBlank(param.getSlug()) ? param.getSlug() : SlugUtils.generateSlug(param.getTitle()));
        page.setContent(param.getContent());
        page.setContentHtml(markdownService.toHtml(param.getContent()));
        page.setTemplate(param.getTemplate());
        page.setStatus(StrUtil.isNotBlank(param.getStatus()) ? param.getStatus() : "PUBLISHED");
        page.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        return page;
    }
}
