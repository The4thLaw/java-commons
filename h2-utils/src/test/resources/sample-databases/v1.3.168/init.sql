-- Run in the console using java -cp .\target\legacy-h2-versions\h2-1.3.168.jar org.h2.tools.Console
CREATE TABLE groups (
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(127) NOT NULL,
);
CREATE INDEX ON groups(name);

CREATE TABLE users (
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(127) NOT NULL,
	fname VARCHAR(127) NULL,
	biography CLOB NULL,
	group_id INT NULL,
	CONSTRAINT fk_users_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE SET NULL
);
CREATE INDEX ON users(name);

INSERT INTO groups (name) VALUES ('group1'), ('group2');

INSERT INTO users (name, fname, biography, group_id) VALUES
	('Grundy', 'Solomon', 'Born on a Monday', 1),
	('Doe', NULL, NULL, 2);

COMMIT;

