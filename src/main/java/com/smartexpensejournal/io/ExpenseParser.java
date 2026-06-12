package com.smartexpensejournal.io;

import com.smartexpensejournal.exception.InvalidExpenseException;
import com.smartexpensejournal.model.Expense;
import com.smartexpensejournal.model.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExpenseParser {
    public String toLine(Expense expense) {
        return "{"
                + "\"id\":" + expense.getId() + ","
                + "\"date\":\"" + escape(expense.getDate().toString()) + "\","
                + "\"category\":\"" + escape(expense.getCategory().name()) + "\","
                + "\"amount\":\"" + escape(expense.getAmount().toPlainString()) + "\","
                + "\"note\":\"" + escape(expense.getNote()) + "\""
                + "}";
    }

    public Expense fromLine(String line) {
        try {
            Map<String, String> values = parseObject(line);
            return new Expense(
                    Integer.parseInt(required(values, "id")),
                    LocalDate.parse(required(values, "date")),
                    ExpenseCategory.fromInput(required(values, "category")),
                    new BigDecimal(required(values, "amount")),
                    values.getOrDefault("note", "")
            );
        } catch (RuntimeException exception) {
            throw new InvalidExpenseException("Invalid saved expense line: " + line);
        }
    }

    private Map<String, String> parseObject(String line) {
        String trimmed = line.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new IllegalArgumentException("Line is not an object.");
        }

        Map<String, String> values = new LinkedHashMap<>();
        String body = trimmed.substring(1, trimmed.length() - 1);
        int index = 0;

        while (index < body.length()) {
            index = skipWhitespaceAndComma(body, index);
            if (index >= body.length()) {
                break;
            }

            ParsedToken key = readQuoted(body, index);
            index = skipWhitespace(body, key.nextIndex);
            if (index >= body.length() || body.charAt(index) != ':') {
                throw new IllegalArgumentException("Missing colon.");
            }
            index++;
            index = skipWhitespace(body, index);

            ParsedToken value;
            if (index < body.length() && body.charAt(index) == '"') {
                value = readQuoted(body, index);
            } else {
                value = readUnquoted(body, index);
            }

            values.put(key.value, value.value);
            index = value.nextIndex;
        }

        return values;
    }

    private ParsedToken readQuoted(String text, int startIndex) {
        if (text.charAt(startIndex) != '"') {
            throw new IllegalArgumentException("Expected quoted text.");
        }

        StringBuilder builder = new StringBuilder();
        boolean escaping = false;
        int index = startIndex + 1;

        while (index < text.length()) {
            char current = text.charAt(index);
            if (escaping) {
                builder.append(unescapeChar(current));
                escaping = false;
            } else if (current == '\\') {
                escaping = true;
            } else if (current == '"') {
                return new ParsedToken(builder.toString(), index + 1);
            } else {
                builder.append(current);
            }
            index++;
        }

        throw new IllegalArgumentException("Unclosed quoted text.");
    }

    private ParsedToken readUnquoted(String text, int startIndex) {
        int index = startIndex;
        while (index < text.length() && text.charAt(index) != ',') {
            index++;
        }
        return new ParsedToken(text.substring(startIndex, index).trim(), index);
    }

    private int skipWhitespaceAndComma(String text, int index) {
        while (index < text.length() && (Character.isWhitespace(text.charAt(index)) || text.charAt(index) == ',')) {
            index++;
        }
        return index;
    }

    private int skipWhitespace(String text, int index) {
        while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
            index++;
        }
        return index;
    }

    private String required(Map<String, String> values, String key) {
        String value = values.get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing " + key + ".");
        }
        return value;
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private char unescapeChar(char value) {
        switch (value) {
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            default:
                return value;
        }
    }

    private static final class ParsedToken {
        private final String value;
        private final int nextIndex;

        private ParsedToken(String value, int nextIndex) {
            this.value = value;
            this.nextIndex = nextIndex;
        }
    }
}
