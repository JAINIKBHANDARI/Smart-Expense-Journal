package com.smartexpensejournal.io;

import com.smartexpensejournal.model.Expense;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExpenseFileStorage {
    private final Path filePath;
    private final ExpenseParser parser;

    public ExpenseFileStorage(Path filePath, ExpenseParser parser) {
        this.filePath = filePath;
        this.parser = parser;
    }

    public List<Expense> load() throws IOException {
        ensureFileExists();
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        List<Expense> expenses = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                expenses.add(parser.fromLine(line));
            }
        }

        return expenses;
    }

    public void save(Collection<Expense> expenses) throws IOException {
        ensureFileExists();
        List<String> lines = new ArrayList<>();
        for (Expense expense : expenses) {
            lines.add(parser.toLine(expense));
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    private void ensureFileExists() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
    }
}
