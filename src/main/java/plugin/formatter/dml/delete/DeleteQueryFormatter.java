package plugin.formatter.dml.delete;

import static plugin.config.PluginConfig.BR;
import static plugin.model.sql.Keywords.Conditional.IN;
import static plugin.model.sql.Keywords.Conditional.WHERE;
import static plugin.model.sql.Keywords.DML.DELETE;
import static plugin.model.sql.Keywords.DML.FROM;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.formatter.QueryFormatter;
import plugin.model.sql.Keywords;

public final class DeleteQueryFormatter implements QueryFormatter {

    private static final Pattern DELETE_PATTERN = Pattern.compile(
            "(?i)(DELETE\\s+FROM)\\s+(\\w+)\\s+(WHERE)\\s+(.+)$");

    private static final Pattern EQUALITTY_PATTERN = Pattern.compile("(?i)(\\w+)\\s*(>=|<=|=|>|<)\\s*(.+)");

    private static final int TABLE_NAME_GROUP = 2;
    private static final int WHERE_GROUP = 3;
    private static final int CONDITION_GROUP = 4;

    private static final int COLUMN_NAME = 1;
    private static final int CONDITION_SIGN = 2;
    private static final int VALUE = 3;

    private static final int MAX_KEYWORD_LENGTH = 6; //DELETE? need for right inline

    @Override
    public String format(String query) {
        Matcher matcher = DELETE_PATTERN.matcher(query);

        if(!matcher.find()) {
            return query + ";";
        }

        String tableName = matcher.group(TABLE_NAME_GROUP);
        String where = matcher.group(WHERE_GROUP);
        String condition = matcher.group(CONDITION_GROUP);

        if (where == null) {
            return formatDeleteWithoutWhere(tableName);
        } else {
            return formatDeleteWithWhere(tableName, condition);
        }
    }

    private String formatDeleteWithoutWhere(String tableName) {
        return "DELETE FROM " + tableName.toLowerCase() + ";";
    }

    private String formatDeleteWithWhere(String tableName, String condition) {
        StringBuilder formattedQuery = new StringBuilder();

        int nestingLevel = 1;

        formatKeyword(formattedQuery, DELETE.name(), nestingLevel, MAX_KEYWORD_LENGTH);
        formatKeyword(formattedQuery, FROM.name(), nestingLevel, MAX_KEYWORD_LENGTH);
        formattedQuery.append(" ").append(tableName.toLowerCase());
        formatKeyword(formattedQuery, WHERE.name(), nestingLevel, MAX_KEYWORD_LENGTH);
        formattedQuery.append(formatCondition(condition));
        formattedQuery.append(";");

        return formattedQuery.toString().trim();
    }

    private String formatCondition(String condition) {
        StringBuilder conditionBuilder = new StringBuilder();
        String[] conditionTokens = condition.split("\\s+");

        int nestingLevel = 1;


        for (int i = 0; i < conditionTokens.length; i++) {
            String token = conditionTokens[i];

            Matcher matcher = EQUALITTY_PATTERN.matcher(token);
            if (matcher.find()) {
                String columnName = matcher.group(COLUMN_NAME).trim();
                String conditionSign = matcher.group(CONDITION_SIGN).trim();
                String value = matcher.group(VALUE).trim();
                conditionBuilder
                        .append(" ")
                        .append(columnName.toLowerCase())
                        .append(" ")
                        .append(conditionSign)
                        .append(" ")
                        .append(value);
            }
            else if (i == 0 //first token after WHERE is always column name
                        || i != conditionTokens.length - 1
                        && isCondition(conditionTokens[i + 1])) { //look ahead if next is condition so currently token is column name
                token = token.toLowerCase();
                conditionBuilder.append(" ").append(token);
            }
            else if (Keywords.isKeyword(token)) {
                formatKeyword(conditionBuilder, token, nestingLevel, MAX_KEYWORD_LENGTH);
            }
            else {
                conditionBuilder.append(" ").append(token);
            }
        }
        return conditionBuilder.toString();
    }

    //todo: align right method
    private void formatKeyword(StringBuilder sb, String keyword, int nestingLevel, int maxKeywordLength) {
        sb.append(BR);
        sb.append(" ".repeat(nestingLevel * (maxKeywordLength - keyword.length())));
        sb.append(keyword.toUpperCase());
    }

    private int getMaxKeywordLength(String[] tokens) {
        int maxKeywordLength = 0;
        for (String token : tokens) {
            if (Keywords.isKeyword(token) && token.length() > maxKeywordLength) {
                maxKeywordLength = token.length();
            }
        }
        return maxKeywordLength;
    }

    private boolean isCondition(String token) {
        return token.equalsIgnoreCase(IN.name())
                || token.equals(">=")
                || token.equals("<=")
                || token.equals("=")
                || token.equals(">")
                || token.equals("<");
    }
}