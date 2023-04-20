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
VALUES (default, 'Trainee', 0),
       (default, 'Junior Software Engineer', 1),
       (default, 'Software Engineer', 2),
       (default, 'Senior Software Engineer', 3),
       (default, 'Lead Software Engineer', 4),
       (default, 'Chief Engineer', 5),
       (default, 'Chief Software Architect', 6);


