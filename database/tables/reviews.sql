CREATE TABLE reviews
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY,
    to_user_id       BIGINT,
    from_user_id     BIGINT,
    mentoring_req_id BIGINT,
    text             VARCHAR(1024) NOT NULL,
    rating           INT           NOT NULL DEFAULT 0,
    created_at       TIMESTAMP     NOT NULL DEFAULT now()
);


ALTER TABLE reviews
    ADD CONSTRAINT pk_reviews PRIMARY KEY (id);
ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_users_to_user_id FOREIGN KEY (to_user_id) REFERENCES users (id);
ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_users_from_user_id FOREIGN KEY (from_user_id) REFERENCES users (id);
ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_mentoring_requests FOREIGN KEY (mentoring_req_id) REFERENCES mentoring_requests (id);
ALTER TABLE reviews
    ADD CONSTRAINT uq_reviews_to_user_id_from_user_id_mentoring_req_id UNIQUE (to_user_id, from_user_id, mentoring_req_id);