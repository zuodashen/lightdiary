package com.lightdiary.modules.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lightdiary.modules.blog.model.Article;
import com.lightdiary.modules.blog.vo.ArticleListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {

    Page<ArticleListVO> listPublished(Page<ArticleListVO> page,
                                      @Param("categoryId") Long categoryId,
                                      @Param("tagId") Long tagId,
                                      @Param("keyword") String keyword);

    List<ArticleListVO> listHot(@Param("limit") Integer limit);

    List<ArticleListVO> listAllPublished();

    Long countPublished();

    Long sumPublishedViews();

    Long sumPublishedWordCount();

    List<Article> listPublishedIds();
}
