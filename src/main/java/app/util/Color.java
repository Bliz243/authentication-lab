package app.util;

public class Color {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";

    public static String yellow(String str) {
        return ANSI_YELLOW + str + ANSI_RESET;
    }

    public static String blue(String str) {
        return ANSI_BLUE + str + ANSI_RESET;
    }

    public static String red(String str) {
        return ANSI_RED + str + ANSI_RESET;
    }

    public static String green(String str) {
        return ANSI_GREEN + str + ANSI_RESET;
    }
}
