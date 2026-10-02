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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class ExpenseJournalUI extends JFrame {
    private static final Color BLACK = new Color(18, 18, 18);
    private static final Color DARK = new Color(34, 34, 34);
    private static final Color WHITE = Color.WHITE;
    private static final Color BACKGROUND = new Color(244, 244, 244);
    private static final Color BORDER = new Color(215, 215, 215);
    private static final Color MUTED = new Color(95, 95, 95);
    private static final Color ERROR = new Color(170, 45, 45);

    private final ExpenseManager manager = new ExpenseManager();
    private final FileHandler fileHandler = new FileHandler();

    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 14);
    private final JTextField categoryField = new JTextField(14);
    private final JTextField amountField = new JTextField(14);
    private final JTextField noteField = new JTextField(18);

    private final JTextField searchField = new JTextField(14);
    private final JTextField filterCategoryField = new JTextField(12);
    private final JComboBox<String> monthBox = new JComboBox<>(monthOptions());
    private final JTextField yearField = new JTextField(String.valueOf(LocalDate.now().getYear()), 6);

    private final JTextField budgetAmountField = new JTextField(10);
    private final JLabel totalLabel = valueLabel("INR 0.00");
    private final JLabel countLabel = valueLabel("0");
    private final JLabel highestLabel = valueLabel("None");
    private final JLabel statusLabel = new JLabel("Ready");
    private final JTextArea summaryArea = new JTextArea();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Date", "Category", "Amount", "Note"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable expenseTable = new JTable(tableModel);

    public ExpenseJournalUI() {
        super("Smart Expense Journal");
        loadData();
        buildWindow();
        refreshAll(manager.getAllExpenses());
    }

    private void buildWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 720));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildMainArea(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        pack();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 8));
        header.setBackground(BLACK);
        header.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        JLabel title = new JLabel("Smart Expense Journal");
        title.setForeground(WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JLabel subtitle = new JLabel("Add, edit, filter, summarize, budget, and export expenses.");
        subtitle.setForeground(new Color(220, 220, 220));
        subtitle.setFont(subtitle.getFont().deriveFont(13f));

        JPanel titlePanel = new JPanel(new BorderLayout(0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        titlePanel.add(subtitle, BorderLayout.CENTER);

        JButton saveButton = button("Save", true);
        JButton reloadButton = button("Reload", false);
        saveButton.addActionListener(event -> {
            saveData();
            setStatus("Saved successfully.", false);
        });
        reloadButton.addActionListener(event -> {
            loadData();
            refreshAll(manager.getAllExpenses());
            setStatus("Reloaded data from file.", false);
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(reloadButton);
        actions.add(saveButton);

        header.add(titlePanel, BorderLayout.CENTER);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout(16, 16));
        main.setOpaque(false);
        main.add(buildFormPanel(), BorderLayout.WEST);
        main.add(buildTablePanel(), BorderLayout.CENTER);
        main.add(buildSummaryPanel(), BorderLayout.EAST);
        return main;
    }

    private JPanel buildFormPanel() {
        JPanel panel = card(new GridBagLayout());
        panel.setPreferredSize(new Dimension(300, 500));
        GridBagConstraints c = constraints();

        addFull(panel, sectionTitle("Expense Details"), c, 0);
        addField(panel, "Date", dateField, c, 1);
        addField(panel, "Category", categoryField, c, 3);
        addField(panel, "Amount", amountField, c, 5);
        addField(panel, "Note", noteField, c, 7);

        JButton addButton = button("Add", true);
        JButton editButton = button("Edit Selected", false);
        JButton clearButton = button("Clear", false);
        addButton.addActionListener(event -> addExpense());
        editButton.addActionListener(event -> editSelectedExpense());
        clearButton.addActionListener(event -> clearForm());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(addButton);
        actions.add(editButton);
        actions.add(clearButton);
        addFull(panel, actions, c, 9);

        JLabel help = new JLabel("<html><body style='width:230px'>Select a row from the table to edit or delete it.</body></html>");
        help.setForeground(MUTED);
        addFull(panel, help, c, 10);

        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = card(new BorderLayout(0, 12));
        panel.add(buildFilters(), BorderLayout.NORTH);

        expenseTable.setRowHeight(34);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expenseTable.setFillsViewportHeight(true);
        expenseTable.setBackground(WHITE);
        expenseTable.setForeground(BLACK);
        expenseTable.setGridColor(new Color(235, 235, 235));
        expenseTable.setSelectionBackground(DARK);
        expenseTable.setSelectionForeground(WHITE);
        expenseTable.getTableHeader().setBackground(BLACK);
        expenseTable.getTableHeader().setForeground(WHITE);
        expenseTable.getTableHeader().setFont(expenseTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        expenseTable.getTableHeader().setReorderingAllowed(false);
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        expenseTable.getColumnModel().getColumn(1).setPreferredWidth(95);
        expenseTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        expenseTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        expenseTable.getColumnModel().getColumn(4).setPreferredWidth(300);
        expenseTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        JButton deleteButton = button("Delete Selected", false);
        JButton recentButton = button("Recent 5", false);
        JButton exportButton = button("Export Report", true);
        deleteButton.setForeground(ERROR);
        deleteButton.addActionListener(event -> deleteSelectedExpense());
        recentButton.addActionListener(event -> refreshAll(manager.getRecentExpenses(5)));
        exportButton.addActionListener(event -> exportReport());
        actions.add(deleteButton);
        actions.add(recentButton);
        actions.add(exportButton);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildFilters() {
        JPanel filters = new JPanel(new BorderLayout(10, 10));
        filters.setOpaque(false);
        filters.add(sectionTitle("Expenses"), BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        styleField(searchField);
        styleField(filterCategoryField);
        styleField(yearField);

        JButton searchButton = button("Search Note", false);
        JButton categoryButton = button("Category", false);
        JButton monthButton = button("Month", false);
        JButton allButton = button("All", true);

        searchButton.addActionListener(event -> refreshAll(manager.searchNotes(searchField.getText().trim())));
        categoryButton.addActionListener(event -> refreshAll(manager.filterByCategory(filterCategoryField.getText().trim())));
        monthButton.addActionListener(event -> filterByMonth());
        allButton.addActionListener(event -> {
            searchField.setText("");
            filterCategoryField.setText("");
            refreshAll(manager.getAllExpenses());
        });

        controls.add(label("Note"));
        controls.add(searchField);
        controls.add(searchButton);
        controls.add(label("Category"));
        controls.add(filterCategoryField);
        controls.add(categoryButton);
        controls.add(monthBox);
        controls.add(yearField);
        controls.add(monthButton);
        controls.add(allButton);
        filters.add(controls, BorderLayout.CENTER);
        return filters;
    }

    private JPanel buildSummaryPanel() {
        JPanel panel = card(new BorderLayout(0, 12));
        panel.setPreferredSize(new Dimension(300, 500));

        JPanel stats = new JPanel(new GridBagLayout());
        stats.setOpaque(false);
        GridBagConstraints c = constraints();
        addFull(stats, sectionTitle("Snapshot"), c, 0);
        addStat(stats, "Total", totalLabel, c, 1);
        addStat(stats, "Records", countLabel, c, 2);
        addStat(stats, "Highest", highestLabel, c, 3);

        JPanel budget = new JPanel(new GridBagLayout());
        budget.setOpaque(false);
        GridBagConstraints b = constraints();
        addFull(budget, sectionTitle("Budget"), b, 0);
        addFull(budget, label("Month and year"), b, 1);
        JPanel monthYear = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        monthYear.setOpaque(false);
        JLabel budgetMonthLabel = new JLabel(monthBox.getSelectedItem() != null ? monthBox.getSelectedItem().toString() : "");
        budgetMonthLabel.setForeground(MUTED);
        JLabel budgetYearLabel = new JLabel(yearField.getText().trim());
        budgetYearLabel.setForeground(MUTED);
        monthBox.addActionListener(event -> {
            Object selected = monthBox.getSelectedItem();
            if (selected != null) {
                budgetMonthLabel.setText(selected.toString());
            }
        });
        yearField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                budgetYearLabel.setText(yearField.getText().trim());
            }
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });
        monthYear.add(budgetMonthLabel);
        monthYear.add(budgetYearLabel);
        addFull(budget, monthYear, b, 2);
        addField(budget, "Budget Amount", budgetAmountField, b, 3);
        JButton setBudgetButton = button("Set Budget", true);
        setBudgetButton.addActionListener(event -> setBudget());
        addFull(budget, setBudgetButton, b, 5);

        summaryArea.setEditable(false);
        summaryArea.setFocusable(false);
        summaryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        summaryArea.setBackground(WHITE);
        summaryArea.setForeground(BLACK);
        summaryArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        panel.add(stats, BorderLayout.NORTH);
        panel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        panel.add(budget, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        statusLabel.setForeground(MUTED);
        footer.add(statusLabel, BorderLayout.WEST);
        return footer;
    }

    private void addExpense() {
        try {
            Expense expense = manager.addExpense(readDate(), readCategory(), readAmount(), readNote());
            saveData();
            refreshAll(manager.getAllExpenses());
            clearForm();
            showBudgetMessage(expense.getDate().getYear(), expense.getDate().getMonthValue());
            setStatus("Expense added with ID " + expense.getId() + ".", false);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void editSelectedExpense() {
        Expense selected = selectedExpense();
        if (selected == null) {
            showError("Select an expense from the table first.");
            return;
        }

        try {
            Expense updated = new Expense(selected.getId(), readDate(), readCategory(), readAmount(), readNote());
            manager.updateExpense(updated);
            saveData();
            refreshAll(manager.getAllExpenses());
            showBudgetMessage(updated.getDate().getYear(), updated.getDate().getMonthValue());
            setStatus("Expense updated.", false);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void deleteSelectedExpense() {
        Expense selected = selectedExpense();
        if (selected == null) {
            showError("Select an expense from the table first.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Delete expense ID " + selected.getId() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (answer == JOptionPane.YES_OPTION) {
            manager.deleteExpense(selected.getId());
            saveData();
            refreshAll(manager.getAllExpenses());
            clearForm();
            setStatus("Expense deleted.", false);
        }
    }

    private void filterByMonth() {
        try {
            int year = Integer.parseInt(yearField.getText().trim());
            int month = monthBox.getSelectedIndex() + 1;
            refreshAll(manager.filterByMonth(year, month));
            showBudgetMessage(year, month);
        } catch (NumberFormatException exception) {
            showError("Enter a valid year.");
        }
    }

    private void setBudget() {
        try {
            BigDecimal amount = new BigDecimal(budgetAmountField.getText().trim().replace(",", "")).setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Budget amount must be greater than zero.");
            }
            int year = Integer.parseInt(yearField.getText().trim());
            int month = monthBox.getSelectedIndex() + 1;
            manager.setMonthlyBudget(year, month, amount);
            saveData();
            refreshAll(manager.getAllExpenses());
            showBudgetMessage(year, month);
            setStatus("Budget saved.", false);
        } catch (NumberFormatException exception) {
            showError("Enter valid budget amount and year.");
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void exportReport() {
        boolean exported = fileHandler.exportReport(ReportGenerator.generateReport(manager));
        if (exported) {
            setStatus("Report exported to expense_report.txt.", false);
            JOptionPane.showMessageDialog(this, "Report exported to expense_report.txt");
        } else {
            showError("Could not export report.");
        }
    }

    private void refreshAll(List<Expense> expensesToShow) {
        tableModel.setRowCount(0);
        for (Expense expense : expensesToShow) {
            tableModel.addRow(new Object[]{
                    expense.getId(),
                    expense.getDate(),
                    expense.getCategory(),
                    Main.formatMoney(expense.getAmount()),
                    expense.getNote()
            });
        }

        List<Expense> all = manager.getAllExpenses();
        totalLabel.setText(Main.formatMoney(manager.getTotalSpending()));
        countLabel.setText(String.valueOf(all.size()));
        Expense highest = manager.getHighestExpense();
        highestLabel.setText(highest == null ? "None" : Main.formatMoney(highest.getAmount()));
        summaryArea.setText(buildSummaryText());
    }

    private String buildSummaryText() {
        StringBuilder builder = new StringBuilder();
        builder.append("CATEGORY SUMMARY\n");
        builder.append("----------------\n");
        appendMap(builder, manager.getCategoryWiseSummary());
        builder.append("\nMONTHLY SUMMARY\n");
        builder.append("---------------\n");
        appendMap(builder, manager.getMonthlySummary());
        return builder.toString();
    }

    private void appendMap(StringBuilder builder, Map<String, BigDecimal> map) {
        if (map.isEmpty()) {
            builder.append("No data\n");
            return;
        }
        for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
            builder.append(String.format("%-12s %s%n", entry.getKey(), Main.formatMoney(entry.getValue())));
        }
    }

    private void fillFormFromSelection() {
        Expense expense = selectedExpense();
        if (expense == null) {
            return;
        }
        dateField.setText(expense.getDate().toString());
        categoryField.setText(expense.getCategory());
        amountField.setText(expense.getAmount().toPlainString());
        noteField.setText(expense.getNote());
    }

    private Expense selectedExpense() {
        int row = expenseTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        return manager.findById(id);
    }

    private LocalDate readDate() {
        try {
            return LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Date must be yyyy-MM-dd.");
        }
    }

    private String readCategory() {
        String category = categoryField.getText().trim();
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
        return category;
    }

    private BigDecimal readAmount() {
        try {
            BigDecimal amount = new BigDecimal(amountField.getText().trim().replace(",", ""));
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero.");
            }
            return amount.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a valid amount.");
        }
    }

    private String readNote() {
        String note = noteField.getText().trim();
        if (note.length() > 80) {
            throw new IllegalArgumentException("Note must be 80 characters or fewer.");
        }
        return note;
    }

    private void showBudgetMessage(int year, int month) {
        BigDecimal budget = manager.getMonthlyBudget(year, month);
        if (budget == null) {
            return;
        }

        BigDecimal spent = manager.getMonthlySpending(year, month);
        if (spent.compareTo(budget) > 0) {
            showError("Budget crossed for " + YearMonth.of(year, month) + " by "
                    + Main.formatMoney(spent.subtract(budget)) + ".");
        } else {
            setStatus("Budget: " + Main.formatMoney(spent) + " / " + Main.formatMoney(budget), false);
        }
    }

    private void clearForm() {
        dateField.setText(LocalDate.now().toString());
        categoryField.setText("");
        amountField.setText("");
        noteField.setText("");
        expenseTable.clearSelection();
    }

    private void loadData() {
        manager.loadExpenses(fileHandler.loadExpenses());
        manager.loadBudgets(fileHandler.loadBudgets());
    }

    private void saveData() {
        fileHandler.saveExpenses(manager.getAllExpenses());
        fileHandler.saveBudgets(manager.getMonthlyBudgets());
    }

    private JPanel card(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(BLACK);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
        return label;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(BLACK);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }

    private JLabel valueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setForeground(BLACK);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15f));
        return label;
    }

    private JButton button(String text, boolean primary) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        if (primary) {
            button.setBackground(BLACK);
            button.setForeground(WHITE);
        } else {
            button.setBackground(WHITE);
            button.setForeground(BLACK);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    BorderFactory.createEmptyBorder(8, 13, 8, 13)
            ));
        }
        return button;
    }

    private void styleField(JTextField field) {
        field.setBackground(WHITE);
        field.setForeground(BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
        ));
    }

    private void addField(JPanel panel, String label, JTextField field, GridBagConstraints c, int row) {
        styleField(field);
        addFull(panel, label(label), c, row);
        addFull(panel, field, c, row + 1);
    }

    private void addStat(JPanel panel, String label, JLabel value, GridBagConstraints c, int row) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setOpaque(false);
        rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        JLabel name = new JLabel(label);
        name.setForeground(MUTED);
        rowPanel.add(name, BorderLayout.WEST);
        rowPanel.add(value, BorderLayout.EAST);
        addFull(panel, rowPanel, c, row);
    }

    private void addFull(JPanel panel, java.awt.Component component, GridBagConstraints c, int row) {
        c.gridy = row;
        panel.add(component, c);
    }

    private GridBagConstraints constraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        return c;
    }

    private void showError(String message) {
        setStatus(message, true);
        JOptionPane.showMessageDialog(this, message, "Smart Expense Journal", JOptionPane.ERROR_MESSAGE);
    }

    private void setStatus(String message, boolean error) {
        statusLabel.setText(message);
        statusLabel.setForeground(error ? ERROR : MUTED);
    }

    private static String[] monthOptions() {
        String[] months = new String[12];
        for (int i = 0; i < 12; i++) {
            months[i] = java.time.Month.of(i + 1).name().substring(0, 1)
                    + java.time.Month.of(i + 1).name().substring(1).toLowerCase();
        }
        return months;
    }
}
