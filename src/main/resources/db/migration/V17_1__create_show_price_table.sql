CREATE TABLE `show_price`
(
    `id`       INT(11) NOT NULL AUTO_INCREMENT,
    `show_id`  INT(11) NOT NULL,
    `price_id` INT(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

--
-- Index pour la table `show_price`
--
ALTER TABLE `show_price`
    ADD KEY `show_price_show_id` (`show_id`),
    ADD KEY `show_price_price_id` (`price_id`);

--
-- Contraintes pour la table `show_price`
--
ALTER TABLE `show_price`
    ADD CONSTRAINT `fk_show_price_show` FOREIGN KEY (`show_id`) REFERENCES `shows` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT `fk_show_price_price` FOREIGN KEY (`price_id`) REFERENCES `prices` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;