package plugin.formatter.dml.insert;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import plugin.formatter.QueryFormatter;
import plugin.formatter.dml.insert.InsertQueryFormatter;

public class InsertQueryFormatterTest {

    private QueryFormatter formatter = new InsertQueryFormatter();

    @Test
    public void insert() {
        String candidate = "insert into table1 (COLUMN1, COLUMN2, COLUMN3) values (VALUE1, VALUE2, VALUE3);";

        String expected = "INSERT INTO table1\n" +
                          "\t(column1, column2, column3)\n" +
                          "VALUES\n" +
                          "\t(value1, value2, value3);";

        String actual = formatter.format(candidate);

        assertEquals(expected, actual);
    }

    @Test
    public void insertTwoValues() {
        String candidate = "insert into table1 (COLUMN1, COLUMN2, COLUMN3) values (VALUE1-1, VALUE1-2, VALUE1-3), (VALUE2-1, VALUE2-2, VALUE2-3);";

        String expected = "INSERT INTO table1\n" +
                "\t(column1, column2, column3)\n" +
                "VALUES\n" +
                "\t(value1-1, value1-2, value1-3), \n" +
                "\t(value2-1, value2-2, value2-3);";

        String actual = formatter.format(candidate);

        assertEquals(expected, actual);
    }

    @Test
    public void insert_WhenQueryToLong_ShouldDivideColumnsAndValuesOntoParts() {
        String candidate = "insert into table1 " +
                            "(COLUMN1, LONG_TEXT_COLUMN2, LONG_TEXT_COLUMN3, LONG_TEXT_COLUMN4, " +
                            "LONG_TEXT_COLUMN5, LONG_TEXT_COLUMN6, LONG_TEXT_COLUMN7) " +
                            "values (VALUE1, VALUE2, VALUE3, VALUE4, VALUE5, VALUE6, VALUE7);";

        String expected = "INSERT INTO table1\n" +
                          "\t(column1, long_text_column2, long_text_column3, \n" +
                          "\t long_text_column4, long_text_column5, long_text_column6, \n" +
                          "\t long_text_column7)\n" +
                          "VALUES\n" +
                          "\t(value1, value2, value3, \n" +
                          "\t value4, value5, value6, \n" +
                          "\t value7);";


        String actual = formatter.format(candidate);

        assertEquals(expected, actual);
    }


    @Test
    public void insertTwoValues_WhenQueryToLong_ShouldDivideColumnsAndValuesOntoParts() {
        String candidate = "insert into table1 " +
                "(COLUMN1, LONG_TEXT_COLUMN2, LONG_TEXT_COLUMN3, LONG_TEXT_COLUMN4, " +
                "LONG_TEXT_COLUMN5, LONG_TEXT_COLUMN6, LONG_TEXT_COLUMN7) " +
                "values (VALUE1, VALUE2, VALUE3, VALUE4, VALUE5, VALUE6, VALUE7)," +
                        "(VALUE1, VALUE2, VALUE3, VALUE4, VALUE5, VALUE6, VALUE7);";

        String expected = "INSERT INTO table1\n" +
                "\t(column1, long_text_column2, long_text_column3, \n" +
                "\t long_text_column4, long_text_column5, long_text_column6, \n" +
                "\t long_text_column7)\n" +
                "VALUES\n" +
                "\t(value1, value2, value3, \n" +
                "\t value4, value5, value6, \n" +
                "\t value7), \n" +
                "\t(value1, value2, value3, \n" +
                "\t value4, value5, value6, \n" +
                "\t value7);";


        String actual = formatter.format(candidate);

        assertEquals(expected, actual);
    }

    @Test
    public void insertTwoValues_WhenQueryToLong_ShouldDivideColumnsAndValuesOntoParts2() {
        String candidate = "insert into MEMBER (MEMBER_PID, MEMBER_ID, FIRST_NAME, LAST_NAME, FIRST_NAME_UPPER, CREATED_TIMESTAMP, LAST_NAME_UPPER,\n" +
                "                    MEMBER_ID_UPPER)\n" +
                "values\n" +
                "(${memberPID1}, 'memberId', 'John', 'Wick', 'JOHN', '2018-10-30', 'WICK', 'MEMBERID'),\n" +
                "(${memberPID2}, 'memberId2', 'Jack', 'Black', 'JACK', '2018-10-31', 'BLACK', 'MEMBERID2');";

        String expected = "INSERT INTO member\n" +
                "\t(member_pid, member_id, first_name, \n" +
                "\t last_name, first_name_upper, created_timestamp, \n" +
                "\t last_name_upper, member_id_upper)\n" +
                "VALUES\n" +
                "\t(${memberPID1}, 'memberId', 'John', \n" +
                "\t 'Wick', 'JOHN', '2018-10-30', \n" +
                "\t 'WICK', 'MEMBERID'),\n" +
                "\t(${memberPID2}, 'memberId2', 'Jack', \n" +
                "\t 'Black', 'JACK', '2018-10-31', \n" +
                "\t 'BLACK', 'MEMBERID2');";


        String actual = formatter.format(candidate);

        assertEquals(expected, actual);
    }
}
