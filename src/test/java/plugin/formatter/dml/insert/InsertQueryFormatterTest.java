package plugin.formatter.dml.insert;

import plugin.formatter.AbstractFormatterTest;
import plugin.formatter.QueryFormatter;

class InsertQueryFormatterTest extends AbstractFormatterTest {

    @Override
    protected QueryFormatter queryFormatterForTest() {
        return new InsertQueryFormatter();
    }
}
