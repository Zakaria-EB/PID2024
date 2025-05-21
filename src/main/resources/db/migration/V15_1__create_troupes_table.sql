CREATE TABLE troupes (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         name VARCHAR(60) NOT NULL,
                         logo_url VARCHAR(255),
                         PRIMARY KEY(id),
                         UNIQUE(name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE artists
    ADD COLUMN troupe_id BIGINT;

ALTER TABLE artists
    ADD CONSTRAINT fk_artist_troupe
        FOREIGN KEY (troupe_id)
            REFERENCES troupes(id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT;