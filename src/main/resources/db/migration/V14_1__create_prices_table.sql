CREATE TABLE `prices`
(
    `id`                 INT(11) NOT NULL AUTO_INCREMENT,
    `type`               VARCHAR(30) NOT NULL,
    `price`              DECIMAL(10, 2) NOT NULL,
    `start_date`         DATE NOT NULL,
    `end_date`           DATE NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

--
-- Index pour la table `prices`
--
ALTER TABLE `prices`
    ADD KEY `prices_id` (`id`);