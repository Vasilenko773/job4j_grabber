create table schema(
id serial primary key,
	name text,
	text text,
	link text unique,
	created date
);