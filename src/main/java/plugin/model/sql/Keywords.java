package plugin.model.sql;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Keywords {


    public enum Group {
        DML, DDL, CONDITIONAL, FUNCTIONS, OPERATORS, JOINS, DATA_TYPES, TRANSACTIONS
    }

    public enum DDL {
        CREATE, ALTER, DROP, TRUNCATE, TABLE, CONSTRAINT, PRIMARY, KEY, UNIQUE
    }

    public enum DML {
        SELECT, INSERT, UPDATE, DELETE, FROM, INTO
    }

    public enum Conditional {
        WHERE, AND, OR, IN, ANY, BETWEEN, LIKE,
    }

    public enum Functions {
        COUNT, SUM, AVG, MIN, MAX,
        CONCAT, SUBSTRING, LENGTH, UPPER, LOWER, TRIM, REPLACE,
        GETDATE, DATEADD, DATEDIFF, YEAR, MONTH, DAY, HOUR, MINUTE, SECOND
    }

    public enum Operators {
        NOT, NUll, DEFAULT
    }

    public enum Joins {
        JOIN, INNER, LEFT, RIGHT, FULL, CROSS, SELF
    }

    public enum DataTypes {
        INT, VARCHAR, CHAR, DATE, TIME, TIMESTAMP, FLOAT, BOOLEAN, BOOL, TRUE, FALSE
    }

    public enum Transactions {
        BEGIN_TRANSACTION, COMMIT, ROLLBACK, SAVEPOINT
    }


    // Map to associate keywords with their corresponding groups
    private static final Map<String, Group> keywordGroupMap = new HashMap<>();
    private static final Map<Group, Set<String>> groupKeywordsMap = new EnumMap<>(Group.class);

    static {
        putGroup(Group.DDL, DDL.values());
        putGroup(Group.DML, DML.values());
        putGroup(Group.CONDITIONAL, Conditional.values());
        putGroup(Group.OPERATORS, Operators.values());
        putGroup(Group.FUNCTIONS, Functions.values());
        putGroup(Group.JOINS, Joins.values());
        putGroup(Group.DATA_TYPES, DataTypes.values());
        putGroup(Group.TRANSACTIONS, Transactions.values());
    }

    private static void putGroup(Group group, Enum[] keywords) {
        associateKeywordsWithGroup(group, keywords);
        associateGroupWithKeywords(group, keywords);
    }

    private static void associateKeywordsWithGroup(Group group, Enum[] keywords) {
        Arrays.stream(keywords)
                .map(Enum::name)
                .forEach(keyword -> keywordGroupMap.put(keyword, group));
    }

    private static void associateGroupWithKeywords(Group group, Enum[] keywords) {
        groupKeywordsMap.put(group, Arrays.stream(keywords)
                .map(Enum::name)
                .collect(Collectors.toSet()));
    }

    public static boolean isKeyword(String candidate) {
        return keywordGroupMap.containsKey(candidate.toUpperCase());
    }

    public static boolean isKeyword(String candidate, Group group) {
        return groupKeywordsMap.get(group).contains(candidate.toUpperCase());
    }

    public static String convertIfKeywordToUppercase(String candidate) {
        String candidateInUppercase = candidate.toUpperCase();

        if (keywordGroupMap.containsKey(candidateInUppercase)) {
            return candidateInUppercase;
        } else {
            return candidate;
        }
    }
}
