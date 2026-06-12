package com.smartexpensejournal.model;

import com.smartexpensejournal.util.ConsoleUtils;
import com.smartexpensejournal.util.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class Expense {
    private final int id;
    private final LocalDate date;
    private final ExpenseCategory category;
    private final BigDecimal amount;
    private final String note;

    public Expense(int id, LocalDate date, ExpenseCategory category, BigDecimal amount, String note) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.note = note == null ? "" : note.trim();
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String toDisplayLine() {
        return "#" + id + " | " + DateUtils.formatDate(date) + " | " + category.getDisplayName()
                + " | " + ConsoleUtils.money(amount) + " | " + note;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Expense)) {
            return false;
        }
        Expense expense = (Expense) object;
        return id == expense.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return toDisplayLine();
    }
}
