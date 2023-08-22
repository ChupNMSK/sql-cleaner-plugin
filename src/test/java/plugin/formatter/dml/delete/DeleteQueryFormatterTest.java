package plugin.formatter.dml.delete;

import plugin.formatter.AbstractFormatterTest;
import plugin.formatter.QueryFormatter;

class DeleteQueryFormatterTest extends AbstractFormatterTest {

    @Override
    protected QueryFormatter queryFormatterForTest() {
        return new DeleteQueryFormatter();
    }
}
