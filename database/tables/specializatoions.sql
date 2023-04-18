CREATE TABLE specializations
(
    id   INT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL
);

ALTER TABLE specializations
    ADD CONSTRAINT pk_specializations PRIMARY KEY (id);

INSERT INTO specializations
VALUES (default, 'JavaScript / Front-End'),
       (default, 'Java'),
       (default, 'C# / .NET'),
       (default, 'Python'),
       (default, 'PHP'),
       (default, 'Node.js'),
       (default, 'iOS'),
       (default, 'Android'),
       (default, 'C / C++ / Embedded'),
       (default, 'Flutter'),
       (default, 'Golang'),
       (default, 'Ruby'),
       (default, 'Scala'),
       (default, 'Salesforce'),
       (default, 'Rust'),
       (default, 'QA Manual'),
       (default, 'QA Automation'),
       (default, 'DevOps'),
       (default, 'Data Science'),
       (default, 'Data Analyst'),
       (default, 'Gamedev / Unity'),
       (default, 'SQL / DBA'),
       (default, 'Data Engineer');