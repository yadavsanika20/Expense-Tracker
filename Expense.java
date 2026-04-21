package expenseTracker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Expense {

    // ✅ Must be static — because fromCsv() is a static method
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final int id;
    private final String description;
    private final double amount;
    private final Category category;
    private final LocalDate date;

    public Expense(int id, String description, double amount, Category category, LocalDate date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    // Converts object → one line of text for saving to file
    public String toCsv() {
        return id + "," + date.format(FMT) + "," + category.name()
                + "," + amount + "," + description.replace(",", ";");
    }

    // Converts one line of text → object when loading from file
    public static Expense fromCsv(String line) {
        String[] p = line.split(",", 5);
        int id          = Integer.parseInt(p[0].trim());
        LocalDate date  = LocalDate.parse(p[1].trim(), FMT);
        Category cat    = Category.fromString(p[2].trim());
        double amount   = Double.parseDouble(p[3].trim());
        String desc     = p.length > 4 ? p[4].trim() : "";
        return new Expense(id, desc, amount, cat, date);
    }

    public int getId()             { return id; }
    public String getDescription() { return description; }
    public double getAmount()      { return amount; }
    public Category getCategory()  { return category; }
    public LocalDate getDate()     { return date; }

    @Override
    public String toString() {
        return String.format("%-4d %-12s %-15s %8.2f  %s",
                id, date.format(FMT), category.display(), amount, description);
    }
}