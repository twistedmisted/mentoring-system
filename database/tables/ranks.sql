CREATE TABLE ranks
(
    id   INT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL
);

ALTER TABLE ranks
    ADD CONSTRAINT pk_ranks PRIMARY KEY (id);

INSERT INTO ranks
VALUES (default, 'Junior Software Engineer'),
       (default, 'Software Engineer'),
       (default, 'Senior Software Engineer'),
       (default, 'Lead Software Engineer'),
       (default, 'Chief Engineer'),
       (default, 'Chief Software Architect');