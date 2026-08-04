package com.lightdiary.modules.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightdiary.modules.blog.dto.BookmarkParam;
import com.lightdiary.modules.blog.mapper.BookmarkCategoryMapper;
import com.lightdiary.modules.blog.mapper.BookmarkMapper;
import com.lightdiary.modules.blog.model.Bookmark;
import com.lightdiary.modules.blog.model.BookmarkCategory;
import com.lightdiary.modules.blog.service.BookmarkService;
import com.lightdiary.modules.blog.vo.BookmarkGroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookmarkServiceImpl extends ServiceImpl<BookmarkMapper, Bookmark> implements BookmarkService {

    @Autowired
    private BookmarkCategoryMapper bookmarkCategoryMapper;

    @Override
    public boolean create(BookmarkParam param) {
        Bookmark bookmark = new Bookmark();
        BeanUtils.copyProperties(param, bookmark);
        bookmark.setSortOrder(param.getSortOrder() != null ? param.getSortOrder() : 0);
        return save(bookmark);
    }

    @Override
    public boolean update(Long id, BookmarkParam param) {
        Bookmark bookmark = new Bookmark();
        BeanUtils.copyProperties(param, bookmark);
        bookmark.setId(id);
        return updateById(bookmark);
    }

    @Override
    public boolean createCategory(String name, String icon, Integer sortOrder) {
        BookmarkCategory category = new BookmarkCategory();
        category.setName(name);
        category.setIcon(icon);
        category.setSortOrder(sortOrder != null ? sortOrder : 0);
        return bookmarkCategoryMapper.insert(category) > 0;
    }

    @Override
    public List<BookmarkCategory> listCategories() {
        return bookmarkCategoryMapper.selectList(
                new QueryWrapper<BookmarkCategory>().orderByAsc("sort_order"));
    }

    @Override
    public List<BookmarkGroupVO> listGrouped() {
        List<BookmarkCategory> categories = listCategories();
        List<BookmarkGroupVO> result = new ArrayList<>();
        for (BookmarkCategory category : categories) {
            BookmarkGroupVO group = new BookmarkGroupVO();
            group.setId(category.getId());
            group.setName(category.getName());
            group.setIcon(category.getIcon());
            group.setSortOrder(category.getSortOrder());
            List<Bookmark> bookmarks = list(new QueryWrapper<Bookmark>()
                    .eq("category_id", category.getId())
                    .orderByAsc("sort_order"));
            group.setBookmarks(bookmarks);
            result.add(group);
        }
        return result;
    }
}
