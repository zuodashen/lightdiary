package com.lightdiary.modules.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightdiary.common.util.SlugUtils;
import com.lightdiary.modules.blog.dto.InnovationParam;
import com.lightdiary.modules.blog.mapper.InnovationMapper;
import com.lightdiary.modules.blog.model.Innovation;
import com.lightdiary.modules.blog.service.InnovationService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class InnovationServiceImpl extends ServiceImpl<InnovationMapper, Innovation> implements InnovationService {

    @Override
    public boolean create(InnovationParam param) {
        Innovation innovation = buildInnovation(param, new Innovation());
        innovation.setCreateTime(new Date());
        innovation.setUpdateTime(new Date());
        return save(innovation);
    }

    @Override
    public boolean update(Long id, InnovationParam param) {
        Innovation existing = getById(id);
        if (existing == null) {
            return false;
        }
        Innovation innovation = buildInnovation(param, existing);
        innovation.setId(id);
        innovation.setUpdateTime(new Date());
        return updateById(innovation);
    }

    @Override
    public List<Innovation> listPublished() {
        return list(new QueryWrapper<Innovation>()
                .orderByDesc("is_featured")
                .orderByAsc("sort_order")
                .orderByDesc("create_time"));
    }

    private Innovation buildInnovation(InnovationParam param, Innovation innovation) {
        innovation.setTitle(param.getTitle());
        innovation.setSlug(StrUtil.isNotBlank(param.getSlug())
                ? param.getSlug()
                : SlugUtils.generateSlug(param.getTitle()));
        innovation.setSummary(param.getSummary());
        innovation.setDescription(param.getDescription());
        innovation.setCoverImage(param.getCoverImage());
        innovation.setDemoUrl(param.getDemoUrl());
        innovation.setGithubUrl(param.getGithubUrl());
        innovation.setTechStack(param.getTechStack());
        innovation.setStatus(StrUtil.isNotBlank(param.getStatus()) ? param.getStatus() : "BUILDING");
        innovation.setIsFeatured(param.getIsFeatured() != null ? param.getIsFeatured() : 0);
        innovation.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        return innovation;
    }
}
