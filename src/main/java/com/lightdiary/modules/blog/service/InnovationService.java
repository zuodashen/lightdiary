package com.lightdiary.modules.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightdiary.modules.blog.dto.InnovationParam;
import com.lightdiary.modules.blog.model.Innovation;

import java.util.List;

public interface InnovationService extends IService<Innovation> {

    boolean create(InnovationParam param);

    boolean update(Long id, InnovationParam param);

    List<Innovation> listPublished();
}
