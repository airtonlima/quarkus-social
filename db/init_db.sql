create database quarkus-social;

create table users (
	id bigserial not null primary key,
	name varchar(50) not null,
	age integer not null
)

create table posts (
    id bigserial not null primary key,
    post_text varchar(200) not null,
    created_at timestamp,
    user_id bigint not null references users(id)
)

create table followers (
    id bigserial not null primary key,
    user_id bigint not null references users(id),
    follower_id bigint not null references users(id)
)