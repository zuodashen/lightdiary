package com.lightdiary.modules.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightdiary.common.util.SlugUtils;
import com.lightdiary.modules.blog.mapper.TagMapper;
import com.lightdiary.modules.blog.model.Tag;
import com.lightdiary.modules.blog.service.TagService;
import com.lightdiary.modules.blog.vo.TagVO;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public boolean create(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setSlug(SlugUtils.generateSlug(name));
        tag.setCreateTime(new Date());
        return save(tag);
    }

    @Override
    public Tag getBySlug(String slug) {
        return getOne(new QueryWrapper<Tag>().eq("slug", slug));
    }

    @Override
    public List<TagVO> listWithCount() {
        return baseMapper.listWithArticleCount();
    }

    public Tag getOrCreate(String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        Tag tag = getOne(new QueryWrapper<Tag>().eq("name", name));
        if (tag == null) {
            create(name);
            tag = getOne(new QueryWrapper<Tag>().eq("name", name));
        }
        return tag;
    }
}
