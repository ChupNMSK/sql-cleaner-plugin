package plugin.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import lombok.extern.slf4j.Slf4j;
import plugin.formatter.QueryFormatter;

@Slf4j
public class SqlParsingChain {

    private String keyword;
    private Map<String, SqlParsingChain> children;

    private QueryFormatter formatter;

    public static SqlParsingChain newSqlNode() {
        return new SqlParsingChain();
    }

    public static SqlParsingChain newSqlNode(String keyword) {
        return new SqlParsingChain(keyword);
    }

    private SqlParsingChain() {
        this.children = new HashMap<>();
    }

    private SqlParsingChain(String keyword) {
        this.children = new HashMap<>();
        this.keyword = keyword;
    }


    public SqlParsingChain addChild(SqlParsingChain child) {
        children.put(child.keyword, child);
        return this;
    }

    public SqlParsingChain with(QueryFormatter formatter) {
        this.formatter = formatter;
        return this;
    }

    public String format(StringTokenizer tokenizer, String query) {
        while (tokenizer.hasMoreTokens()) {

            //format and return
            if (formatter != null) {
                return formatter.format(query);
            }

            //go to next node
            String token = tokenizer.nextToken().trim().toUpperCase(); //keywords are stored in upper case

            log.info("Token: {}", token);
            if (!token.isEmpty() && children.containsKey(token)) {
                SqlParsingChain childNode = children.get(token);
                return childNode.format(tokenizer, query);
            }
        }

        return query; //just skip unsupported
    }
}
