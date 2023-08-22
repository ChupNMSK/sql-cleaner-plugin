package plugin.formatter.ddl.create;

import plugin.formatter.AbstractFormatterTest;
import plugin.formatter.QueryFormatter;

public class CreateTableQueryFormatterTest extends AbstractFormatterTest {

    @Override
    protected QueryFormatter queryFormatterForTest() {
        return new CreateTableQueryFormatter();
    }
}
