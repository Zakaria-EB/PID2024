CREATE TABLE representation_reservation
(
    `id`                INT(11) AUTO_INCREMENT PRIMARY KEY,
    `representation_id` INT(11),
    `reservation_id`    INT(11),
    `price_id`          INT(11),
    `quantity`          TINYINT(4)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

--
-- Index pour la table `representation_reservation`
--
ALTER TABLE `representation_reservation`
    ADD KEY `representation_reservation_representation_id` (`representation_id`),
    ADD KEY `representation_reservation_reservation_id` (`reservation_id`),
    ADD KEY `representation_reservation_price_id` (`price_id`);

--
-- Contraintes pour la table `representation_reservation`
--
ALTER TABLE `representation_reservation`
    ADD CONSTRAINT `representation_reservation_representation_id` FOREIGN KEY (`representation_id`) REFERENCES `representations` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
    ADD CONSTRAINT `representation_reservation_reservation_id` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
    ADD CONSTRAINT `representation_reservation_price_id` FOREIGN KEY (`price_id`) REFERENCES `prices` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
