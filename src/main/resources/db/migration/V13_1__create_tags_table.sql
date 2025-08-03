CREATE TABLE `tags` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `tag` VARCHAR(30) NOT NULL UNIQUE,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `show_tag` (
  `show_id` INT NOT NULL,
  `tag_id` INT NOT NULL,
  PRIMARY KEY (`show_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes
ALTER TABLE `show_tag`
  ADD KEY `show_tag_show_id` (`show_id`);

ALTER TABLE `show_tag`
  ADD KEY `show_tag_tag_id` (`tag_id`);

-- Foreign Keys
ALTER TABLE `show_tag`
  ADD CONSTRAINT `show_tag_show_id` FOREIGN KEY (`show_id`) REFERENCES `shows` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE `show_tag`
  ADD CONSTRAINT `show_tag_tag_id` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT;
