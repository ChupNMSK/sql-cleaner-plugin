package plugin.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringsUtil {

    public static void replaceLastEntry(String oldValue, String newValue, StringBuilder sb) {
        sb.replace(sb.lastIndexOf(oldValue), sb.length(), newValue);
    }

    public static String leftAlign(int columnWidth, String columnValue) {
        return String.format("%-" + columnWidth + "s ", columnValue);
    }
}
