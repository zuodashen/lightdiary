-- 为已有 blog_article 表增加字数/阅读时长字段
-- mysql -u root -p --default-character-set=utf8mb4 light_diary < sql/migrate_article_stats.sql

SET NAMES utf8mb4;

ALTER TABLE blog_article
  ADD COLUMN word_count int(11) NOT NULL DEFAULT 0 COMMENT '正文字数' AFTER views;

ALTER TABLE blog_article
  ADD COLUMN reading_time int(11) NOT NULL DEFAULT 1 COMMENT '预计阅读时长(分钟)' AFTER word_count;

-- 粗略回填（含 Markdown 符号；重新保存文章后会由后端精确计算）
-- 仅更新有正文且尚未统计过的行，避免全表更新告警
UPDATE blog_article
SET word_count = CHAR_LENGTH(content),
    reading_time = GREATEST(1, CEIL(CHAR_LENGTH(content) / 400))
WHERE word_count = 0
  AND content IS NOT NULL
  AND CHAR_LENGTH(TRIM(content)) > 0;
