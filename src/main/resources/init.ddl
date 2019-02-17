create schema if not exists ORA_AWR;
create schema if not exists SEC_USERS;


create table if not exists
    SEC_USERS.TBL_USER_ROLE (userId bigint not null,
                            roleName varchar(255) not null,
                            primary key (userId));

create table if not exists
    SEC_USERS.TBL_USERS (id bigint not null,
                        enabled boolean not null,
                        password varchar(255) not null,
                        username varchar(255) not null,
                        deleted boolean not null,
                        primary key (id),
                        unique(username),
                        foreign key (id) references SEC_USERS.TBL_USER_ROLE (userId) on delete cascade);

create sequence if not exists
    SEC_USERS.TBL_USERS_SEQUENCE start with 1 increment by 1;

insert into SEC_USERS.TBL_USER_ROLE
    select NEXT VALUE FOR SEC_USERS.TBL_USERS_SEQUENCE,
           'ROLE_ADMIN' from dual
                where not exists
                    (select userid from SEC_USERS.TBL_USER_ROLE);

insert into SEC_USERS.TBL_USERS
    select userId,
            true,
            '$2a$04$NpZg9zZRx87QJMWe7q9YD.xKvvf3FWDn7oxTg5S4tuCiTjgf5RVrW',
            'admin',
            false from SEC_USERS.TBL_USER_ROLE
                where not exists
                    (select id from SEC_USERS.TBL_USERS);


