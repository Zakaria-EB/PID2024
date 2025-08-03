CREATE TABLE `reservations`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `user_id`      int(11) NOT NULL,
    `booking_date` DATETIME,
    `status`       VARCHAR(60),
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

--
-- Index pour la table `reservations`
--
ALTER TABLE `reservations`
    ADD KEY `reservations_user_id` (`user_id`);

--
-- Contraintes pour la table `reservations`
--
ALTER TABLE `reservations`
    ADD CONSTRAINT `reservations_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT;

