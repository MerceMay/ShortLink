CREATE TABLE `t_link`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

# 分表SQL
CREATE TABLE `t_link_0`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_1`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_2`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_3`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_4`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_5`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_6`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_7`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_8`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_9`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_10`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_11`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_12`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_13`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_14`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_15`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                   DEFAULT NULL COMMENT '域名',
    `short_uri`       varchar(8) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128)                                   DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                  DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                        DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                    DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                   DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                     DEFAULT NULL COMMENT '启用标识 0：未启用 1：已启用',
    `created_type`    tinyint(1)                                     DEFAULT NULL COMMENT '创建类型 0：控制台 1：接口',
    `valid_date_type` tinyint(1)                                     DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                       DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                  DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                       DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                     DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

# 路由表
CREATE TABLE `t_link_route`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;

CREATE TABLE `t_link_route_0`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_1`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_2`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_3`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_4`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_5`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_6`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_7`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_8`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_9`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_10`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_11`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_12`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_13`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_14`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;
CREATE TABLE `t_link_route_15`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4;

# 访问统计表
CREATE TABLE `t_link_access_stats`
(
    `id`             bigint(20)                                                    NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '完整短链接',
    `gid`            varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '分组标识',
    `date`           date                                                          NULL DEFAULT NULL COMMENT '日期',
    `pv`             int(11)                                                       NULL DEFAULT NULL COMMENT '访问量',
    `uv`             int(11)                                                       NULL DEFAULT NULL COMMENT '独立访问数',
    `uip`            int(11)                                                       NULL DEFAULT NULL COMMENT '独立IP数',
    `hour`           int(3)                                                        NULL DEFAULT NULL COMMENT '小时',
    `weekday`        int(3)                                                        NULL DEFAULT NULL COMMENT '星期',
    `create_time`    datetime                                                      NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime                                                      NULL DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1)                                                    NULL DEFAULT NULL COMMENT '删除标识：0 未删除 1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_link_access_stats` (`full_short_url`, `gid`, `weekday`, `hour`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4;

# ip定位表
CREATE TABLE `t_link_locale_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '完整短链接',
    `gid`            varchar(32) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '分组标识',
    `date`           date                                    DEFAULT NULL COMMENT '日期',
    `cnt`            int(11)                                 DEFAULT NULL COMMENT '访问量',
    `province`       varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '省份名称',
    `city`           varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '市名称',
    `adcode`         varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '城市编码',
    `country`        varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '国家标识',
    `create_time`    datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime   NOT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1)                              DEFAULT NULL COMMENT '删除标识 0表示删除 1表示未删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_locale_stats` (`full_short_url`, `gid`, `date`, `adcode`, `province`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

# 操作系统统计表
CREATE TABLE `t_link_os_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '完整短链接',
    `gid`            varchar(32) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '分组标识',
    `date`           date                                    DEFAULT NULL COMMENT '日期',
    `cnt`            int(11)                                 DEFAULT NULL COMMENT '访问量',
    `os`             varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '操作系统',
    `create_time`    datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime   NOT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1)                              DEFAULT NULL COMMENT '删除标识 0表示删除 1表示未删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_locale_stats` (`full_short_url`, `gid`, `date`, `os`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

# 浏览器统计表
CREATE TABLE `t_link_browser_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11)      DEFAULT NULL COMMENT '访问量',
    `browser`        varchar(64)  DEFAULT NULL COMMENT '浏览器',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_browser_stats` (`full_short_url`, `gid`, `date`, `browser`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

# 访问日志表
CREATE TABLE `t_link_access_logs`
(
    `id`             bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '完整短链接',
    `gid`            varchar(32) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '分组标识',
    `user`           varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '用户信息',
    `browser`        varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '浏览器',
    `os`             varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '操作系统',
    `ip`             varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT 'IP',
    `create_time`    datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime                                DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1)                              DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;