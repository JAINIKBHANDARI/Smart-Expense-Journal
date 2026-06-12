package com.smartexpensejournal.app;

import com.smartexpensejournal.exception.ExpenseNotFoundException;
import com.smartexpensejournal.exception.InvalidExpenseException;
import com.smartexpensejournal.io.ExpenseFileStorage;
import com.smartexpensejournal.model.Expense;
import com.smartexpensejournal.model.ExpenseCategory;
import com.smartexpensejournal.service.ExpenseAnalyticsService;
import com.smartexpensejournal.service.ExpenseService;
import com.smartexpensejournal.util.ConsoleUtils;
import com.smartexpensejournal.util.DateUtils;
import com.smartexpensejournal.validation.InputValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ExpenseJournalApp {
    private static final int TABLE_WIDTH = 104;

    private final ExpenseService expenseService;
    private final ExpenseAnalyticsService analyticsService;
    private final ExpenseFileStorage storage;
    private final Scanner scanner;

    public ExpenseJournalApp(
            ExpenseService expenseService,
            ExpenseAnalyticsService analyticsService,
            ExpenseFileStorage storage
    ) {
        this.expenseService = expenseService;
        this.analyticsService = analyticsService;
        this.storage = storage;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        loadExpenses();
        ConsoleUtils.printTitle("Smart Expense Journal");

        boolean running = true;
        while (running) {
            printMenu();
            try {
                int choice = InputValidator.readIntInRange(scanner, "Choose an option: ", 1, MenuOption.values().length);
                MenuOption option = MenuOption.fromNumber(choice);

                switch (option) {
                    case ADD_EXPENSE:
                        addExpense();
                        break;
                    case LIST_ALL:
                        listExpenses(expenseService.listAll());
                        break;
                    case SEARCH:
                        searchExpenses();
                        break;
                    case FILTER_BY_MONTH:
                        filterByMonth();
                        break;
                    case SHOW_TOTALS:
                        showTotals();
                        break;
                    case DELETE_BY_ID:
                        deleteExpense();
                        break;
                    case SAVE:
                        saveExpenses();
                        break;
                    case EXIT:
                        saveExpenses();
                        running = false;
                        ConsoleUtils.printSuccess("Saved. Goodbye!");
                        break;
                    default:
                        ConsoleUtils.printError("Unknown menu option.");
                }
            } catch (InvalidExpenseException | ExpenseNotFoundException exception) {
                ConsoleUtils.printError(exception.getMessage());
            } catch (IOException exception) {
                ConsoleUtils.printError("File operation failed: " + exception.getMessage());
            } catch (NoSuchElementException exception) {
                ConsoleUtils.printInfo("No console input received. Saving and closing.");
                try {
                    saveExpenses();
                } catch (IOException saveException) {
                    ConsoleUtils.printError("File operation failed: " + saveException.getMessage());
                }
                running = false;
            }

            if (running) {
                ConsoleUtils.pause(scanner);
            }
        }
    }

    private void printMenu() {
        ConsoleUtils.printLine();
        for (MenuOption option : MenuOption.values()) {
            System.out.printf("%d. %s%n", option.getNumber(), option.getLabel());
        }
        ConsoleUtils.printLine();
    }

    private void addExpense() {
        ConsoleUtils.printSection("Add Expense");
        LocalDate date = InputValidator.readDate(scanner, "Date (yyyy-MM-dd, blank for today): ", true);
        ExpenseCategory category = InputValidator.readCategory(scanner, "Category: ");
        BigDecimal amount = InputValidator.readPositiveAmount(scanner, "Amount: ");
        String note = InputValidator.readOptionalText(scanner, "Note: ", 120);

        Expense expense = expenseService.addExpense(date, category, amount, note);
        ConsoleUtils.printSuccess("Expense added with id #" + expense.getId() + ".");
    }

    private void searchExpenses() {
        ConsoleUtils.printSection("Search Expenses");
        String keyword = InputValidator.readRequiredText(scanner, "Keyword: ", 40);
        listExpenses(expenseService.search(keyword));
    }

    private void filterByMonth() {
        ConsoleUtils.printSection("Filter By Month");
        YearMonth month = InputValidator.readYearMonth(scanner, "Month (yyyy-MM): ");
        listExpenses(expenseService.filterByMonth(month));
    }

    private void showTotals() {
        List<Expense> expenses = expenseService.listAll();
        ConsoleUtils.printSection("Totals");

        if (expenses.isEmpty()) {
            ConsoleUtils.printInfo("No expenses recorded yet.");
            return;
        }

        System.out.printf("Total spent: %s%n", ConsoleUtils.money(analyticsService.totalAmount(expenses)));
        System.out.printf("Expense count: %d%n", expenses.size());
        System.out.printf("Highest expense: %s%n", analyticsService.highestExpense(expenses)
                .map(expense -> "#" + expense.getId() + " " + ConsoleUtils.money(expense.getAmount()) + " - " + expense.getNote())
                .orElse("None"));

        printCategoryTotals(analyticsService.totalByCategory(expenses));
        printMonthlyTotals(analyticsService.totalByMonth(expenses));
    }

    private void deleteExpense() {
        ConsoleUtils.printSection("Delete Expense");
        int id = InputValidator.readPositiveInt(scanner, "Expense id: ");
        Expense expense = expenseService.findById(id);
        System.out.println("Selected: " + expense.toDisplayLine());

        if (InputValidator.readYesNo(scanner, "Delete this expense? (y/n): ")) {
            expenseService.deleteById(id);
            ConsoleUtils.printSuccess("Expense #" + id + " deleted.");
        } else {
            ConsoleUtils.printInfo("Delete cancelled.");
        }
    }

    private void listExpenses(List<Expense> expenses) {
        ConsoleUtils.printSection("Expenses");
        if (expenses.isEmpty()) {
            ConsoleUtils.printInfo("No matching expenses found.");
            return;
        }

        System.out.println(ConsoleUtils.repeat("-", TABLE_WIDTH));
        System.out.printf("%-5s %-12s %-14s %12s  %-55s%n", "ID", "Date", "Category", "Amount", "Note");
        System.out.println(ConsoleUtils.repeat("-", TABLE_WIDTH));
        for (Expense expense : expenses) {
            System.out.printf(
                    "%-5d %-12s %-14s %12s  %-55s%n",
                    expense.getId(),
                    DateUtils.formatDate(expense.getDate()),
                    expense.getCategory().getDisplayName(),
                    ConsoleUtils.money(expense.getAmount()),
                    ConsoleUtils.fit(expense.getNote(), 55)
            );
        }
        System.out.println(ConsoleUtils.repeat("-", TABLE_WIDTH));
        System.out.printf("Rows: %d | Total: %s%n", expenses.size(), ConsoleUtils.money(analyticsService.totalAmount(expenses)));
    }

    private void printCategoryTotals(Map<ExpenseCategory, BigDecimal> totals) {
        ConsoleUtils.printSection("Category Totals");
        for (Map.Entry<ExpenseCategory, BigDecimal> entry : totals.entrySet()) {
            System.out.printf("%-14s %12s%n", entry.getKey().getDisplayName(), ConsoleUtils.money(entry.getValue()));
        }
    }

    private void printMonthlyTotals(Map<YearMonth, BigDecimal> totals) {
        ConsoleUtils.printSection("Monthly Totals");
        for (Map.Entry<YearMonth, BigDecimal> entry : totals.entrySet()) {
            System.out.printf("%-14s %12s%n", entry.getKey(), ConsoleUtils.money(entry.getValue()));
        }
    }

    private void loadExpenses() {
        try {
            expenseService.replaceAll(storage.load());
        } catch (IOException | InvalidExpenseException exception) {
            ConsoleUtils.printError("Could not load saved expenses: " + exception.getMessage());
            ConsoleUtils.printInfo("Starting with an empty journal.");
        }
    }

    private void saveExpenses() throws IOException {
        storage.save(expenseService.listAll());
        ConsoleUtils.printSuccess("Saved " + expenseService.listAll().size() + " expense(s).");
    }
}
