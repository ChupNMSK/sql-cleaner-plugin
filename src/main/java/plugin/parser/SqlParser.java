package plugin.parser;

import static plugin.parser.SqlParsingChain.newSqlNode;

import java.util.StringTokenizer;

import plugin.formatter.ddl.create.CreateTableQueryFormatter;
import plugin.formatter.dml.delete.DeleteQueryFormatter;
import plugin.formatter.dml.insert.InsertQueryFormatter;

public class SqlParser {

    private SqlParser() {
    }

    private static final SqlParsingChain ROOT = newSqlNode();

    static {
        ROOT.addChild(newSqlNode("INSERT")
                        .with(new InsertQueryFormatter()));

        ROOT.addChild(newSqlNode("CREATE")
                .addChild(newSqlNode("TABLE")
                        .with(new CreateTableQueryFormatter())));

        ROOT.addChild(newSqlNode("DELETE")
                .with(new DeleteQueryFormatter()));
    }


    public static String parseAndFormat(String query) {
        StringTokenizer tokenizer = new StringTokenizer(query);

        return ROOT.format(tokenizer, query);
    }
}
