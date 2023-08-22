package plugin.formatter.ddl.create;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joni.exception.SyntaxException;

import lombok.extern.slf4j.Slf4j;
import plugin.formatter.QueryFormatter;
import plugin.model.sql.Keywords;

@Slf4j
public class CreateTableFormatter implements QueryFormatter {
    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("(?i)CREATE\\s+TABLE\\s+(.*?)\\s");
    private static final Pattern COLUMN_PATTERN = Pattern.compile("(\\w+)\\s+((?:\\w+\\(\\d+(?:,\\d+)?\\)|\\w+)\\s*)(.*?)(?:,\\s*|$)");

    private static final String COLUMN_NAME = "columnNames";
    private static final String COLUMN_TYPE = "columnType";
    private static final String COLUMN_CONSTRAINT = "columnConstraint";

    @Override
    public String format(String query) {
        StringBuilder formattedQuery = new StringBuilder();

        String createTable = getCreateTable(query);
        formattedQuery.append(createTable).append("\n(\n");

        String valueBetweenParentheses = valueBetweenParentheses(query);

        Matcher columnMatcher = COLUMN_PATTERN.matcher(valueBetweenParentheses);

        Map<String, List<String>> columnsMap = new LinkedHashMap<>();
        columnsMap.put(COLUMN_NAME, new ArrayList<>());
        columnsMap.put(COLUMN_TYPE, new ArrayList<>());
        columnsMap.put(COLUMN_CONSTRAINT, new ArrayList<>());

        while (columnMatcher.find()) {
            String columnName = columnMatcher.group(1).toLowerCase().trim();
            if(Keywords.DDL.CONSTRAINT.name().equalsIgnoreCase(columnName)) {
                columnName = columnName.toUpperCase();
            }
            String columnType = columnMatcher.group(2).toUpperCase().trim();
            String constraints = columnMatcher.group(3).toUpperCase().trim();

            columnsMap.get(COLUMN_NAME).add(columnName);
            columnsMap.get(COLUMN_TYPE).add(columnType);
            columnsMap.get(COLUMN_CONSTRAINT).add(constraints);
        }

        String columns = alignTable(columnsMap);
        formattedQuery.append(columns);

        formattedQuery.replace(formattedQuery.lastIndexOf(","), formattedQuery.length(), "\n);");

        return formattedQuery.toString();
    }

    private String getCreateTable(String query) {
        Matcher matcher = CREATE_TABLE_PATTERN.matcher(query);
        if(!matcher.find()) {
            throw new SyntaxException("Table name not fount");
        }

        return "CREATE TABLE " + matcher.group(1).toLowerCase();
    }

    private String valueBetweenParentheses(String query) {
        int openParentheses = query.indexOf('(');
        int closeParentheses = query.lastIndexOf(")");
        return query.substring(openParentheses, closeParentheses);
    }

    public String alignTable(Map<String, List<String>> columnsMap) {
        int rowCount = columnsMap.values().stream().findFirst().map(List::size).orElse(0);

        // Calculate column widths for alignment
        Map<String, Integer> columnWidths = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : columnsMap.entrySet()) {
            String column = entry.getKey();
            int maxColumnWidth = entry.getValue()
                                        .stream()
                                        .mapToInt(String::length)
                                        .max()
                                        .orElse(0);
            columnWidths.put(column, maxColumnWidth);
        }
        log.info("Columns map {}, Column width {}",columnsMap, columnWidths);
        StringBuilder result = new StringBuilder();

        // Iterate over rows
        for (int i = 0; i < rowCount; i++) {
            StringBuilder rowString = new StringBuilder("\t");

            for (String column : columnsMap.keySet()) {
                if(column.equals(COLUMN_CONSTRAINT)) {
                    rowString.append(columnsMap.get(column).get(i));
                } else {
                    rowString.append(alignRow(columnWidths.get(column), columnsMap.get(column).get(i)));
                }
            }

            rowString.append(",\n");
            result.append(rowString);
        }
        return result.toString();
    }

    private String alignRow(int columnWidth, String columnValue) {
        return String.format("%-" + columnWidth + "s ", columnValue);
    }
}


