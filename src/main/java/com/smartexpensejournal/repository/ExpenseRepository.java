package com.smartexpensejournal.repository;

import com.smartexpensejournal.model.Expense;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository {
    Expense save(Expense expense);

    List<Expense> findAll();

    Optional<Expense> findById(int id);

    boolean deleteById(int id);

    void replaceAll(Collection<Expense> expenses);

    int nextId();
}
