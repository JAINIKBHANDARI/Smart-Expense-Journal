import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputHelper {
    private final Scanner scanner = new Scanner(System.in);

    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            String input = readLine(prompt);
            if (input == null) {
                return -1;
            }
            try {
                int value = Integer.parseInt(input.trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("[ERROR] Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException exception) {
                System.out.println("[ERROR] Please enter a valid number.");
            }
        }
    }

    public int readPositiveInt(String prompt) {
        while (true) {
            String input = readLine(prompt);
            if (input == null) {
                return -1;
            }
            try {
                int value = Integer.parseInt(input.trim());
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                // Message below handles invalid numbers.
            }
            System.out.println("[ERROR] Please enter a positive whole number.");
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            String input = readLine(prompt);
            if (input == null || input.trim().isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(input.trim());
            } catch (DateTimeParseException exception) {
                System.out.println("[ERROR] Date must be in yyyy-MM-dd format.");
            }
        }
    }

    public LocalDate readOptionalDate(String prompt, LocalDate oldValue) {
        while (true) {
            String input = readLine(prompt);
            if (input == null || input.trim().isEmpty()) {
                return oldValue;
            }
            try {
                return LocalDate.parse(input.trim());
            } catch (DateTimeParseException exception) {
                System.out.println("[ERROR] Date must be in yyyy-MM-dd format.");
            }
        }
    }

    public BigDecimal readPositiveAmount(String prompt) {
        while (true) {
            String input = readLine(prompt);
            if (input == null) {
                return BigDecimal.ZERO;
            }
            try {
                BigDecimal amount = new BigDecimal(input.trim().replace(",", ""));
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    return amount.setScale(2, RoundingMode.HALF_UP);
                }
            } catch (NumberFormatException exception) {
                // Message below handles invalid amounts.
            }
            System.out.println("[ERROR] Amount must be a positive number, example 250.75.");
        }
    }

    public BigDecimal readOptionalAmount(String prompt, BigDecimal oldValue) {
        while (true) {
            String input = readLine(prompt);
            if (input == null || input.trim().isEmpty()) {
                return oldValue;
            }
            try {
                BigDecimal amount = new BigDecimal(input.trim().replace(",", ""));
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    return amount.setScale(2, RoundingMode.HALF_UP);
                }
            } catch (NumberFormatException exception) {
                // Message below handles invalid amounts.
            }
            System.out.println("[ERROR] Amount must be a positive number.");
        }
    }

    public String readRequiredText(String prompt, int maxLength) {
        while (true) {
            String input = readText(prompt, maxLength);
            if (!input.trim().isEmpty()) {
                return input;
            }
            System.out.println("[ERROR] This field cannot be empty.");
        }
    }

    public String readText(String prompt, int maxLength) {
        while (true) {
            String input = readLine(prompt);
            if (input == null) {
                return "";
            }
            input = input.trim();
            if (input.length() <= maxLength) {
                return input;
            }
            System.out.println("[ERROR] Please keep it within " + maxLength + " characters.");
        }
    }

    public String readOptionalText(String prompt, String oldValue, int maxLength) {
        String input = readText(prompt, maxLength);
        return input.trim().isEmpty() ? oldValue : input;
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String input = readLine(prompt);
            if (input == null) {
                return false;
            }
            input = input.trim().toLowerCase();
            if ("y".equals(input) || "yes".equals(input)) {
                return true;
            }
            if ("n".equals(input) || "no".equals(input)) {
                return false;
            }
            System.out.println("[ERROR] Please enter y or n.");
        }
    }

    public void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println();
            return null;
        }
        return scanner.nextLine();
    }
}
