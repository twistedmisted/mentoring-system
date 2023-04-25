CREATE TABLE messages
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY,
    chat_id      BIGINT,
    from_user_id BIGINT,
    text         VARCHAR(4096) NOT NULL,
    created_at   TIMESTAMP     NOT NULL DEFAULT now()
);

ALTER TABLE messages
    ADD CONSTRAINT pk_messages PRIMARY KEY (id);
ALTER TABLE messages
    ADD CONSTRAINT fk_messages_users FOREIGN KEY (from_user_id) REFERENCES users (id);