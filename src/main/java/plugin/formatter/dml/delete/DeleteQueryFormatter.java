package plugin.formatter.dml.delete;

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
            if (isKeyword(token)) {
                formatKeyword(formattedQuery, token, nestingLevel, maxKeywordLength);
            } else {
                formattedQuery.append(token);
                formattedQuery.append(" ");
            }

            if (token.endsWith(";")) {
                formattedQuery.append("\n");
            }
        }

        return formattedQuery.toString().trim();
    }

    private boolean isKeyword(String word) {
        return Keywords.isKeyword(word);
    }

    private void formatKeyword(StringBuilder sb, String keyword, int nestingLevel, int maxKeywordLength) {
        sb.append("\n");
        sb.append(" ".repeat( nestingLevel * (maxKeywordLength - keyword.length()) ));
        sb.append(keyword.toUpperCase());
        sb.append(" ");
    }


    private int getMaxKeywordLength(String[] words) {
        int maxKeywordLength = 0;
        for (String word : words) {
            if (isKeyword(word) && word.length() > maxKeywordLength) {
                maxKeywordLength = word.length();
            }
        }
        return maxKeywordLength;
    }
}
