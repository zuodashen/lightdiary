package com.lightdiary.modules.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightdiary.modules.blog.dto.BookmarkParam;
import com.lightdiary.modules.blog.model.Bookmark;
import com.lightdiary.modules.blog.model.BookmarkCategory;
import com.lightdiary.modules.blog.vo.BookmarkGroupVO;

import java.util.List;

public interface BookmarkService extends IService<Bookmark> {

    boolean create(BookmarkParam param);

    boolean update(Long id, BookmarkParam param);

    boolean createCategory(String name, String icon, Integer sortOrder);

    List<BookmarkCategory> listCategories();

    List<BookmarkGroupVO> listGrouped();
}
