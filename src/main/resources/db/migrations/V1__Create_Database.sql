DROP TABLE IF EXISTS users;

CREATE TABLE users (
 id serial PRIMARY KEY,
 username VARCHAR (50) UNIQUE NOT NULL,
 password VARCHAR (50) NOT NULL,
 identity_provider_friendly_id VARCHAR (255) NOT NULL,
 "data" json NOT NULL
);