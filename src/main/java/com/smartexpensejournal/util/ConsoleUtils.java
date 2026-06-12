package com.smartexpensejournal.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Scanner;

public final class ConsoleUtils {
    private static final int WIDTH = 72;

    private ConsoleUtils() {
    }

    public static void printTitle(String title) {
        printLine();
        System.out.println(center(title.toUpperCase(Locale.ROOT), WIDTH));
        printLine();
    }

    public static void printSection(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println(repeat("-", Math.min(WIDTH, Math.max(12, title.length()))));
    }

    public static void printLine() {
        System.out.println(repeat("=", WIDTH));
    }

    public static void printSuccess(String message) {
        System.out.println("[OK] " + message);
    }

    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void pause(Scanner scanner) {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    public static String money(BigDecimal amount) {
        BigDecimal safeAmount = amount == null ? BigDecimal.ZERO : amount;
        return "INR " + safeAmount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String fit(String text, int width) {
        String safeText = text == null ? "" : text.replace('\n', ' ').replace('\r', ' ');
        if (safeText.length() <= width) {
            return safeText;
        }
        if (width <= 3) {
            return safeText.substring(0, width);
        }
        return safeText.substring(0, width - 3) + "...";
    }

    public static String repeat(String value, int count) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < count; index++) {
            builder.append(value);
        }
        return builder.toString();
    }

    private static String center(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        return repeat(" ", padding) + text;
    }
}
