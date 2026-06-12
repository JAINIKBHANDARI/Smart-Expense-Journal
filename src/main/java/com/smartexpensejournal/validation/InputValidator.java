package com.smartexpensejournal.validation;

import com.smartexpensejournal.model.ExpenseCategory;
import com.smartexpensejournal.util.ConsoleUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class InputValidator {
    private InputValidator() {
    }

    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            int value = readPositiveInt(scanner, prompt);
            if (value >= min && value <= max) {
                return value;
            }
            ConsoleUtils.printError("Enter a number from " + min + " to " + max + ".");
        }
    }

    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            ConsoleUtils.printError("Enter a positive whole number.");
        }
    }

    public static LocalDate readDate(Scanner scanner, String prompt, boolean allowBlankToday) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (allowBlankToday && input.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException exception) {
                ConsoleUtils.printError("Use date format yyyy-MM-dd.");
            }
        }
    }

    public static YearMonth readYearMonth(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return YearMonth.parse(input);
            } catch (DateTimeParseException exception) {
                ConsoleUtils.printError("Use month format yyyy-MM.");
            }
        }
    }

    public static ExpenseCategory readCategory(Scanner scanner, String prompt) {
        System.out.println("Available: " + ExpenseCategory.availableOptions());
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            ExpenseCategory category = ExpenseCategory.fromInput(input);
            if (!input.isEmpty() || category == ExpenseCategory.OTHER) {
                return category;
            }
            ConsoleUtils.printError("Enter a category name.");
        }
    }

    public static BigDecimal readPositiveAmount(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().replace(",", "");
            try {
                BigDecimal amount = new BigDecimal(input);
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    return amount.setScale(2, RoundingMode.HALF_UP);
                }
            } catch (NumberFormatException exception) {
                ConsoleUtils.printError("Enter a valid amount, for example 249.99.");
                continue;
            }
            ConsoleUtils.printError("Amount must be greater than zero.");
        }
    }

    public static String readRequiredText(Scanner scanner, String prompt, int maxLength) {
        while (true) {
            String text = readOptionalText(scanner, prompt, maxLength);
            if (!text.isEmpty()) {
                return text;
            }
            ConsoleUtils.printError("This field is required.");
        }
    }

    public static String readOptionalText(Scanner scanner, String prompt, int maxLength) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.length() <= maxLength) {
                return input;
            }
            ConsoleUtils.printError("Keep it within " + maxLength + " characters.");
        }
    }

    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if ("y".equals(input) || "yes".equals(input)) {
                return true;
            }
            if ("n".equals(input) || "no".equals(input)) {
                return false;
            }
            ConsoleUtils.printError("Please enter y or n.");
        }
    }
}
