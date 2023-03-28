-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE FROM horse where id < 0;

INSERT INTO horse (id, name, description, date_of_birth, sex)
VALUES (-1, 'Wendy', 'The famous one!', '2012-12-12', 'FEMALE')
;

INSERT INTO owner (first_name, last_name, email) VALUES ('John', 'Doe', 'johndoe@example.com');
INSERT INTO owner (first_name, last_name, email) VALUES ('Jane', 'Doe', 'janedoe@example.com');
INSERT INTO owner (first_name, last_name, email) VALUES ('Bob', 'Smith', 'bobsmith@example.com');
INSERT INTO owner (first_name, last_name, email) VALUES ('Mary', 'Johnson', 'maryjohnson@example.com');
INSERT INTO owner (first_name, last_name, email) VALUES ('Tom', 'Jones', 'tomjones@example.com');
INSERT INTO owner (first_name, last_name, email) VALUES ('Samantha', 'Williams', 'samanthawilliams@example.com');
INSERT INTO owner (first_name, last_name, email) VALUES ('David', 'Brown', 'davidbrown@example.com');
INSERT INTO owner (first_name, last_name, email) VALUES ('Karen', 'Wilson', 'karenwilson@example.com');
INSERT INTO owner (first_name, last_name, email) VALUES ('Steven', 'Taylor', 'steventaylor@example.com');
INSERT INTO owner (first_name, last_name, email) VALUES ('Jessica', 'Davis', 'jessicadavis@example.com');

INSERT INTO horse (name, description, date_of_birth, sex, owner_id, father_id, mother_id) VALUES ('Thunderbolt', 'A strong and powerful horse', '2010-05-02', 'MALE', 1, NULL, NULL);
INSERT INTO horse (name, description, date_of_birth, sex, owner_id, father_id, mother_id) VALUES ('Daisy', 'A beautiful and gentle horse', '2012-09-14', 'FEMALE', 2, NULL, NULL);
INSERT INTO horse (name, description, date_of_birth, sex, owner_id, father_id, mother_id) VALUES ('Apollo', 'A majestic horse with a proud demeanor', '2011-04-22', 'MALE', 3, NULL, NULL);
INSERT INTO horse (name, description, date_of_birth, sex, owner_id, father_id, mother_id) VALUES ('Cocoa', 'A friendly and affectionate horse', '2013-06-01', 'FEMALE', 4, NULL, NULL);
INSERT INTO horse (name, description, date_of_birth, sex, owner_id, father_id, mother_id) VALUES ('Romeo', 'A charming horse with a playful personality', '2012-01-10', 'MALE', 5, NULL, NULL);
INSERT INTO horse (name, description, date_of_birth, sex, owner_id, father_id, mother_id) VALUES ('Lily', 'A graceful and elegant horse', '2014-03-12', 'FEMALE', 6, NULL, NULL);
INSERT INTO horse (name, description, date_of_birth, sex, owner_id, father_id, mother_id) VALUES ('Zeus', 'A majestic horse with a strong and imposing presence', '2010-11-23', 'MALE', 7, NULL, NULL);
INSERT INTO horse (name, description, date_of_birth, sex, owner_id, father_id, mother_id) VALUES ('Bella', 'A sweet and affectionate horse', '2013-09-27', 'FEMALE', 8, NULL, NULL);
INSERT INTO horse (name, description, date_of_birth, sex, owner_id, father_id, mother_id) VALUES ('Max', 'A brave and loyal horse', '2011-07-08', 'MALE', 9, NULL, NULL);
