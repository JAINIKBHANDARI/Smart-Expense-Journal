package com.smartexpensejournal.gui;

import com.smartexpensejournal.model.Expense;
import com.smartexpensejournal.util.ConsoleUtils;
import com.smartexpensejournal.util.DateUtils;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ExpenseTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"ID", "Date", "Category", "Amount", "Note"};

    private final List<Expense> expenses = new ArrayList<>();

    @Override
    public int getRowCount() {
        return expenses.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Expense expense = expenses.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return expense.getId();
            case 1:
                return DateUtils.formatDate(expense.getDate());
            case 2:
                return expense.getCategory().getDisplayName();
            case 3:
                return ConsoleUtils.money(expense.getAmount());
            case 4:
                return expense.getNote();
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Integer.class;
        }
        return String.class;
    }

    public void setExpenses(List<Expense> newExpenses) {
        expenses.clear();
        expenses.addAll(newExpenses);
        fireTableDataChanged();
    }

    public Expense getExpenseAt(int rowIndex) {
        return expenses.get(rowIndex);
    }
}
