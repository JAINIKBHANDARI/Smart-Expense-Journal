package com.smartexpensejournal.service;

import com.smartexpensejournal.model.Expense;
import com.smartexpensejournal.model.ExpenseCategory;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class ExpenseAnalyticsService {
    public BigDecimal totalAmount(Collection<Expense> expenses) {
        BigDecimal total = BigDecimal.ZERO;
        for (Expense expense : expenses) {
            total = total.add(expense.getAmount());
        }
        return total;
    }

    public Map<ExpenseCategory, BigDecimal> totalByCategory(Collection<Expense> expenses) {
        Map<ExpenseCategory, BigDecimal> totals = new LinkedHashMap<>();
        for (ExpenseCategory category : ExpenseCategory.values()) {
            totals.put(category, BigDecimal.ZERO);
        }

        for (Expense expense : expenses) {
            totals.put(expense.getCategory(), totals.get(expense.getCategory()).add(expense.getAmount()));
        }

        totals.entrySet().removeIf(entry -> entry.getValue().compareTo(BigDecimal.ZERO) == 0);
        return totals;
    }

    public Map<YearMonth, BigDecimal> totalByMonth(Collection<Expense> expenses) {
        Map<YearMonth, BigDecimal> chronologicalTotals = new TreeMap<>();
        for (Expense expense : expenses) {
            YearMonth month = YearMonth.from(expense.getDate());
            chronologicalTotals.put(month, chronologicalTotals.getOrDefault(month, BigDecimal.ZERO).add(expense.getAmount()));
        }
        return chronologicalTotals;
    }

    public Optional<Expense> highestExpense(Collection<Expense> expenses) {
        return expenses.stream().max(Comparator.comparing(Expense::getAmount));
    }
}
