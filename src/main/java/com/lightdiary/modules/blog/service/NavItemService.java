package com.lightdiary.modules.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightdiary.modules.blog.dto.NavItemParam;
import com.lightdiary.modules.blog.model.NavItem;

import java.util.List;

public interface NavItemService extends IService<NavItem> {

    boolean create(NavItemParam param);

    boolean update(Long id, NavItemParam param);

    List<NavItem> listAll();
}
