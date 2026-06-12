package com.smartexpensejournal.app;

public enum MenuOption {
    ADD_EXPENSE(1, "Add Expense"),
    LIST_ALL(2, "List All Expenses"),
    SEARCH(3, "Search Expenses"),
    FILTER_BY_MONTH(4, "Filter By Month"),
    SHOW_TOTALS(5, "Show Totals"),
    DELETE_BY_ID(6, "Delete By Id"),
    SAVE(7, "Save Now"),
    EXIT(8, "Save And Exit");

    private final int number;
    private final String label;

    MenuOption(int number, String label) {
        this.number = number;
        this.label = label;
    }

    public int getNumber() {
        return number;
    }

    public String getLabel() {
        return label;
    }

    public static MenuOption fromNumber(int number) {
        for (MenuOption option : values()) {
            if (option.number == number) {
                return option;
            }
        }
        throw new IllegalArgumentException("Invalid menu option: " + number);
    }
}
