import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportGenerator {
    private static final int TABLE_WIDTH = 95;

    public static void printExpenseTable(List<Expense> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            System.out.println("[INFO] No expenses found.");
            return;
        }

        printSeparator();
        System.out.printf("%-5s %-12s %-15s %14s  %-40s%n", "ID", "Date", "Category", "Amount", "Note");
        printSeparator();
        for (Expense expense : expenses) {
            System.out.printf("%-5d %-12s %-15s %14s  %-40s%n",
                    expense.getId(),
                    expense.getDate(),
                    fit(expense.getCategory(), 15),
                    Main.formatMoney(expense.getAmount()),
                    fit(expense.getNote(), 40));
        }
        printSeparator();
        System.out.println("Rows: " + expenses.size());
    }

    public static void printAmountMap(String title, Map<String, BigDecimal> values) {
        System.out.println();
        System.out.println(title);
        printSeparator();
        if (values.isEmpty()) {
            System.out.println("[INFO] No data available.");
            return;
        }
        for (Map.Entry<String, BigDecimal> entry : values.entrySet()) {
            System.out.printf("%-25s %15s%n", entry.getKey(), Main.formatMoney(entry.getValue()));
        }
    }

    public static String generateReport(ExpenseManager manager) {
        StringBuilder report = new StringBuilder();
        report.append("SMART EXPENSE JOURNAL REPORT").append(System.lineSeparator());
        report.append("Generated: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .append(System.lineSeparator());
        report.append(repeat("=", TABLE_WIDTH)).append(System.lineSeparator());
        report.append("Total spending: ").append(Main.formatMoney(manager.getTotalSpending())).append(System.lineSeparator());
        report.append("Total records : ").append(manager.getAllExpenses().size()).append(System.lineSeparator());
        report.append(System.lineSeparator());
        report.append("Recent Expenses").append(System.lineSeparator());
        report.append(expenseTableAsText(manager.getRecentExpenses(5)));
        report.append(System.lineSeparator());
        report.append(amountMapAsText("Category-wise Spending", manager.getCategoryWiseSummary()));
        report.append(System.lineSeparator());
        report.append(amountMapAsText("Monthly Spending", manager.getMonthlySummary()));
        return report.toString();
    }

    private static String expenseTableAsText(List<Expense> expenses) {
        StringBuilder builder = new StringBuilder();
        builder.append(repeat("-", TABLE_WIDTH)).append(System.lineSeparator());
        builder.append(String.format("%-5s %-12s %-15s %14s  %-40s%n", "ID", "Date", "Category", "Amount", "Note"));
        builder.append(repeat("-", TABLE_WIDTH)).append(System.lineSeparator());
        for (Expense expense : expenses) {
            builder.append(String.format("%-5d %-12s %-15s %14s  %-40s%n",
                    expense.getId(),
                    expense.getDate(),
                    fit(expense.getCategory(), 15),
                    Main.formatMoney(expense.getAmount()),
                    fit(expense.getNote(), 40)));
        }
        builder.append(repeat("-", TABLE_WIDTH)).append(System.lineSeparator());
        return builder.toString();
    }

    private static String amountMapAsText(String title, Map<String, BigDecimal> values) {
        StringBuilder builder = new StringBuilder();
        builder.append(title).append(System.lineSeparator());
        builder.append(repeat("-", TABLE_WIDTH)).append(System.lineSeparator());
        for (Map.Entry<String, BigDecimal> entry : values.entrySet()) {
            builder.append(String.format("%-25s %15s%n", entry.getKey(), Main.formatMoney(entry.getValue())));
        }
        return builder.toString();
    }

    private static void printSeparator() {
        System.out.println(repeat("-", TABLE_WIDTH));
    }

    private static String fit(String text, int width) {
        if (text == null) {
            return "";
        }
        String cleaned = text.replace('\n', ' ').replace('\r', ' ');
        if (cleaned.length() <= width) {
            return cleaned;
        }
        return cleaned.substring(0, width - 3) + "...";
    }

    private static String repeat(String value, int times) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(value);
        }
        return builder.toString();
    }
}
