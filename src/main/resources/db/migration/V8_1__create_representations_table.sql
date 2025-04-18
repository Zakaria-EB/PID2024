CREATE TABLE `representations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `show_id` int(11) NOT NULL,
  `location_id` int(11) DEFAULT NULL,
  `when` datetime(6) NOT NULL,
  PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET= utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Index pour la table `representations`
--
ALTER TABLE `representations`
  ADD KEY `representations_location_id_a6832141_fk_locations_id` (`location_id`);

ALTER TABLE `representations`
  ADD KEY `representations_show_id_a6832141_fk_shows_id` (`show_id`);

--
-- Contraintes pour la table `shows`
--
ALTER TABLE `representations`
  ADD CONSTRAINT `representations_location_id_a6832141_fk_locations_id` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE `representations`
  ADD CONSTRAINT `representations_show_id_a6832141_fk_shows_id` FOREIGN KEY (`show_id`) REFERENCES `shows` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT;
