package pucrs.util;

public class Util {

    public static int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String removeParams(String s) {
        StringBuilder builder = new StringBuilder(s);
        builder.deleteCharAt(0);
        builder.deleteCharAt(s.length()-2);
        return builder.toString();
    }
}
