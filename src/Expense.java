import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense {
    private int id;
    private LocalDate date;
    private String category;
    private BigDecimal amount;
    private String note;

    public Expense(int id, LocalDate date, String category, BigDecimal amount, String note) {
        this.id = id;
        this.date = date;
        this.category = cleanCategory(category);
        this.amount = amount;
        this.note = note == null ? "" : note.trim();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = cleanCategory(category);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? "" : note.trim();
    }

    private String cleanCategory(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Other";
        }
        String cleaned = value.trim().toLowerCase();
        return cleaned.substring(0, 1).toUpperCase() + cleaned.substring(1);
    }
}
