CREATE TABLE chat_members
(
    user_id BIGINT,
    chat_id BIGINT
);

ALTER TABLE chat_members
    ADD CONSTRAINT pk_chat_members PRIMARY KEY (user_id, chat_id);
ALTER TABLE chat_members
    ADD CONSTRAINT fk_chat_members_users FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE chat_members
    ADD CONSTRAINT fk_chat_members_chats FOREIGN KEY (chat_id) REFERENCES chats (id);