CREATE TABLE table_name
(
    column1    VARCHAR(10)       NOT NULL,
    column2    NUMERIC(20, 0)    NOT NULL,
    column3    BOOL              NULL DEFAULT TRUE,
    CONSTRAINT pk_constraint     PRIMARY KEY (column1),
    CONSTRAINT unique_constraint UNIQUE (column2)
);