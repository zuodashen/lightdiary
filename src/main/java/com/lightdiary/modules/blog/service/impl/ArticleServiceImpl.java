package com.lightdiary.modules.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightdiary.common.service.MarkdownService;
import com.lightdiary.common.service.RedisService;
import com.lightdiary.common.util.SlugUtils;
import com.lightdiary.modules.blog.dto.ArticleParam;
import com.lightdiary.modules.blog.dto.ArticleQueryParam;
import com.lightdiary.modules.blog.mapper.ArticleMapper;
import com.lightdiary.modules.blog.mapper.ArticleTagMapper;
import com.lightdiary.modules.blog.model.Article;
import com.lightdiary.modules.blog.model.ArticleTag;
import com.lightdiary.modules.blog.model.Category;
import com.lightdiary.modules.blog.service.ArticleService;
import com.lightdiary.modules.blog.service.CategoryService;
import com.lightdiary.modules.blog.service.TagService;
import com.lightdiary.modules.blog.vo.ArchiveVO;
import com.lightdiary.modules.blog.vo.ArticleDetailVO;
import com.lightdiary.modules.blog.vo.ArticleListVO;
import com.lightdiary.modules.blog.vo.TagRefVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private static final String VIEW_KEY_PREFIX = "blog:article:views:";

    @Autowired
    private MarkdownService markdownService;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisService redisService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(ArticleParam param) {
        Article article = buildArticle(param, new Article());
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        if ("PUBLISHED".equals(article.getStatus()) && article.getPublishTime() == null) {
            article.setPublishTime(new Date());
        }
        boolean saved = save(article);
        if (saved) {
            saveTags(article.getId(), param.getTagIds());
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Long id, ArticleParam param) {
        Article existing = getById(id);
        if (existing == null) {
            return false;
        }
        Article article = buildArticle(param, existing);
        article.setId(id);
        article.setUpdateTime(new Date());
        if ("PUBLISHED".equals(article.getStatus()) && article.getPublishTime() == null) {
            article.setPublishTime(new Date());
        }
        boolean updated = updateById(article);
        if (updated) {
            articleTagMapper.delete(new QueryWrapper<ArticleTag>().eq("article_id", id));
            saveTags(id, param.getTagIds());
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        articleTagMapper.delete(new QueryWrapper<ArticleTag>().eq("article_id", id));
        return removeById(id);
    }

    @Override
    public Page<Article> listAdmin(ArticleQueryParam param) {
        Page<Article> page = new Page<>(param.getPageNum(), param.getPageSize());
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        if (param.getCategoryId() != null) {
            wrapper.eq("category_id", param.getCategoryId());
        }
        if (StrUtil.isNotBlank(param.getStatus())) {
            wrapper.eq("status", param.getStatus());
        }
        if (StrUtil.isNotBlank(param.getKeyword())) {
            wrapper.and(w -> w.like("title", param.getKeyword()).or().like("summary", param.getKeyword()));
        }
        wrapper.orderByDesc("is_top").orderByDesc("create_time");
        return page(page, wrapper);
    }

    @Override
    public ArticleDetailVO getDetail(Long id) {
        Article article = getById(id);
        if (article == null) {
            return null;
        }
        return toDetailVO(article);
    }

    @Override
    public boolean publish(Long id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus("PUBLISHED");
        article.setPublishTime(new Date());
        article.setUpdateTime(new Date());
        return updateById(article);
    }

    @Override
    public boolean draft(Long id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus("DRAFT");
        article.setUpdateTime(new Date());
        return updateById(article);
    }

    @Override
    public boolean toggleTop(Long id) {
        Article existing = getById(id);
        if (existing == null) {
            return false;
        }
        Article article = new Article();
        article.setId(id);
        article.setIsTop(existing.getIsTop() != null && existing.getIsTop() == 1 ? 0 : 1);
        return updateById(article);
    }

    @Override
    public Page<ArticleListVO> listPublished(ArticleQueryParam param) {
        Page<ArticleListVO> page = new Page<>(param.getPageNum(), param.getPageSize());
        Page<ArticleListVO> result = baseMapper.listPublished(page, param.getCategoryId(), param.getTagId(), param.getKeyword());
        fillTags(result.getRecords());
        fillViews(result.getRecords());
        return result;
    }

    @Override
    public ArticleDetailVO getPublishedBySlug(String slug) {
        Article article = getOne(new QueryWrapper<Article>().eq("slug", slug));
        if (article == null || !"PUBLISHED".equals(article.getStatus())) {
            return null;
        }
        redisService.incr(VIEW_KEY_PREFIX + article.getId(), 1);
        ArticleDetailVO vo = toDetailVO(article);
        Object cached = redisService.get(VIEW_KEY_PREFIX + article.getId());
        if (cached != null) {
            int extra = Integer.parseInt(cached.toString());
            vo.setViews((article.getViews() != null ? article.getViews() : 0) + extra);
        }
        return vo;
    }

    @Override
    public List<ArticleListVO> listHot(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }
        List<ArticleListVO> list = baseMapper.listHot(limit);
        fillTags(list);
        fillViews(list);
        return list;
    }

    @Override
    public List<ArchiveVO> listArchives() {
        List<ArticleListVO> all = baseMapper.listAllPublished();
        fillTags(all);
        Map<String, ArchiveVO> map = new LinkedHashMap<>();
        Calendar cal = Calendar.getInstance();
        for (ArticleListVO article : all) {
            Date time = article.getPublishTime() != null ? article.getPublishTime() : article.getCreateTime();
            if (time == null) {
                continue;
            }
            cal.setTime(time);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            String key = year + "-" + month;
            ArchiveVO archive = map.computeIfAbsent(key, k -> {
                ArchiveVO vo = new ArchiveVO();
                vo.setYear(year);
                vo.setMonth(month);
                vo.setCount(0);
                vo.setArticles(new ArrayList<>());
                return vo;
            });
            archive.getArticles().add(article);
            archive.setCount(archive.getCount() + 1);
        }
        return new ArrayList<>(map.values());
    }

    @Override
    @Scheduled(cron = "0 0 * * * ?")
    public void syncViewCount() {
        List<Article> articles = list();
        for (Article article : articles) {
            String key = VIEW_KEY_PREFIX + article.getId();
            Object val = redisService.get(key);
            if (val != null) {
                int increment = Integer.parseInt(val.toString());
                if (increment > 0) {
                    Article update = new Article();
                    update.setId(article.getId());
                    update.setViews((article.getViews() != null ? article.getViews() : 0) + increment);
                    updateById(update);
                    redisService.del(key);
                }
            }
        }
    }

    private Article buildArticle(ArticleParam param, Article article) {
        article.setTitle(param.getTitle());
        article.setSlug(StrUtil.isNotBlank(param.getSlug()) ? param.getSlug() : SlugUtils.generateSlug(param.getTitle()));
        article.setSummary(param.getSummary());
        article.setContent(param.getContent());
        article.setContentHtml(markdownService.toHtml(param.getContent()));
        article.setCoverImage(param.getCoverImage());
        article.setCategoryId(param.getCategoryId());
        article.setStatus(StrUtil.isNotBlank(param.getStatus()) ? param.getStatus() : "DRAFT");
        article.setIsTop(param.getIsTop() != null ? param.getIsTop() : 0);
        article.setAllowComment(param.getAllowComment() != null ? param.getAllowComment() : 1);
        if (article.getViews() == null) {
            article.setViews(0);
        }
        return article;
    }

    private void saveTags(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        for (Long tagId : tagIds) {
            ArticleTag at = new ArticleTag();
            at.setArticleId(articleId);
            at.setTagId(tagId);
            articleTagMapper.insert(at);
        }
    }

    private ArticleDetailVO toDetailVO(Article article) {
        ArticleDetailVO vo = new ArticleDetailVO();
        BeanUtils.copyProperties(article, vo);
        vo.setAllowComment(article.getAllowComment() != null && article.getAllowComment() == 1);
        if (article.getCategoryId() != null) {
            Category category = categoryService.getById(article.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        vo.setTags(getTagsForArticle(article.getId()));
        return vo;
    }

    private void fillTags(List<ArticleListVO> list) {
        for (ArticleListVO vo : list) {
            vo.setTags(getTagsForArticle(vo.getId()));
        }
    }

    private void fillViews(List<ArticleListVO> list) {
        for (ArticleListVO vo : list) {
            Object cached = redisService.get(VIEW_KEY_PREFIX + vo.getId());
            if (cached != null) {
                vo.setViews((vo.getViews() != null ? vo.getViews() : 0) + Integer.parseInt(cached.toString()));
            }
        }
    }

    private List<TagRefVO> getTagsForArticle(Long articleId) {
        List<ArticleTag> relations = articleTagMapper.selectList(
                new QueryWrapper<ArticleTag>().eq("article_id", articleId));
        if (relations.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> tagIds = relations.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
        return tagService.listByIds(tagIds).stream().map(tag -> {
            TagRefVO ref = new TagRefVO();
            ref.setId(tag.getId());
            ref.setName(tag.getName());
            ref.setSlug(tag.getSlug());
            return ref;
        }).collect(Collectors.toList());
    }
}
