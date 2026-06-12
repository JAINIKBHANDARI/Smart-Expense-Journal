package com.smartexpensejournal.gui;

import com.smartexpensejournal.exception.InvalidExpenseException;
import com.smartexpensejournal.io.ExpenseFileStorage;
import com.smartexpensejournal.model.Expense;
import com.smartexpensejournal.model.ExpenseCategory;
import com.smartexpensejournal.service.ExpenseAnalyticsService;
import com.smartexpensejournal.service.ExpenseService;
import com.smartexpensejournal.util.ConsoleUtils;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

public class ExpenseJournalFrame extends JFrame {
    private final ExpenseService expenseService;
    private final ExpenseAnalyticsService analyticsService;
    private final ExpenseFileStorage storage;
    private final ExpenseTableModel tableModel;
    private final TableRowSorter<ExpenseTableModel> sorter;

    private final JTextField dateField = new JTextField(12);
    private final JComboBox<ExpenseCategory> categoryBox = new JComboBox<>(ExpenseCategory.values());
    private final JTextField amountField = new JTextField(12);
    private final JTextField noteField = new JTextField(20);
    private final JTextField searchField = new JTextField(18);
    private final JTextField monthField = new JTextField(9);

    private final JLabel totalLabel = statLabel("INR 0.00");
    private final JLabel countLabel = statLabel("0");
    private final JLabel highestLabel = statLabel("None");
    private final JLabel statusLabel = new JLabel("Ready");

