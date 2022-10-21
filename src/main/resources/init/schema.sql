use webflux;

drop table todos;
drop table users;

create table users
(
    cid      bigint auto_increment
        primary key,
    login_id varchar(32) charset utf8  not null,
    login_pw varchar(256) charset utf8 not null,
    name     varchar(16) charset utf8  not null,
    email    varchar(128) charset utf8 null,
    enabled  bit default b'0'          not null,
    role varchar(128) charset utf8 not null
);

create table todos
(
    cid       bigint auto_increment
        primary key,
    user_cid  bigint                    not null,
    title     varchar(256) charset utf8 not null,
    content   text                  null,
    created_at datetime                  not null,
    updated_at datetime                  not null,
    done      bit                       null,
    constraint todos_users_cid_fk
        foreign key (user_cid) references users (cid)
);

