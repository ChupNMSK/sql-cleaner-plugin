create table table_name (
    column1 varchar(10) not null,
    column2 numeric(20, 0) not null,
    column3 bool null default true,
    constraint pk_constraint primary key (column1),
    constraint unique_constraint unique (column2)
);