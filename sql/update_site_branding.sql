-- 品牌统一：jianyueblog / 简约博客 / 简约无双 → lightdiary / 微光博客
-- 适用于已导入旧数据的库，执行后必须清 Redis 缓存：
--   redis-cli DEL blog:settings blog:social

USE light_diary;

-- 站点设置
UPDATE blog_site_setting SET setting_value = 'lightdiary' WHERE setting_key = 'site_title';
UPDATE blog_site_setting SET setting_value = '微光博客' WHERE setting_key = 'site_subtitle';
UPDATE blog_site_setting SET setting_value = 'lightdiary' WHERE setting_key = 'site_author';
UPDATE blog_site_setting SET setting_value = '微光博客' WHERE setting_key = 'banner_title';
UPDATE blog_site_setting SET setting_value = '一个分享知识与技术的微光博客' WHERE setting_key = 'site_description';
UPDATE blog_site_setting SET setting_value = '一个分享知识与技术的微光博客' WHERE setting_key = 'banner_subtitle';

-- 兜底：替换仍含旧品牌文案的值
UPDATE blog_site_setting SET setting_value = REPLACE(setting_value, 'jianyueblog', 'lightdiary')
WHERE setting_value LIKE '%jianyueblog%';
UPDATE blog_site_setting SET setting_value = REPLACE(setting_value, '简约博客', '微光博客')
WHERE setting_value LIKE '%简约博客%';
UPDATE blog_site_setting SET setting_value = REPLACE(setting_value, '简约无双', 'lightdiary')
WHERE setting_value LIKE '%简约无双%';

-- 社交链接
UPDATE blog_social_link SET url = 'https://github.com/zuodashen/lightdiary' WHERE platform = 'GitHub';
UPDATE blog_social_link SET url = 'mailto:1904465424@qq.com' WHERE platform = 'Email';
DELETE FROM blog_social_link WHERE platform = 'GitLink';

-- 关于页（若已迁移）
UPDATE blog_page SET content = REPLACE(content, 'jianyuewushuang@163.com', '1904465424@qq.com') WHERE slug = 'about';
UPDATE blog_page SET content = REPLACE(content, '简约无双', 'lightdiary') WHERE slug = 'about';
UPDATE blog_page SET content_html = REPLACE(content_html, 'jianyuewushuang@163.com', '1904465424@qq.com') WHERE slug = 'about';
UPDATE blog_page SET content_html = REPLACE(content_html, '简约无双', 'lightdiary') WHERE slug = 'about';
