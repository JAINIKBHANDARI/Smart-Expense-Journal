public class Main {
    private static final InputHelper input = new InputHelper();
    private static final FileHandler fileHandler = new FileHandler();
    private static final ExpenseManager manager = new ExpenseManager();

    public static void main(String[] args) {
        if (args.length == 0) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new ExpenseJournalUI().setVisible(true);
                }
            });
            return;
        }

        if ("--ui-check".equalsIgnoreCase(args[0])) {
            ExpenseJournalUI ui = new ExpenseJournalUI();
            ui.dispose();
            System.out.println("UI check passed.");
            return;
        }

        loadData();
        runMenu();
    }

    private static void loadData() {
        manager.loadExpenses(fileHandler.loadExpenses());
        manager.loadBudgets(fileHandler.loadBudgets());
    }

    private static void runMenu() {
        boolean running = true;

        while (running) {
            printHome();
            int choice = input.readIntInRange("Choose option: ", 1, 12);
            if (choice == -1) {
                saveData();
                break;
            }

            switch (choice) {
                case 1:
                    addExpense();
                    break;
                case 2:
                    showExpenses("All Expenses", manager.getAllExpenses());
                    break;
                case 3:
                    editExpense();
                    break;
                case 4:
                    deleteExpense();
                    break;
                case 5:
                    searchNotes();
                    break;
                case 6:
                    filterByCategory();
                    break;
                case 7:
                    filterByMonth();
                    break;
                case 8:
                    showSummaries();
                    break;
                case 9:
                    showExpenses("Recent 5 Expenses", manager.getRecentExpenses(5));
                    break;
                case 10:
                    setBudget();
                    break;
                case 11:
                    exportReport();
                    break;
                case 12:
                    saveData();
                    printSuccess("Data saved. Goodbye!");
                    running = false;
                    break;
                default:
                    printError("Invalid menu option.");
            }

            if (running) {
                input.pressEnterToContinue();
            }
        }
    }

    private static void printHome() {
        clearScreen();
        printLine("=");
        System.out.println(center("SMART EXPENSE JOURNAL", 78));
        System.out.println(center("Core Java Console Expense Tracker", 78));
        printLine("=");
        System.out.printf("Total Expenses: %-5d  Total Spending: %s%n",
                manager.getAllExpenses().size(), formatMoney(manager.getTotalSpending()));
        printCurrentMonthBudgetStatus();
        printLine("-");
        System.out.println(" 1. Add Expense");
        System.out.println(" 2. View All Expenses");
        System.out.println(" 3. Edit Expense");
        System.out.println(" 4. Delete Expense");
        System.out.println(" 5. Search Notes");
        System.out.println(" 6. Filter By Category");
        System.out.println(" 7. Filter By Month And Year");
        System.out.println(" 8. View Spending Summary");
        System.out.println(" 9. Recent 5 Expenses");
        System.out.println("10. Set Monthly Budget");
        System.out.println("11. Export Text Report");
        System.out.println("12. Save And Exit");
        printLine("-");
    }

    private static void addExpense() {
        printHeading("Add Expense");
        Expense expense = manager.addExpense(
                input.readDate("Date (yyyy-MM-dd, blank for today): "),
                input.readRequiredText("Category: ", 25),
                input.readPositiveAmount("Amount: "),
                input.readText("Note: ", 80)
        );
        saveData();
        printSuccess("Expense added with ID " + expense.getId() + ".");
        showBudgetWarning(expense.getDate().getYear(), expense.getDate().getMonthValue());
    }

    private static void editExpense() {
        printHeading("Edit Expense");
        int id = input.readPositiveInt("Enter expense ID: ");
        Expense expense = manager.findById(id);
        if (expense == null) {
            printError("No expense found with ID " + id + ".");
            return;
        }

        System.out.println("Current expense:");
        ReportGenerator.printExpenseTable(java.util.Arrays.asList(expense));
        System.out.println("Press Enter to keep old value.");

        Expense updated = new Expense(
                expense.getId(),
                input.readOptionalDate("New date (" + expense.getDate() + "): ", expense.getDate()),
                input.readOptionalText("New category (" + expense.getCategory() + "): ", expense.getCategory(), 25),
                input.readOptionalAmount("New amount (" + formatMoney(expense.getAmount()) + "): ", expense.getAmount()),
                input.readOptionalText("New note (" + expense.getNote() + "): ", expense.getNote(), 80)
        );

        manager.updateExpense(updated);
        saveData();
        printSuccess("Expense updated.");
        showBudgetWarning(updated.getDate().getYear(), updated.getDate().getMonthValue());
    }

    private static void deleteExpense() {
        printHeading("Delete Expense");
        int id = input.readPositiveInt("Enter expense ID to delete: ");
        Expense expense = manager.findById(id);
        if (expense == null) {
            printError("No expense found with ID " + id + ".");
            return;
        }

        ReportGenerator.printExpenseTable(java.util.Arrays.asList(expense));
        if (input.readYesNo("Are you sure you want to delete this expense? (y/n): ")) {
            manager.deleteExpense(id);
            saveData();
            printSuccess("Expense deleted.");
        } else {
            printInfo("Delete cancelled.");
        }
    }

    private static void searchNotes() {
        printHeading("Search Notes");
        String keyword = input.readRequiredText("Enter keyword: ", 40);
        showExpenses("Search Results For \"" + keyword + "\"", manager.searchNotes(keyword));
    }

    private static void filterByCategory() {
        printHeading("Filter By Category");
        String category = input.readRequiredText("Enter category: ", 25);
        showExpenses("Expenses In Category \"" + category + "\"", manager.filterByCategory(category));
    }

    private static void filterByMonth() {
        printHeading("Filter By Month And Year");
        int month = input.readIntInRange("Month (1-12): ", 1, 12);
        int year = input.readIntInRange("Year (example 2026): ", 2000, 2100);
        showExpenses("Expenses For " + monthName(month) + " " + year, manager.filterByMonth(year, month));
        showBudgetWarning(year, month);
    }

    private static void showExpenses(String title, java.util.List<Expense> expenses) {
        printHeading(title);
        ReportGenerator.printExpenseTable(expenses);
    }

    private static void showSummaries() {
        printHeading("Spending Summary");
        System.out.println("Total spending: " + formatMoney(manager.getTotalSpending()));
        System.out.println();

        Expense highest = manager.getHighestExpense();
        if (highest == null) {
            printInfo("No expenses available yet.");
        } else {
            System.out.println("Highest expense:");
            ReportGenerator.printExpenseTable(java.util.Arrays.asList(highest));
        }

        ReportGenerator.printAmountMap("Category-wise Spending", manager.getCategoryWiseSummary());
        ReportGenerator.printAmountMap("Monthly Spending", manager.getMonthlySummary());
    }

    private static void setBudget() {
        printHeading("Set Monthly Budget");
        int month = input.readIntInRange("Month (1-12): ", 1, 12);
        int year = input.readIntInRange("Year (example 2026): ", 2000, 2100);
        java.math.BigDecimal amount = input.readPositiveAmount("Budget amount: ");

        manager.setMonthlyBudget(year, month, amount);
        saveData();
        printSuccess("Budget set for " + monthName(month) + " " + year + ": " + formatMoney(amount));
        showBudgetWarning(year, month);
    }

    private static void exportReport() {
        printHeading("Export Report");
        String report = ReportGenerator.generateReport(manager);
        if (fileHandler.exportReport(report)) {
            printSuccess("Report exported to expense_report.txt");
        } else {
            printError("Could not export report.");
        }
    }

    private static void saveData() {
        fileHandler.saveExpenses(manager.getAllExpenses());
        fileHandler.saveBudgets(manager.getMonthlyBudgets());
    }

    private static void printCurrentMonthBudgetStatus() {
        java.time.LocalDate today = java.time.LocalDate.now();
        showBudgetWarning(today.getYear(), today.getMonthValue());
    }

    private static void showBudgetWarning(int year, int month) {
        java.math.BigDecimal budget = manager.getMonthlyBudget(year, month);
        if (budget == null) {
            return;
        }

        java.math.BigDecimal spent = manager.getMonthlySpending(year, month);
        System.out.println("Budget " + monthName(month) + " " + year + ": "
                + formatMoney(spent) + " / " + formatMoney(budget));
        if (spent.compareTo(budget) > 0) {
            printError("Budget crossed by " + formatMoney(spent.subtract(budget)) + ".");
        }
    }

    private static void printHeading(String title) {
        clearScreen();
        printLine("=");
        System.out.println(title);
        printLine("-");
    }

    private static void printLine(String value) {
        System.out.println(repeat(value, 78));
    }

    private static void clearScreen() {
        for (int i = 0; i < 3; i++) {
            System.out.println();
        }
    }

    public static String formatMoney(java.math.BigDecimal amount) {
        if (amount == null) {
            amount = java.math.BigDecimal.ZERO;
        }
        return "INR " + amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private static String monthName(int month) {
        return java.time.Month.of(month).name().substring(0, 1)
                + java.time.Month.of(month).name().substring(1).toLowerCase();
    }

    private static String center(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int leftPadding = (width - text.length()) / 2;
        return repeat(" ", leftPadding) + text;
    }

    private static String repeat(String value, int times) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(value);
        }
        return builder.toString();
    }

    private static void printSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    private static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    private static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }
}
