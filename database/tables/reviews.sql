CREATE TABLE reviews
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT,
    text    VARCHAR(1024) NOT NULL,
    rating  FLOAT         NOT NULL DEFAULT 0
);

ALTER TABLE reviews
    ADD CONSTRAINT pk_reviews PRIMARY KEY (id);
ALTER TABLE reviews
    ADD CONSTRAINT pk_reviews_users FOREIGN KEY (user_id) REFERENCES users (id);