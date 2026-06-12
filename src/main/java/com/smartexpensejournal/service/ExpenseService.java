package com.smartexpensejournal.service;

import com.smartexpensejournal.exception.ExpenseNotFoundException;
import com.smartexpensejournal.exception.InvalidExpenseException;
import com.smartexpensejournal.model.Expense;
import com.smartexpensejournal.model.ExpenseCategory;
import com.smartexpensejournal.repository.ExpenseRepository;
import com.smartexpensejournal.validation.ExpenseValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ExpenseService {
    private final ExpenseRepository repository;
    private final ExpenseValidator validator;

    public ExpenseService(ExpenseRepository repository, ExpenseValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public Expense addExpense(LocalDate date, ExpenseCategory category, BigDecimal amount, String note) {
        Expense expense = new Expense(repository.nextId(), date, category, amount, note);
        validator.validate(expense);
        repository.save(expense);
        return expense;
    }

    public List<Expense> listAll() {
        return sortNewestFirst(repository.findAll());
    }

    public Expense findById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense #" + id + " was not found."));
    }

    public List<Expense> search(String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        if (normalizedKeyword.isEmpty()) {
            return Collections.emptyList();
        }

        List<Expense> matches = new ArrayList<>();
        for (Expense expense : repository.findAll()) {
            if (matchesKeyword(expense, normalizedKeyword)) {
                matches.add(expense);
            }
        }
        return sortNewestFirst(matches);
    }

    public List<Expense> filterByMonth(YearMonth month) {
        List<Expense> matches = new ArrayList<>();
        for (Expense expense : repository.findAll()) {
            if (YearMonth.from(expense.getDate()).equals(month)) {
                matches.add(expense);
            }
        }
        return sortNewestFirst(matches);
    }

    public void deleteById(int id) {
        if (!repository.deleteById(id)) {
            throw new ExpenseNotFoundException("Expense #" + id + " was not found.");
        }
    }

    public void replaceAll(Collection<Expense> expenses) {
        for (Expense expense : expenses) {
            validator.validate(expense);
        }
        repository.replaceAll(expenses);
    }

    private boolean matchesKeyword(Expense expense, String keyword) {
        return String.valueOf(expense.getId()).contains(keyword)
                || expense.getDate().toString().contains(keyword)
                || expense.getCategory().getDisplayName().toLowerCase(Locale.ROOT).contains(keyword)
                || expense.getAmount().toPlainString().contains(keyword)
                || expense.getNote().toLowerCase(Locale.ROOT).contains(keyword);
    }

    private List<Expense> sortNewestFirst(Collection<Expense> expenses) {
        List<Expense> sorted = new ArrayList<>(expenses);
        sorted.sort(Comparator.comparing(Expense::getDate).reversed().thenComparing(Expense::getId));
        return Collections.unmodifiableList(sorted);
    }
}
