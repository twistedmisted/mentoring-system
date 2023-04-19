CREATE TABLE users
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY,
    name     VARCHAR(50)  NOT NULL,
    surname  VARCHAR(50)  NOT NULL,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status   VARCHAR(256) NOT NULL DEFAULT 'NEEDS_INFORMATION',
    role_id  INT
);

ALTER TABLE users
    ADD CONSTRAINT pk_users PRIMARY KEY (id);
ALTER TABLE users
    ADD CONSTRAINT fk_users_roles FOREIGN KEY (role_id) REFERENCES roles (id);