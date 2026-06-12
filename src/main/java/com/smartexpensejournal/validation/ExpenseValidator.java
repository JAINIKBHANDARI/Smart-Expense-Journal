package com.smartexpensejournal.validation;

import com.smartexpensejournal.exception.InvalidExpenseException;
import com.smartexpensejournal.model.Expense;

import java.math.BigDecimal;

public class ExpenseValidator {
    private static final int MAX_NOTE_LENGTH = 120;
    private static final BigDecimal MAX_REASONABLE_AMOUNT = new BigDecimal("100000000");

    public void validate(Expense expense) {
        if (expense == null) {
            throw new InvalidExpenseException("Expense cannot be null.");
        }
        if (expense.getId() <= 0) {
            throw new InvalidExpenseException("Expense id must be positive.");
        }
        if (expense.getDate() == null) {
            throw new InvalidExpenseException("Expense date is required.");
        }
        if (expense.getCategory() == null) {
            throw new InvalidExpenseException("Expense category is required.");
        }
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidExpenseException("Expense amount must be greater than zero.");
        }
        if (expense.getAmount().compareTo(MAX_REASONABLE_AMOUNT) > 0) {
            throw new InvalidExpenseException("Expense amount is too large.");
        }
        if (expense.getNote().length() > MAX_NOTE_LENGTH) {
            throw new InvalidExpenseException("Expense note must be " + MAX_NOTE_LENGTH + " characters or fewer.");
        }
    }
}
