import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHandler {
    private static final Path DATA_FOLDER = Paths.get("data");
    private static final Path EXPENSE_FILE = DATA_FOLDER.resolve("expenses.csv");
    private static final Path BUDGET_FILE = DATA_FOLDER.resolve("budgets.csv");
    private static final Path REPORT_FILE = Paths.get("expense_report.txt");

    public List<Expense> loadExpenses() {
        ensureDataFiles();
        ArrayList<Expense> expenses = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(EXPENSE_FILE, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty() || line.startsWith("id,")) {
                    continue;
                }
                try {
                    List<String> values = parseCsvLine(line);
                    if (values.size() != 5) {
                        throw new IllegalArgumentException("Wrong column count");
                    }
                    expenses.add(new Expense(
                            Integer.parseInt(values.get(0)),
                            LocalDate.parse(values.get(1)),
                            values.get(2),
                            new BigDecimal(values.get(3)),
                            values.get(4)
                    ));
                } catch (RuntimeException exception) {
                    System.out.println("[WARNING] Skipped invalid expense line " + lineNumber + ".");
                }
            }
        } catch (IOException exception) {
            System.out.println("[WARNING] Could not read expenses file. Starting empty.");
        }

        return expenses;
    }

    public Map<YearMonth, BigDecimal> loadBudgets() {
        ensureDataFiles();
        HashMap<YearMonth, BigDecimal> budgets = new HashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(BUDGET_FILE, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty() || line.startsWith("month,")) {
                    continue;
                }
                try {
                    List<String> values = parseCsvLine(line);
                    budgets.put(YearMonth.parse(values.get(0)), new BigDecimal(values.get(1)));
                } catch (RuntimeException exception) {
                    System.out.println("[WARNING] Skipped invalid budget line " + lineNumber + ".");
                }
            }
        } catch (IOException exception) {
            System.out.println("[WARNING] Could not read budget file.");
        }

        return budgets;
    }

    public boolean saveExpenses(List<Expense> expenses) {
        ensureDataFiles();
        try (BufferedWriter writer = Files.newBufferedWriter(EXPENSE_FILE, StandardCharsets.UTF_8)) {
            writer.write("id,date,category,amount,note");
            writer.newLine();
            for (Expense expense : expenses) {
                writer.write(toCsv(
                        String.valueOf(expense.getId()),
                        expense.getDate().toString(),
                        expense.getCategory(),
                        expense.getAmount().toPlainString(),
                        expense.getNote()
                ));
                writer.newLine();
            }
            return true;
        } catch (IOException exception) {
            System.out.println("[ERROR] Could not save expenses.");
            return false;
        }
    }

    public boolean saveBudgets(Map<YearMonth, BigDecimal> budgets) {
        ensureDataFiles();
        try (BufferedWriter writer = Files.newBufferedWriter(BUDGET_FILE, StandardCharsets.UTF_8)) {
            writer.write("month,amount");
            writer.newLine();
            for (Map.Entry<YearMonth, BigDecimal> entry : budgets.entrySet()) {
                writer.write(toCsv(entry.getKey().toString(), entry.getValue().toPlainString()));
                writer.newLine();
            }
            return true;
        } catch (IOException exception) {
            System.out.println("[ERROR] Could not save budgets.");
            return false;
        }
    }

    public boolean exportReport(String report) {
        try (BufferedWriter writer = Files.newBufferedWriter(REPORT_FILE, StandardCharsets.UTF_8)) {
            writer.write(report);
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    private void ensureDataFiles() {
        try {
            Files.createDirectories(DATA_FOLDER);
            if (!Files.exists(EXPENSE_FILE)) {
                Files.write(EXPENSE_FILE, java.util.Arrays.asList("id,date,category,amount,note"), StandardCharsets.UTF_8);
            }
            if (!Files.exists(BUDGET_FILE)) {
                Files.write(BUDGET_FILE, java.util.Arrays.asList("month,amount"), StandardCharsets.UTF_8);
            }
        } catch (IOException exception) {
            System.out.println("[WARNING] Could not prepare data folder.");
        }
    }

    private String toCsv(String... values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(escapeCsv(values[i]));
        }
        return builder.toString();
    }

    private String escapeCsv(String value) {
        String safe = value == null ? "" : value;
        if (safe.contains(",") || safe.contains("\"") || safe.contains("\n")) {
            return "\"" + safe.replace("\"", "\"\"") + "\"";
        }
        return safe;
    }

    private List<String> parseCsvLine(String line) {
        ArrayList<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (character == ',' && !insideQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        values.add(current.toString());
        return values;
    }
}
