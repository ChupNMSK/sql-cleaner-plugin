package plugin.formatter.dml.delete;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import plugin.formatter.QueryFormatter;
import plugin.formatter.dml.delete.DeleteQueryFormatter;

public class DeleteQueryFormatterTest {

    private QueryFormatter formatter = new DeleteQueryFormatter();

    @Test
    public void test() {
        String candidate = "delete from table1 where column1 = value1;";
        String expected = "DELETE \n" +
                          "  FROM table1 \n" +
                          " WHERE column1 = value1;";

        String actual = formatter.format(candidate);

        assertEquals(expected, actual);
    }

    @Test
    public void test2() {
        String candidate = "delete from table_name where column1 = value1 and column2 = value2;";
        String expected = "DELETE\n" +
                          "  FROM table_name \n" +
                          " WHERE column1 = value1 \n" +
                          "   AND column2 = value2;";

        String actual = formatter.format(candidate);

        assertEquals(expected, actual);
    }
}
