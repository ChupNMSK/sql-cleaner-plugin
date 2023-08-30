package plugin.formatter.ddl.create;

import static plugin.model.sql.Keywords.DDL.CONSTRAINT;
import static plugin.util.StringsUtil.BR;
import static plugin.util.StringsUtil.COMA;
import static plugin.util.StringsUtil.COMA_BR;
import static plugin.util.StringsUtil.EMPTY_STR;
import static plugin.util.StringsUtil.TAB;
import static plugin.util.StringsUtil.leftAlign;

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
import plugin.util.StringsUtil;

@Slf4j
public class CreateTableQueryFormatter implements QueryFormatter {
    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile("(?i)CREATE\\s+TABLE\\s+(.*?)\\s");
    private static final Pattern COLUMN_PATTERN =
            Pattern.compile("(\\w+)\\s+((?:\\w+\\(\\d+(?:,\\d+)?\\)|\\w+)\\s*)(.*?)(?:,\\s*|$)");

    private static final String COLUMN_NAME = "columnNames";
    private static final String COLUMN_TYPE = "columnType";
    private static final String COLUMN_CONSTRAINT = "columnConstraint";

    @Override
    public String format(String query) {
        StringBuilder formattedQuery = new StringBuilder();

        formattedQuery
                .append("CREATE TABLE ")
                .append(tableName(query))
                .append(BR)
                .append("(")
                .append(BR)
                .append(columnNamesTypesConstraints(query))
                .append(BR)
                .append(");");

        return formattedQuery.toString();
    }

    private String tableName(String query) {
        Matcher matcher = CREATE_TABLE_PATTERN.matcher(query);
        if (!matcher.find()) {
            throw new SyntaxException("Table name not fount");
        }

        return matcher.group(1).toLowerCase();
    }

    private String columnNamesTypesConstraints(String query) {
        String valueBetweenParentheses = valueBetweenParentheses(query);

        Map<String, List<String>> columnsMap = new LinkedHashMap<>();
        columnsMap.put(COLUMN_NAME, new ArrayList<>());
        columnsMap.put(COLUMN_TYPE, new ArrayList<>());
        columnsMap.put(COLUMN_CONSTRAINT, new ArrayList<>());

        Matcher columnMatcher = COLUMN_PATTERN.matcher(valueBetweenParentheses);
        while (columnMatcher.find()) {
            String columnName = columnMatcher.group(1).toLowerCase().trim();
            if (CONSTRAINT.name().equalsIgnoreCase(columnName)) {
                columnName = columnName.toUpperCase();
            }
            String columnType = columnMatcher.group(2).toUpperCase().trim();
            String constraints = columnMatcher.group(3).toUpperCase().trim();

            columnsMap.get(COLUMN_NAME).add(columnName);
            columnsMap.get(COLUMN_TYPE).add(columnType);
            columnsMap.get(COLUMN_CONSTRAINT).add(constraints);
        }

        return alignColumnNamesTypesConstraints(columnsMap);
    }

    private String valueBetweenParentheses(String query) {
        int openParentheses = query.indexOf('(');
        int closeParentheses = query.lastIndexOf(")");
        return query.substring(openParentheses, closeParentheses);
    }

    public String alignColumnNamesTypesConstraints(Map<String, List<String>> columnsMap) {
        int rowCount = columnsMap.values()
                .stream()
                .findFirst()
                .map(List::size)
                .orElse(0);

        // Calculate column widths for alignment
        Map<String, Integer> columnWidths = foundMaxLengthForEachColumn(columnsMap);

        StringBuilder columnNamesTypesConstraints = new StringBuilder();

        // Iterate over rows
        for (int i = 0; i < rowCount; i++) {

            StringBuilder rowString = new StringBuilder(TAB);
            for (String column : columnsMap.keySet()) {
                if (column.equals(COLUMN_CONSTRAINT)) {
                    rowString.append(columnsMap.get(column).get(i));
                } else {
                    rowString.append(leftAlign(columnWidths.get(column), columnsMap.get(column).get(i)));
                }
            }
            rowString.append(COMA_BR);

            columnNamesTypesConstraints
                    .append(rowString);
        }

        StringsUtil.replaceLastEntry(COMA, EMPTY_STR, columnNamesTypesConstraints);

        return columnNamesTypesConstraints.toString();
    }


    // Calculate column widths for alignment
    private Map<String, Integer> foundMaxLengthForEachColumn(Map<String, List<String>> columnsMap) {
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
        log.debug("Columns map {}, Column width {}", columnsMap, columnWidths);
        return columnWidths;
    }
}


