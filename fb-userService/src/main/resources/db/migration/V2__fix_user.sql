ALTER TABLE users
    ADD created_by VARCHAR(255);

ALTER TABLE users
    ADD updated_by VARCHAR(255);

ALTER TABLE users
    DROP COLUMN password;

ALTER TABLE users
    DROP COLUMN id;

ALTER TABLE users
    ADD id UUID NOT NULL PRIMARY KEY;