package com.smartexpensejournal;

import com.smartexpensejournal.app.ExpenseJournalApp;
import com.smartexpensejournal.gui.ExpenseJournalFrame;
import com.smartexpensejournal.io.ExpenseFileStorage;
import com.smartexpensejournal.io.ExpenseParser;
import com.smartexpensejournal.repository.ExpenseRepository;
import com.smartexpensejournal.repository.InMemoryExpenseRepository;
import com.smartexpensejournal.service.ExpenseAnalyticsService;
import com.smartexpensejournal.service.ExpenseService;
import com.smartexpensejournal.validation.ExpenseValidator;

import javax.swing.SwingUtilities;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        ExpenseRepository repository = new InMemoryExpenseRepository();
        ExpenseValidator validator = new ExpenseValidator();
        ExpenseService expenseService = new ExpenseService(repository, validator);
        ExpenseAnalyticsService analyticsService = new ExpenseAnalyticsService();
        ExpenseFileStorage storage = new ExpenseFileStorage(
                Paths.get("src", "main", "resources", "data", "expenses.jsonl"),
                new ExpenseParser()
        );

        if (args.length > 0 && "console".equalsIgnoreCase(args[0])) {
            new ExpenseJournalApp(expenseService, analyticsService, storage).run();
            return;
        }

        if (args.length > 0 && "--gui-check".equalsIgnoreCase(args[0])) {
            ExpenseJournalFrame frame = new ExpenseJournalFrame(expenseService, analyticsService, storage);
            frame.dispose();
            System.out.println("GUI check passed.");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            ExpenseJournalFrame frame = new ExpenseJournalFrame(expenseService, analyticsService, storage);
            frame.setVisible(true);
        });
    }
}
