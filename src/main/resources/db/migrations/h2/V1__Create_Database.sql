DROP TABLE IF EXISTS users;

CREATE TABLE users (
 id serial PRIMARY KEY,
 username VARCHAR (50) NOT NULL,
 password VARCHAR (50) NOT NULL,
 identity_provider_friendly_id VARCHAR (255) NOT NULL,
 "data" text NOT NULL
);

CREATE TABLE stub_idp_session
(
	session_id varchar(36) not null primary key,
	session_data text,
	last_modified timestamp default now()
);

CREATE ALIAS to_json AS $$
   String to_json(String value) {
       return "{" + value + "}";
   }
$$;