CREATE TABLE `localities`
(
    `id`          int(11)                                NOT NULL AUTO_INCREMENT,
    `postal_code` varchar(6) COLLATE utf8mb4_unicode_ci  NOT NULL,
    `locality`    varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

