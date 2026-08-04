package com.lightdiary.modules.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightdiary.modules.blog.model.Tag;
import com.lightdiary.modules.blog.vo.TagVO;

import java.util.List;

public interface TagService extends IService<Tag> {

    boolean create(String name);

    Tag getBySlug(String slug);

    List<TagVO> listWithCount();
}
