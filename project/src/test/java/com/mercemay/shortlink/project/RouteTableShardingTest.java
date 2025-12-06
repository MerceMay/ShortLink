package com.mercemay.shortlink.project;

public class RouteTableShardingTest {
    public static final String SQL = """
            CREATE TABLE `t_link_route_%d`
            (
                `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
                `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                PRIMARY KEY (`id`)
            )
                ENGINE = InnoDB
                DEFAULT CHARSET = utf8mb4;""";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i);
        }
    }
}
