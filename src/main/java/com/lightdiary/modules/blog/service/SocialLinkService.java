package com.lightdiary.modules.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightdiary.modules.blog.model.SocialLink;

import java.util.List;

public interface SocialLinkService extends IService<SocialLink> {

    boolean create(SocialLink link);

    List<SocialLink> listAll();
}
