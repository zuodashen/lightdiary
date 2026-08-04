package com.lightdiary.modules.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lightdiary.modules.blog.dto.ArticleParam;
import com.lightdiary.modules.blog.dto.ArticleQueryParam;
import com.lightdiary.modules.blog.model.Article;
import com.lightdiary.modules.blog.vo.ArchiveVO;
import com.lightdiary.modules.blog.vo.ArticleDetailVO;
import com.lightdiary.modules.blog.vo.ArticleListVO;

import java.util.List;

public interface ArticleService extends IService<Article> {

    boolean create(ArticleParam param);

    boolean update(Long id, ArticleParam param);

    boolean delete(Long id);

    Page<Article> listAdmin(ArticleQueryParam param);

    ArticleDetailVO getDetail(Long id);

    boolean publish(Long id);

    boolean draft(Long id);

    boolean toggleTop(Long id);

    Page<ArticleListVO> listPublished(ArticleQueryParam param);

    ArticleDetailVO getPublishedBySlug(String slug);

    List<ArticleListVO> listHot(Integer limit);

    List<ArchiveVO> listArchives();

    void syncViewCount();
}
