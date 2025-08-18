CREATE TABLE show_tag
(
    show_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (show_id, tag_id)
);