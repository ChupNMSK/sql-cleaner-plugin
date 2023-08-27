package plugin.formatter.dml.insert;

import static plugin.config.GeneralConfig.BR;
import static plugin.config.GeneralConfig.DIVINE_ON;
import static plugin.config.GeneralConfig.MAX_LENGTH;
import static plugin.config.GeneralConfig.TAB;
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
            Pattern.compile("(?i)INSERT\\s+INTO\\s+(.*?)\\s+");
    private static final Pattern COLUMNS_VALUES_PATTERN = Pattern.compile("\\(([^),]+(?:,\\s*[^),]+)+)\\)");

    @Override
    public String format(String query) {
        StringBuilder formattedQuery = new StringBuilder();
        String tableName = getTableName(query);
        Map<Integer, String> map = getColumnValues(query);

        log.info("Column values map: {}", map);
        boolean isTooLong = map.values()
                .stream()
                .anyMatch(str -> str.length() + 4 > MAX_LENGTH);
        if (isTooLong) {
            map.forEach((key, value) -> map.put(key, divideAndAlign(value, DIVINE_ON)));
        } else {
            map.forEach((key, value) -> map.put(key, join(splitOnParts(value))));
        }

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
                    .append(",")
                    .append(BR)
                    .append(TAB);
        }

        replaceLastEntry("," + BR, ";", formattedQuery);

        log.info("Formatted query {}", formattedQuery);
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
        log.info("Column values map: {}", columnValues);
        return columnValues;
    }

    private Map<Integer, String> parseColumnAndValues(String query) {
        Map<Integer, String> columnValues = new HashMap<>();

        Matcher matcher = COLUMNS_VALUES_PATTERN.matcher(query);
        for (int group = 0; matcher.find(); group++) {
            columnValues.put(group, matcher.group());
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
        return String.join(", ", parts);
    }

    private String divideAndAlign(String string, int divideOn) {
        StringBuilder sb = new StringBuilder();

        String[] parts = splitOnParts(string);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0 && i % divideOn == 0) {
                //remove space in end of row
                replaceLastEntry(" ", "", sb);
                sb.append(BR).append(TAB + " ");
            }

            sb.append(parts[i]).append(", ");
        }

        replaceLastEntry(", ", "", sb);

        return sb.toString();
    }

    private String[] splitOnParts(String valuesGroup) {
        return valuesGroup.split("\\s*,\\s*");
    }
}
