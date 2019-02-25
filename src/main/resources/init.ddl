create schema if not exists ORA_AWR;
create schema if not exists SEC_USERS;


create table if not exists
    SEC_USERS.TBL_USER_ROLE (id bigint not null,
                            rolename varchar(255) not null,
                            unique(rolename),
                            primary key (id));

create table if not exists
    SEC_USERS.TBL_USERS (id bigint not null,
                        role_id bigint not null,
                        enabled boolean not null,
                        password varchar(255) not null,
                        username varchar(255) not null,
                        deleted boolean not null,
                        primary key (id),
                        unique(username),
                        foreign key (role_id) references SEC_USERS.TBL_USER_ROLE (id) on delete cascade);

create sequence if not exists
    SEC_USERS.TBL_USERS_ROLE_SEQUENCE start with 1 increment by 1;

create sequence if not exists
    SEC_USERS.TBL_USERS_SEQUENCE start with 1 increment by 1;

insert into SEC_USERS.TBL_USER_ROLE
    select NEXT VALUE FOR SEC_USERS.TBL_USERS_ROLE_SEQUENCE,
           'ROLE_ADMIN' from dual
                where not exists
                    (select rolename from SEC_USERS.TBL_USER_ROLE where rolename='ROLE_ADMIN');

insert into SEC_USERS.TBL_USER_ROLE
    select NEXT VALUE FOR SEC_USERS.TBL_USERS_ROLE_SEQUENCE,
           'ROLE_USER' from dual
                where not exists
                    (select rolename from SEC_USERS.TBL_USER_ROLE where rolename='ROLE_USER');

insert into SEC_USERS.TBL_USERS
    select  SEC_USERS.TBL_USERS_SEQUENCE.NEXTVAL,
            id,
            true,
            '$2a$04$lg8DgWEHByGSfAAKUvuule1DyWDMbG6wKXqECzjBKLocWjiS4VaEC',
            'admin',
            false from SEC_USERS.TBL_USER_ROLE
                where not exists
                    (select id from SEC_USERS.TBL_USERS)
                    and
                    rolename='ROLE_ADMIN';