CREATE TABLE questionnaires
(
    user_id           BIGINT,
    about             VARCHAR(1024) NOT NULL,
    rank_id           INT,
    specialization_id INT,
    linkedin          VARCHAR(256),
    hours_per_week    FLOAT        NOT NULL
);

ALTER TABLE questionnaires
    ADD CONSTRAINT pk_questionnaires PRIMARY KEY (user_id);
ALTER TABLE questionnaires
    ADD CONSTRAINT fk_questionnaires_users FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE questionnaires
    ADD CONSTRAINT fk_questionnaires_ranks FOREIGN KEY (rank_id) REFERENCES ranks (id);
ALTER TABLE questionnaires
    ADD CONSTRAINT fk_questionnaires_specializations FOREIGN KEY (specialization_id) REFERENCES specializations (id);