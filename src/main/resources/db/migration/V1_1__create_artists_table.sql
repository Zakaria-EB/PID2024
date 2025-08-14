CREATE TABLE `artists` (
                           `id` int(11) NOT NULL AUTO_INCREMENT,
                           `firstname` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
                           `lastname` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
                           PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
