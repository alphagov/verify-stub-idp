CREATE TABLE IF NOT EXISTS stub_idp_session
(
	session_id varchar(36) not null primary key,
	session_data json,
	last_modified timestamp default now()
)