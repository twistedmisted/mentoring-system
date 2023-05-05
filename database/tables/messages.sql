CREATE TABLE messages
(
    id           BIGINT,
    chat_id      BIGINT,
    from_user_id BIGINT,
    text         VARCHAR(4096) NOT NULL,
    created_at   TIMESTAMP     NOT NULL DEFAULT now(),
    status       VARCHAR(30)   NOT NULL
);

ALTER TABLE messages
    ADD CONSTRAINT pk_messages PRIMARY KEY (id);
ALTER TABLE messages
    ADD CONSTRAINT fk_messages_users FOREIGN KEY (from_user_id) REFERENCES users (id);
ALTER TABLE messages
    ADD CONSTRAINT fk_messages_chats FOREIGN KEY (chat_id) REFERENCES chats (id);