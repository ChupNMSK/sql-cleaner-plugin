package plugin.formatter.dml.insert;

import static plugin.config.PluginConfig.DIVINE_ON;
import static plugin.config.PluginConfig.MAX_LENGTH;
import static plugin.model.sql.Keywords.convertIfKeywordToUppercase;
import static plugin.util.StringsUtil.BR;
import static plugin.util.StringsUtil.COMA;
import static plugin.util.StringsUtil.COMA_BR;
import static plugin.util.StringsUtil.COMA_SPACE;
import static plugin.util.StringsUtil.EMPTY_STR;
import static plugin.util.StringsUtil.SEMICOLON;
import static plugin.util.StringsUtil.SPACE;
import static plugin.util.StringsUtil.TAB;
import static plugin.util.StringsUtil.TAB_SPACE;
import static plugin.util.StringsUtil.replaceLastEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import plugin.formatter.QueryFormatter;

@Slf4j
public class InsertQueryFormatter implements QueryFormatter {

    private static final Pattern INSERT_INTO_PATTERN =
            Pattern.compile("(?i)INSERT\\s+INTO\\s+(\\w*\\d*)\\s*");
    private static final Pattern COLUMNS_VALUES_PATTERN = Pattern.compile("\\(([^),]+(?:,\\s*[^),]+)+)\\)");

    @Override
    public String format(String query) {
        StringBuilder formattedQuery = new StringBuilder();
        String tableName = getTableName(query);
        Map<Integer, String> map = getColumnValues(query);

        String columNames = map.remove(0); //first group is columns

        formattedQuery.append("INSERT INTO ")
                .append(tableName.toLowerCase())
                .append(BR)
                .append(TAB)
                .append(columNames.toLowerCase())
                .append(BR)
                .append("VALUES")
                .append(BR)
                .append(TAB);

        for (String valueGroup : map.values()) {
            formattedQuery.append(valueGroup)
                    .append(COMA)
                    .append(BR)
                    .append(TAB);
        }

        replaceLastEntry(COMA_BR, SEMICOLON, formattedQuery);

        log.debug("Formatted query {}", formattedQuery);
        return formattedQuery.toString();
    }

    private String getTableName(String query) {
        Matcher matcher = INSERT_INTO_PATTERN.matcher(query);
        matcher.find();
        return matcher.group(1).trim();
    }

    private Map<Integer, String> getColumnValues(String query) {
        Map<Integer, String> columnValues = parseColumnAndValues(query);

        alignColumnAndValues(columnValues);
        log.debug("Column values map: {}", columnValues);
        return columnValues;
    }

    private Map<Integer, String> parseColumnAndValues(String query) {
        Map<Integer, String> columnValues = new HashMap<>();

        Matcher matcher = COLUMNS_VALUES_PATTERN.matcher(query);
        for (int group = 0; matcher.find(); group++) {
            columnValues.put(group, matcher.group(1));
        }

        return columnValues;
    }

    private void alignColumnAndValues(Map<Integer, String> columnValues) {
        boolean isTooLong = columnValues.values()
                .stream()
                .anyMatch(str -> str.length() + 4 > MAX_LENGTH);

        if (isTooLong) {
            columnValues.forEach((key, value) -> columnValues.put(key, divideAndAlign(value, DIVINE_ON)));
        } else {
            columnValues.forEach((key, value) -> columnValues.put(key, join(splitOnParts(value))));
        }
    }

    private String join(String[] parts) {
        StringBuilder sb = new StringBuilder("(");

        for (String currentWord : parts) {
            currentWord = convertIfKeywordToUppercase(currentWord);
            sb.append(currentWord).append(COMA_SPACE);
        }
        replaceLastEntry(COMA_SPACE, ")", sb);

        return sb.toString();
    }

    private String divideAndAlign(String string, int divideOn) {
        StringBuilder sb = new StringBuilder("(");

        String[] parts = splitOnParts(string);
        for (int i = 0; i < parts.length; i++) {
            var currentWord = convertIfKeywordToUppercase(parts[i]);

            if (i > 0 && i % divideOn == 0) {
                //remove space in end of row
                replaceLastEntry(SPACE, EMPTY_STR, sb);
                sb.append(BR).append(TAB_SPACE);
            }

            sb.append(currentWord).append(COMA_SPACE);
        }

        replaceLastEntry(COMA_SPACE, ")", sb);

        return sb.toString();
    }

    private String[] splitOnParts(String valuesGroup) {
        return valuesGroup.split("\\s*,\\s*");
    }
}
