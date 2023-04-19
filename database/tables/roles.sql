CREATE TABLE roles
(
    id   INT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(30) NOT NULL
);

ALTER TABLE roles
    ADD CONSTRAINT pk_roles PRIMARY KEY (id);

INSERT INTO roles
VALUES (default, 'MENTOR'),
       (default, 'MENTEE');