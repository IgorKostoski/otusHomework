

create tabe client
(
    id   bigserial NOT NULL PRIMARY KEY,
    name varchar(50),
       age smallint
);

create table manager
(
    id bigserial not null primary key ,
    label varchar(50),
    param1 varchar(50)
);