    public ExpenseJournalFrame(
            ExpenseService expenseService,
            ExpenseAnalyticsService analyticsService,
            ExpenseFileStorage storage
    ) {
        super("Smart Expense Journal");
        this.expenseService = expenseService;
        this.analyticsService = analyticsService;
        this.storage = storage;
        this.tableModel = new ExpenseTableModel();
        this.sorter = new TableRowSorter<>(tableModel);

        GuiTheme.install();
        loadSavedExpenses();
        buildUi();
        refreshTable();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 720));
        setLocationByPlatform(true);

        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(GuiTheme.PANEL);
        GuiTheme.padded(root, 18, 18, 18, 18);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        pack();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 12));
        header.setBackground(GuiTheme.BLACK);
        header.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));

        JLabel title = new JLabel("Smart Expense Journal");
        title.setForeground(GuiTheme.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JLabel subtitle = new JLabel("Track, search, filter, and understand your spending in one clean workspace.");
        subtitle.setForeground(new java.awt.Color(220, 220, 220));
        subtitle.setFont(subtitle.getFont().deriveFont(13f));

        JPanel titlePanel = new JPanel(new BorderLayout(0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton saveButton = new JButton("Save");
        JButton reloadButton = new JButton("Reload");
        GuiTheme.stylePrimaryButton(saveButton);
        GuiTheme.styleSecondaryButton(reloadButton);
        saveButton.addActionListener(event -> saveExpenses());
        reloadButton.addActionListener(event -> {
            loadSavedExpenses();
            refreshTable();
        });
        actions.add(reloadButton);
        actions.add(saveButton);

        header.add(titlePanel, BorderLayout.CENTER);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(18, 18));
        body.setOpaque(false);
        body.add(buildLeftPanel(), BorderLayout.WEST);
        body.add(buildTablePanel(), BorderLayout.CENTER);
        return body;
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(330, 100));
        panel.add(buildFormPanel(), BorderLayout.NORTH);
        panel.add(buildStatsPanel(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFormPanel() {
        JPanel panel = cardPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(6, 0, 6, 0);
        constraints.gridx = 0;
        constraints.weightx = 1;

        JLabel title = sectionTitle("Add Expense");
        addFull(panel, title, constraints, 0);

        dateField.setText(LocalDate.now().toString());
        GuiTheme.styleField(dateField);
        GuiTheme.styleField(amountField);
        GuiTheme.styleField(noteField);

        addField(panel, "Date (yyyy-MM-dd)", dateField, constraints, 1);
        addField(panel, "Category", categoryBox, constraints, 3);
        addField(panel, "Amount", amountField, constraints, 5);
        addField(panel, "Note", noteField, constraints, 7);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);
        JButton addButton = new JButton("Add Expense");
        JButton clearButton = new JButton("Clear");
        GuiTheme.stylePrimaryButton(addButton);
        GuiTheme.styleSecondaryButton(clearButton);
        addButton.addActionListener(event -> addExpense());
        clearButton.addActionListener(event -> clearForm());
        buttons.add(addButton);
        buttons.add(clearButton);
        addFull(panel, buttons, constraints, 9);

        return panel;
    }

    private JPanel buildStatsPanel() {
        JPanel panel = cardPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.insets = new Insets(6, 0, 10, 0);

        addFull(panel, sectionTitle("Snapshot"), constraints, 0);
        addStat(panel, "Total Spent", totalLabel, constraints, 1);
        addStat(panel, "Expenses", countLabel, constraints, 2);
        addStat(panel, "Highest", highestLabel, constraints, 3);

        JTextArea hint = new JTextArea("Tip: use Search for notes/categories and Month for yyyy-MM filtering.");
        hint.setLineWrap(true);
        hint.setWrapStyleWord(true);
        hint.setEditable(false);
        hint.setFocusable(false);
        hint.setBackground(GuiTheme.WHITE);
        hint.setForeground(GuiTheme.MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        addFull(panel, hint, constraints, 4);

        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = cardPanel(new BorderLayout(0, 14));
        panel.add(buildFilters(), BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        GuiTheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(420);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(GuiTheme.BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buildTableActions(table), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFilters() {
        JPanel filters = new JPanel(new BorderLayout(14, 0));
        filters.setOpaque(false);

        JLabel title = sectionTitle("Expenses");
        filters.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);
        GuiTheme.styleField(searchField);
        GuiTheme.styleField(monthField);
        searchField.setToolTipText("Search id, date, category, amount, or note");
        monthField.setToolTipText("Filter month as yyyy-MM");

        JButton clearButton = new JButton("Clear Filters");
        GuiTheme.styleSecondaryButton(clearButton);
        clearButton.addActionListener(event -> {
            searchField.setText("");
            monthField.setText("");
            applyFilters();
        });

        controls.add(GuiTheme.label("Search"));
        controls.add(searchField);
        controls.add(GuiTheme.label("Month"));
        controls.add(monthField);
        controls.add(clearButton);
        filters.add(controls, BorderLayout.EAST);

        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                applyFilters();
            }
        };
        searchField.getDocument().addDocumentListener(listener);
        monthField.getDocument().addDocumentListener(listener);
        return filters;
    }

    private JPanel buildTableActions(JTable table) {
        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);

        JButton deleteButton = new JButton("Delete Selected");
        GuiTheme.styleDangerButton(deleteButton);
        deleteButton.addActionListener(event -> deleteSelectedExpense(table));

        actions.add(deleteButton, BorderLayout.WEST);
        return actions;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        statusLabel.setForeground(GuiTheme.MUTED);
        footer.add(statusLabel, BorderLayout.WEST);
        return footer;
    }

    private void addExpense() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            ExpenseCategory category = (ExpenseCategory) categoryBox.getSelectedItem();
            BigDecimal amount = new BigDecimal(amountField.getText().trim().replace(",", ""));
            String note = noteField.getText().trim();

            Expense expense = expenseService.addExpense(date, category, amount, note);
            saveExpensesQuietly();
            clearForm();
            refreshTable();
            setStatus("Added expense #" + expense.getId() + ".", false);
        } catch (DateTimeParseException exception) {
            showError("Use date format yyyy-MM-dd.");
        } catch (NumberFormatException exception) {
            showError("Enter a valid amount, for example 249.50.");
        } catch (InvalidExpenseException exception) {
            showError(exception.getMessage());
        }
    }

    private void deleteSelectedExpense(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Select an expense row first.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        Expense expense = tableModel.getExpenseAt(modelRow);
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Delete expense #" + expense.getId() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (answer == JOptionPane.YES_OPTION) {
            expenseService.deleteById(expense.getId());
            saveExpensesQuietly();
            refreshTable();
            setStatus("Deleted expense #" + expense.getId() + ".", false);
        }
    }

    private void applyFilters() {
        String keyword = searchField.getText().trim();
        String month = monthField.getText().trim();

        sorter.setRowFilter(new RowFilter<ExpenseTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends ExpenseTableModel, ? extends Integer> entry) {
                Expense expense = tableModel.getExpenseAt(entry.getIdentifier());
                return matchesKeyword(expense, keyword) && matchesMonth(expense, month);
            }
        });
    }

    private boolean matchesKeyword(Expense expense, String keyword) {
        if (keyword.isEmpty()) {
            return true;
        }
        String normalized = keyword.toLowerCase();
        String searchable = (expense.getId() + " "
                + expense.getDate() + " "
                + expense.getCategory().getDisplayName() + " "
                + expense.getAmount() + " "
                + expense.getNote()).toLowerCase();
        return searchable.contains(normalized);
    }

    private boolean matchesMonth(Expense expense, String month) {
        if (month.isEmpty()) {
            return true;
        }
        if (!Pattern.matches("\\d{4}-\\d{2}", month)) {
            return true;
        }
        return YearMonth.from(expense.getDate()).equals(YearMonth.parse(month));
    }

    private void refreshTable() {
        List<Expense> expenses = expenseService.listAll();
        tableModel.setExpenses(expenses);
        applyFilters();
        refreshStats(expenses);
    }

    private void refreshStats(List<Expense> expenses) {
        totalLabel.setText(ConsoleUtils.money(analyticsService.totalAmount(expenses)));
        countLabel.setText(String.valueOf(expenses.size()));
        highestLabel.setText(analyticsService.highestExpense(expenses)
                .map(expense -> ConsoleUtils.money(expense.getAmount()))
                .orElse("None"));
    }

    private void loadSavedExpenses() {
        try {
            expenseService.replaceAll(storage.load());
            setStatus("Loaded saved expenses.", false);
        } catch (IOException | InvalidExpenseException exception) {
            setStatus("Could not load saved expenses: " + exception.getMessage(), true);
        }
    }

    private void saveExpenses() {
        try {
            storage.save(expenseService.listAll());
            setStatus("Saved " + expenseService.listAll().size() + " expense(s).", false);
        } catch (IOException exception) {
            showError("Save failed: " + exception.getMessage());
        }
    }

    private void saveExpensesQuietly() {
        try {
            storage.save(expenseService.listAll());
        } catch (IOException exception) {
            setStatus("Auto-save failed: " + exception.getMessage(), true);
        }
    }

    private void clearForm() {
        dateField.setText(LocalDate.now().toString());
        categoryBox.setSelectedItem(ExpenseCategory.FOOD);
        amountField.setText("");
        noteField.setText("");
        amountField.requestFocusInWindow();
    }

    private void showError(String message) {
        setStatus(message, true);
        JOptionPane.showMessageDialog(this, message, "Smart Expense Journal", JOptionPane.ERROR_MESSAGE);
    }

    private void setStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.setForeground(error ? GuiTheme.ERROR : GuiTheme.MUTED);
    }

    private JPanel cardPanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(GuiTheme.WHITE);
        panel.setBorder(GuiTheme.cardBorder());
        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(GuiTheme.BLACK);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
        return label;
    }

    private JLabel statLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(GuiTheme.BLACK);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        return label;
    }

    private void addField(JPanel panel, String label, java.awt.Component field, GridBagConstraints constraints, int row) {
        constraints.gridy = row;
        JLabel labelComponent = GuiTheme.label(label);
        panel.add(labelComponent, constraints);
        constraints.gridy = row + 1;
        panel.add(field, constraints);
    }

    private void addStat(JPanel panel, String label, JLabel value, GridBagConstraints constraints, int row) {
        JPanel stat = new JPanel(new BorderLayout());
        stat.setBackground(GuiTheme.WHITE);
        stat.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GuiTheme.BORDER));
        JLabel name = new JLabel(label);
        name.setForeground(GuiTheme.MUTED);
        stat.add(name, BorderLayout.WEST);
        stat.add(value, BorderLayout.EAST);
        addFull(panel, stat, constraints, row);
    }

    private void addFull(JPanel panel, java.awt.Component component, GridBagConstraints constraints, int row) {
        constraints.gridy = row;
        panel.add(component, constraints);
    }
}
