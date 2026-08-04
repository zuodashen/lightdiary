package com.lightdiary.modules.blog.dto;

import lombok.Data;

@Data
public class NavItemParam {
    private String name;
    private String path;
    private String icon;
    private Long parentId;
    private Integer sortOrder;
    private Integer isExternal;
}
