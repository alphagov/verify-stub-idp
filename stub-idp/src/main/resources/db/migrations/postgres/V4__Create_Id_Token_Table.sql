CREATE TABLE IF NOT EXISTS token
(
	auth_code varchar(255) not null primary key,
	access_token varchar (255) not null,
	id_token varchar(512) not null,
	last_modified timestamp default now()
)