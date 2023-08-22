package plugin.formatter.dml.insert;

import static plugin.model.xml.enums.QueryType.INSERT;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import plugin.formatter.Formatter;
import plugin.formatter.QueryFormatter;

@Slf4j
@Formatter(queryType = INSERT)
public class InsertQueryFormatter implements QueryFormatter {

    private static final Pattern INSERT_INTO_PATTERN =
                                    Pattern.compile("(?i)INSERT\\s+INTO\\s+(.*?)\\s+");
    private static final Pattern COLUMNS_VALUES_PATTERN = Pattern.compile("\\(([^),]+(?:,\\s*[^),]+)+)\\)");

    private static int MAX_LENGHT = 120;

    private static int DIVINE_ON = 3;

    @Override
    public String format(String query) {
        StringBuilder formattedQuery = new StringBuilder();
        String tableName = getTableName(query);
        Map<Integer, String> map = getColumnValues(query);
        log.info("Column values map: {}", map);
        boolean isTooLong = map.values()
                                .stream()
                                .anyMatch(str -> str.length() + 4 > MAX_LENGHT);
        if(isTooLong) {
            map.forEach((key, value) -> map.put(key, divideAndAlign(value, DIVINE_ON)));
        } else {
            map.forEach((key, value) -> map.put(key, join(splitOnParts(value))));
        }

        String columNames = map.remove(0); //first group is columns

        //append insert into table_name
        formattedQuery.append("INSERT INTO ").append(tableName.toLowerCase()).append("\n");

        //append column names
        formattedQuery.append("\t").append(columNames.toLowerCase()).append("\n");

        //append values
        formattedQuery.append("VALUES\n").append("\t");
        for(String valueGroup : map.values()) {
            formattedQuery.append(valueGroup).append(",\n").append("\t");
        }

        replaceLastEntry(",\n", ";", formattedQuery);

        log.info("Formatted query {}" , formattedQuery);
        return formattedQuery.toString();
    }

    private void replaceLastEntry(String oldValue, String newValue, StringBuilder sb) {
        sb.replace(sb.lastIndexOf(oldValue), sb.length(), newValue);
    }

    private String getTableName(String query) {
        Matcher matcher = INSERT_INTO_PATTERN.matcher(query);
        matcher.find();
        return matcher.group(1).trim();
    }

    private Map<Integer, String> getColumnValues(String query) {
        Map<Integer, String> columnValues = new HashMap<>();

        Matcher matcher = COLUMNS_VALUES_PATTERN.matcher(query);

        for(int group = 0; matcher.find(); group++) {
            columnValues.put(group, matcher.group());
        }

        return columnValues;
    }

    private String join(String[] parts) {
        return String.join(", ", parts);
    }

    private String divideAndAlign(String string, int divideOn) {
        StringBuilder sb = new StringBuilder();

        String[] parts = splitOnParts(string);
        for (int i = 0; i < parts.length; i++) {
            if(i > 0 && i % divideOn == 0) {
                sb.append("\n").append("\t ");
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
