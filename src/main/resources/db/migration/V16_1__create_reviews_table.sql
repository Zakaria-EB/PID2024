CREATE TABLE `reviews`
(
    `id`         INT(11)             NOT NULL AUTO_INCREMENT,
    `user_id`    INT(11)             NOT NULL,
    `show_id`    INT(11)             NOT NULL,
    `review`     TEXT                NOT NULL,
    `stars`      TINYINT(5) UNSIGNED NOT NULL,
    `created_at` DATETIME            NOT NULL,
    `updated_at` DATETIME            NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

--
-- Index pour la table `reviews`
--
ALTER TABLE `reviews`
    ADD KEY `reviews_user_id` (`user_id`),
    ADD KEY `reviews_show_id` (`show_id`);

--
-- Contraintes pour la table `reviews`
--
ALTER TABLE `reviews`
    ADD CONSTRAINT `reviews_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `reviews_show_id` FOREIGN KEY (`show_id`) REFERENCES `shows` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;