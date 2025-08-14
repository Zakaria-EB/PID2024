CREATE TABLE `roles`
(
    `id`   int(11)                                NOT NULL AUTO_INCREMENT,
    `role` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
