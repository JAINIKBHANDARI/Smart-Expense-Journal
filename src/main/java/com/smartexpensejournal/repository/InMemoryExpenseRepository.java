package com.smartexpensejournal.repository;

import com.smartexpensejournal.model.Expense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryExpenseRepository implements ExpenseRepository {
    private final Map<Integer, Expense> expenseById = new LinkedHashMap<>();
    private int nextId = 1;

    @Override
    public Expense save(Expense expense) {
        expenseById.put(expense.getId(), expense);
        nextId = Math.max(nextId, expense.getId() + 1);
        return expense;
    }

    @Override
    public List<Expense> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(expenseById.values()));
    }

    @Override
    public Optional<Expense> findById(int id) {
        return Optional.ofNullable(expenseById.get(id));
    }

    @Override
    public boolean deleteById(int id) {
        return expenseById.remove(id) != null;
    }

    @Override
    public void replaceAll(Collection<Expense> expenses) {
        expenseById.clear();
        nextId = 1;
        for (Expense expense : expenses) {
            save(expense);
        }
    }

    @Override
    public int nextId() {
        return nextId;
    }
}
