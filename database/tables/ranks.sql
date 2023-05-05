CREATE TABLE ranks
(
    id    INT GENERATED ALWAYS AS IDENTITY,
    name  VARCHAR(50) NOT NULL,
    level INT         NOT NULL
);

ALTER TABLE ranks
    ADD CONSTRAINT pk_ranks PRIMARY KEY (id);
ALTER TABLE ranks
    ADD CONSTRAINT uq_ranks_name UNIQUE (name);

INSERT INTO ranks
VALUES (default, 'Нема досвіду', 0),
       (default, 'Trainee', 1),
       (default, 'Junior Software Engineer', 2),
       (default, 'Software Engineer', 3),
       (default, 'Senior Software Engineer', 4),
       (default, 'Lead Software Engineer', 5),
       (default, 'Chief Engineer', 6),
       (default, 'Chief Software Architect', 7);


