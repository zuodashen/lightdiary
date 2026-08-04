package com.lightdiary.modules.blog.vo;

import com.lightdiary.modules.blog.model.Bookmark;
import lombok.Data;

import java.util.List;

@Data
public class BookmarkGroupVO {
    private Long id;
    private String name;
    private String icon;
    private Integer sortOrder;
    private List<Bookmark> bookmarks;
}
