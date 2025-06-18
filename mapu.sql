CREATE TABLE `user`
(
    `id`          VARCHAR(255) PRIMARY KEY COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    `nickname`    VARCHAR(50) COMMENT '昵称',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `avatar`      VARCHAR(255) COMMENT '头像URL',
    `gender`      TINYINT  DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `phone`       VARCHAR(20) COMMENT '手机号',
    `email`       VARCHAR(100) COMMENT '邮箱',
    `role`        TINYINT  DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
    `is_enabled`  TINYINT  DEFAULT 1 COMMENT '账号是否启用：0-禁用，1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';

CREATE TABLE `article_category`
(
    `id`          VARCHAR(255) PRIMARY KEY COMMENT '类别ID',
    `name`        VARCHAR(50) NOT NULL UNIQUE COMMENT '类别名称（如小说、论文、日常）',
    `description` VARCHAR(255) COMMENT '类别描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT '文章类别表';

CREATE TABLE `article`
(
    `id`           VARCHAR(255) PRIMARY KEY COMMENT '文章ID',
    `user_id`      VARCHAR(255) NOT NULL COMMENT '作者ID',
    `category_id`  VARCHAR(255) NOT NULL COMMENT '类别ID',
    `title`        VARCHAR(100) NOT NULL COMMENT '文章标题',
    `summary`      VARCHAR(255) NOT NULL COMMENT '文章简介',
    `content`      TEXT         NOT NULL COMMENT '文章内容',
    `status`       TINYINT      NOT NULL COMMENT '状态：0-审核中，1-已发布，2-审核未通过，3-草稿',
    `create_time`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `publish_time` DATETIME COMMENT '发布时间',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`category_id`) REFERENCES `article_category` (`id`) ON DELETE RESTRICT
) COMMENT '文章表';

CREATE TABLE `article_favorite`
(
    `id`          VARCHAR(255) PRIMARY KEY COMMENT '收藏ID',
    `user_id`     VARCHAR(255) NOT NULL COMMENT '用户ID',
    `article_id`  VARCHAR(255) NOT NULL COMMENT '文章ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE (`user_id`, `article_id`) COMMENT '用户对同一文章不可重复收藏',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE
) COMMENT '文章收藏表';

CREATE TABLE `article_comment`
(
    `id`          VARCHAR(255) PRIMARY KEY COMMENT '评论ID',
    `user_id`     VARCHAR(255) NOT NULL COMMENT '评论用户ID',
    `article_id`  VARCHAR(255) NOT NULL COMMENT '文章ID',
    `parent_id`   VARCHAR(255) DEFAULT NULL COMMENT '父评论ID（NULL表示顶级评论）',
    `content`     TEXT         NOT NULL COMMENT '评论内容',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`parent_id`) REFERENCES `article_comment` (`id`) ON DELETE SET NULL
) COMMENT '文章评论表';