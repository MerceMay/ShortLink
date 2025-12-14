SET NAMES utf8mb4;

INSERT INTO `t_user_15` (`id`,
                         `username`,
                         `password`,
                         `real_name`,
                         `phone`,
                         `mail`,
                         `deletion_time`,
                         `create_time`,
                         `update_time`,
                         `del_flag`)
VALUES (1,
        'admin',
        'admin123456',
        'admin',
        '8pJitojd/nuDe1Sz0ovj3A==',
        'mtkPlWtFkf/8joc85F4Sjg==',
        NULL,
        '2025-01-01 00:00:00',
        '2025-01-01 00:00:00',
        0);

INSERT INTO `t_group_15` (`id`,
                          `gid`,
                          `name`,
                          `username`,
                          `sort_order`,
                          `create_time`,
                          `update_time`,
                          `del_flag`)
VALUES (1,
        'BSYyI7',
        '默认分组',
        'admin',
        0,
        '2025-01-01 00:00:00',
        '2025-01-01 00:00:00',
        0);
