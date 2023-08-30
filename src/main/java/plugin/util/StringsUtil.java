package plugin.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringsUtil {

    public static final String EMPTY_STR = "";
    public static final String SPACE = " ";
    public static final String COMA = ",";
    public static final String COMA_SPACE = ", ";
    public static final String COMA_BR = ",\n";
    public static final String SEMICOLON = ";";

    public static final String TAB = "    "; //4 spaces

    public static final String TAB_SPACE = "     "; //5 spaces

    public static final String BR = "\n";

    public static void replaceLastEntry(String oldValue, String newValue, StringBuilder sb) {
        sb.replace(sb.lastIndexOf(oldValue), sb.length(), newValue);
    }

    public static String leftAlign(int columnWidth, String columnValue) {
        return String.format("%-" + columnWidth + "s ", columnValue);
    }
}
