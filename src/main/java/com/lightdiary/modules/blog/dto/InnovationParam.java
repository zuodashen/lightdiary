package com.lightdiary.modules.blog.dto;

import lombok.Data;

@Data
public class InnovationParam {
    private String title;
    private String slug;
    private String summary;
    private String description;
    private String coverImage;
    private String demoUrl;
    private String githubUrl;
    private String techStack;
    private String status;
    private Integer isFeatured;
    private Integer sortOrder;
}
