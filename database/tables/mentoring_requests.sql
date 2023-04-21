CREATE TABLE mentoring_requests
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY,
    from_id    BIGINT,
    to_id      BIGINT,
    status     VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at TIMESTAMP   NOT NULL DEFAULT now()
);

ALTER TABLE mentoring_requests
    ADD CONSTRAINT pk_mentoring_requests PRIMARY KEY (id);
ALTER TABLE mentoring_requests
    ADD CONSTRAINT fk_mentoring_requests_users_from_id FOREIGN KEY (from_id) REFERENCES users (id);
ALTER TABLE mentoring_requests
    ADD CONSTRAINT fk_mentoring_requests_users_to_id FOREIGN KEY (to_id) REFERENCES users (id);
ALTER TABLE mentoring_requests
    ADD CONSTRAINT uq_mentoring_requests_from_id_to_id UNIQUE (from_id, to_id);