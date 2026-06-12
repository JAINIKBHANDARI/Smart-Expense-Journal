package com.smartexpensejournal.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

public final class GuiTheme {
    public static final Color BLACK = new Color(18, 18, 18);
    public static final Color CHARCOAL = new Color(34, 34, 34);
    public static final Color PANEL = new Color(248, 248, 248);
    public static final Color WHITE = Color.WHITE;
    public static final Color BORDER = new Color(215, 215, 215);
    public static final Color MUTED = new Color(105, 105, 105);
    public static final Color SUCCESS = new Color(42, 126, 76);
    public static final Color ERROR = new Color(174, 54, 54);

    private GuiTheme() {
    }

    public static void install() {
        UIManager.put("Panel.background", PANEL);
        UIManager.put("OptionPane.background", WHITE);
        UIManager.put("OptionPane.messageForeground", BLACK);
        UIManager.put("TextField.caretForeground", BLACK);
        UIManager.put("ComboBox.background", WHITE);
        UIManager.put("ComboBox.foreground", BLACK);
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        );
    }

    public static void stylePrimaryButton(JButton button) {
        button.setBackground(BLACK);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.BOLD));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setBackground(WHITE);
        button.setForeground(BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleDangerButton(JButton button) {
        styleSecondaryButton(button);
        button.setForeground(ERROR);
    }

    public static void styleField(JTextField field) {
        field.setBackground(WHITE);
        field.setForeground(BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    public static void styleTable(JTable table) {
        table.setBackground(WHITE);
        table.setForeground(BLACK);
        table.setGridColor(new Color(232, 232, 232));
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(CHARCOAL);
        table.setSelectionForeground(WHITE);
        table.getTableHeader().setBackground(BLACK);
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
        table.getTableHeader().setReorderingAllowed(false);
    }

    public static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(BLACK);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }

    public static void padded(JComponent component, int top, int left, int bottom, int right) {
        component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }
}
