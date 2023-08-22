package plugin.formatter.ddl.create;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import plugin.formatter.QueryFormatter;
import plugin.formatter.ddl.create.CreateTableFormatter;

public class CreateTableFormatterTest {

    private QueryFormatter formatter = new CreateTableFormatter();


    @Test
    public void createTable() {
        String candidate =
                "create table table_name ( " +
                " column1 numeric(10) not null, " +
                " column_long2 varchar(10) not null, " +
                " column3 varchar(10) null, " +
                " column_mid4 bool not null default true, " +
                " column5 timestamp(0) null default CURRENT_TIMESTAMP);";
        String expected =
                "CREATE TABLE table_name\n" +
                "(\n" +
                "\tcolumn1      NUMERIC(10)  NOT NULL,\n" +
                "\tcolumn_long2 VARCHAR(10)  NOT NULL,\n" +
                "\tcolumn3      VARCHAR(10)  NULL,\n" +
                "\tcolumn_mid4  BOOL         NOT NULL DEFAULT TRUE,\n" +
                "\tcolumn5      TIMESTAMP(0) NULL DEFAULT CURRENT_TIMESTAMP\n" +
                ");";

        String actual = formatter.format(candidate);

        assertEquals(expected, actual);
    }

    @Test
    public void createTable2() {
        String candidate =
                "create table table_name (\n" +
                "    column1 varchar(20) not null,\n" +
                "    column2 numeric(10) not null,\n" +
                "    column3 bool null default true\n" +
                ");";
        String expected =
                "CREATE TABLE table_name\n" +
                "(\n" +
                "\tcolumn1 VARCHAR(20) NOT NULL,\n" +
                "\tcolumn2 NUMERIC(10) NOT NULL,\n" +
                "\tcolumn3 BOOL        NULL DEFAULT TRUE\n" +
                ");";

        String actual = formatter.format(candidate);

        assertEquals(expected, actual);
    }
}
