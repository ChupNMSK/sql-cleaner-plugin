CREATE TABLE table_name
(
    column1      NUMERIC(10)  NOT NULL,
    column_long2 VARCHAR(10)  NOT NULL,
    column3      VARCHAR(10)  NULL,
    column_mid4  BOOL         NOT NULL DEFAULT TRUE,
    column5      TIMESTAMP(0) NULL DEFAULT CURRENT_TIMESTAMP
);