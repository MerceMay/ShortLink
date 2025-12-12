/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercemay.shortlink.admin;

/**
 * 短链接分组表分表建表语句生成
 */
public class GroupTableShardingTest {
    public static final String SQL = """
            CREATE TABLE `t_group_%d`
            (
                `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
                `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
                `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
                `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
                `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
                `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
                `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                PRIMARY KEY (`id`),
                UNIQUE KEY `uk_username_gid` (`gid`, `username`) USING BTREE
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4;""";


    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i);
        }
    }
}
