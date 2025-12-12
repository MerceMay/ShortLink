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
 * 用户表分表建表语句生成
 */
public class UserTableShardingTest {
    public static final String SQL = """
            CREATE TABLE `t_user_%d`
            (
                `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
                `password`      varchar(512) DEFAULT NULL COMMENT '密码',
                `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
                `phone`         varchar(128) DEFAULT NULL COMMENT '手机号',
                `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱',
                `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
                `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
                `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
                `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
                PRIMARY KEY (`id`),
                UNIQUE KEY `uk_username` (`username`) USING BTREE
            ) ENGINE = InnoDB
              DEFAULT CHARSET = utf8mb4;""";


    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i);
        }
    }
}
