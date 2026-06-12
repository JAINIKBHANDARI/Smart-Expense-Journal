package com.smartexpensejournal.model;

import java.util.Locale;

public enum ExpenseCategory {
    FOOD("Food"),
    TRANSPORT("Transport"),
    RENT("Rent"),
    UTILITIES("Utilities"),
    SHOPPING("Shopping"),
    HEALTH("Health"),
    EDUCATION("Education"),
    ENTERTAINMENT("Entertainment"),
    TRAVEL("Travel"),
    OTHER("Other");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ExpenseCategory fromInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return OTHER;
        }

        String normalized = input.trim().replace('-', '_').replace(' ', '_').toUpperCase(Locale.ROOT);
        for (ExpenseCategory category : values()) {
            if (category.name().equals(normalized)
                    || category.displayName.toUpperCase(Locale.ROOT).equals(normalized)) {
                return category;
            }
        }
        return OTHER;
    }

    public static String availableOptions() {
        StringBuilder builder = new StringBuilder();
        ExpenseCategory[] categories = values();
        for (int index = 0; index < categories.length; index++) {
            if (index > 0) {
                builder.append(", ");
            }
            builder.append(categories[index].getDisplayName());
        }
        return builder.toString();
    }
}
