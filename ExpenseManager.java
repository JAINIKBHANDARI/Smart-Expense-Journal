import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExpenseManager {
    private final ArrayList<Expense> expenses = new ArrayList<>();
    private final HashMap<Integer, Expense> expenseById = new HashMap<>();
    private final HashMap<YearMonth, BigDecimal> monthlyBudgets = new HashMap<>();
    private int nextId = 1;
    private boolean sortedCacheDirty = true;
    private final ArrayList<Expense> sortedByDate = new ArrayList<>();

    public void loadExpenses(List<Expense> loadedExpenses) {
        expenses.clear();
        expenseById.clear();
        nextId = 1;

        for (Expense expense : loadedExpenses) {
            expenses.add(expense);
            expenseById.put(expense.getId(), expense);
            nextId = Math.max(nextId, expense.getId() + 1);
        }
        sortedCacheDirty = true;
    }

    public void loadBudgets(Map<YearMonth, BigDecimal> loadedBudgets) {
        monthlyBudgets.clear();
        monthlyBudgets.putAll(loadedBudgets);
    }

    public Expense addExpense(java.time.LocalDate date, String category, BigDecimal amount, String note) {
        Expense expense = new Expense(nextId++, date, category, amount, note);
        expenses.add(expense);
        expenseById.put(expense.getId(), expense);
        sortedCacheDirty = true;
        return expense;
    }

    public boolean updateExpense(Expense updatedExpense) {
        Expense oldExpense = expenseById.get(updatedExpense.getId());
        if (oldExpense == null) {
            return false;
        }

        oldExpense.setDate(updatedExpense.getDate());
        oldExpense.setCategory(updatedExpense.getCategory());
        oldExpense.setAmount(updatedExpense.getAmount());
        oldExpense.setNote(updatedExpense.getNote());
        sortedCacheDirty = true;
        return true;
    }

    public boolean deleteExpense(int id) {
        Expense expense = expenseById.remove(id);
        if (expense == null) {
            return false;
        }
        expenses.remove(expense);
        sortedCacheDirty = true;
        return true;
    }

    public Expense findById(int id) {
        return expenseById.get(id);
    }

    public List<Expense> getAllExpenses() {
        return new ArrayList<>(getSortedByDate());
    }

    public List<Expense> searchNotes(String keyword) {
        String search = keyword.toLowerCase();
        ArrayList<Expense> result = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getNote().toLowerCase().contains(search)) {
                result.add(expense);
            }
        }
        return sortCopy(result);
    }

    public List<Expense> filterByCategory(String category) {
        String search = category.trim().toLowerCase();
        ArrayList<Expense> result = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getCategory().toLowerCase().equals(search)) {
                result.add(expense);
            }
        }
        return sortCopy(result);
    }

    public List<Expense> filterByMonth(int year, int month) {
        ArrayList<Expense> result = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getDate().getYear() == year && expense.getDate().getMonthValue() == month) {
                result.add(expense);
            }
        }
        return sortCopy(result);
    }

    public BigDecimal getTotalSpending() {
        BigDecimal total = BigDecimal.ZERO;
        for (Expense expense : expenses) {
            total = total.add(expense.getAmount());
        }
        return total;
    }

    public Map<String, BigDecimal> getCategoryWiseSummary() {
        TreeMap<String, BigDecimal> summary = new TreeMap<>();
        for (Expense expense : expenses) {
            String category = expense.getCategory();
            summary.put(category, summary.getOrDefault(category, BigDecimal.ZERO).add(expense.getAmount()));
        }
        return summary;
    }

    public Map<String, BigDecimal> getMonthlySummary() {
        LinkedHashMap<String, BigDecimal> summary = new LinkedHashMap<>();
        TreeMap<YearMonth, BigDecimal> sorted = new TreeMap<>();
        for (Expense expense : expenses) {
            YearMonth month = YearMonth.from(expense.getDate());
            sorted.put(month, sorted.getOrDefault(month, BigDecimal.ZERO).add(expense.getAmount()));
        }
        for (Map.Entry<YearMonth, BigDecimal> entry : sorted.entrySet()) {
            summary.put(entry.getKey().toString(), entry.getValue());
        }
        return summary;
    }

    public Expense getHighestExpense() {
        Expense highest = null;
        for (Expense expense : expenses) {
            if (highest == null || expense.getAmount().compareTo(highest.getAmount()) > 0) {
                highest = expense;
            }
        }
        return highest;
    }

    public List<Expense> getRecentExpenses(int count) {
        List<Expense> sorted = getSortedByDate();
        int end = Math.min(count, sorted.size());
        return new ArrayList<>(sorted.subList(0, end));
    }

    public void setMonthlyBudget(int year, int month, BigDecimal amount) {
        monthlyBudgets.put(YearMonth.of(year, month), amount);
    }

    public BigDecimal getMonthlyBudget(int year, int month) {
        return monthlyBudgets.get(YearMonth.of(year, month));
    }

    public BigDecimal getMonthlySpending(int year, int month) {
        BigDecimal total = BigDecimal.ZERO;
        for (Expense expense : expenses) {
            if (expense.getDate().getYear() == year && expense.getDate().getMonthValue() == month) {
                total = total.add(expense.getAmount());
            }
        }
        return total;
    }

    public Map<YearMonth, BigDecimal> getMonthlyBudgets() {
        return new TreeMap<>(monthlyBudgets);
    }

    private List<Expense> getSortedByDate() {
        if (sortedCacheDirty) {
            sortedByDate.clear();
            sortedByDate.addAll(expenses);
            sortedByDate.sort(expenseComparator());
            sortedCacheDirty = false;
        }
        return Collections.unmodifiableList(sortedByDate);
    }

    private List<Expense> sortCopy(List<Expense> source) {
        source.sort(expenseComparator());
        return source;
    }

    private Comparator<Expense> expenseComparator() {
        return Comparator.comparing(Expense::getDate).reversed()
                .thenComparing(Expense::getId, Comparator.reverseOrder());
    }
}
