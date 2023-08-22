package plugin.formatter.dml.delete;

import static plugin.formatter.GeneralConfig.BR;

import plugin.formatter.QueryFormatter;
import plugin.model.sql.Keywords;

public final class DeleteQueryFormatter implements QueryFormatter {

    @Override
    public String format(String query) {
        // Split the query into individual tokens
        String[] tokens = query.split("\\s+");

        StringBuilder formattedQuery = new StringBuilder();

        int maxKeywordLength = getMaxKeywordLength(tokens);
        int nestingLevel = 1;

        for (String token : tokens) {
            if (Keywords.isKeyword(token)) {
                formatKeyword(formattedQuery, token, nestingLevel, maxKeywordLength);
            } else {
                formattedQuery.append(" ").append(token);
            }
        }

        return formattedQuery.toString().trim();
    }

    private void formatKeyword(StringBuilder sb, String keyword, int nestingLevel, int maxKeywordLength) {
        sb.append(BR);
        //todo: align right method
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
}