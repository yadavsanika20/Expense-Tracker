package expenseTracker;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {

    // ── Constants & fields ───────────────────────────────────────
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Scanner sc      = new Scanner(System.in);
    private final ExpenseService  svc = new ExpenseService();

    // ── Start the app ────────────────────────────────────────────
    public void start() {
        show("╔══════════════════════════════╗");
        show("║    Java Expense Tracker      ║");
        show("╚══════════════════════════════╝");

        while (true) {
            showMenu();
            switch (sc.nextLine().trim()) {
                case "1" -> addExpense();
                case "2" -> listAll();
                case "3" -> filterCategory();
                case "4" -> filterDateRange();
                case "5" -> monthlySummary();
                case "6" -> categoryBreakdown();
                case "7" -> top5();
                case "8" -> deleteExpense();
                case "9" -> { show("Goodbye!"); return; }
                default  -> show("Invalid choice. Try 1–9.");
            }
        }
    }

    // ── Menu ─────────────────────────────────────────────────────
    private void showMenu() {
        show("\n─── Menu ──────────────────────");
        show("1.Add  2.List  3.Category  4.Date Range");
        show("5.Monthly  6.Breakdown  7.Top5  8.Delete  9.Exit");
        prompt("Choose: ");
    }

    // ── 1. Add expense ───────────────────────────────────────────
    private void addExpense() {
        show("\n─── Add Expense ───────────────");

        prompt("Description: ");
        String desc = sc.nextLine().trim();
        if (desc.isEmpty()) { show("Description cannot be empty."); return; }

        double amount = readAmount();
        Category cat  = readCategory();
        LocalDate date = readDate("Date (yyyy-MM-dd, Enter=today): ");
        if (date == null) date = LocalDate.now();

        Expense e = svc.addExpense(desc, amount, cat, date);
        show("✔ Added → " + e);
    }

    // ── 2. List all ──────────────────────────────────────────────
    private void listAll() {
        List<Expense> list = svc.getAll();
        show("\n─── All Expenses ──────────────");
        if (list.isEmpty()) { show("No expenses yet."); return; }
        printHeader();
        list.stream()
            .sorted(java.util.Comparator.comparing(Expense::getDate).reversed())
            .forEach(e -> show(e.toString()));
        show(String.format("Total: ₹%.2f  (%d records)", svc.totalAmount(list), list.size()));
    }

    // ── 3. Filter by category ────────────────────────────────────
    private void filterCategory() {
        show("\n─── Filter by Category ────────");
        Category cat  = readCategory();
        List<Expense> list = svc.filterByCategory(cat);
        if (list.isEmpty()) { show("No expenses in " + cat.display()); return; }
        printHeader();
        list.forEach(e -> show(e.toString()));
        show(String.format("Total (%s): ₹%.2f", cat.display(), svc.totalAmount(list)));
    }

    // ── 4. Filter by date range ──────────────────────────────────
    private void filterDateRange() {
        show("\n─── Filter by Date Range ──────");
        LocalDate from = readDate("From (yyyy-MM-dd): ");
        LocalDate to   = readDate("To   (yyyy-MM-dd): ");
        if (from == null || to == null) { show("Invalid dates."); return; }
        List<Expense> list = svc.filterByDateRange(from, to);
        if (list.isEmpty()) { show("No expenses in this range."); return; }
        printHeader();
        list.forEach(e -> show(e.toString()));
        show(String.format("Total: ₹%.2f", svc.totalAmount(list)));
    }

    // ── 5. Monthly summary ───────────────────────────────────────
    private void monthlySummary() {
        show("\n─── Monthly Summary ───────────");
        int year = readYear();
        prompt("Month (e.g. APRIL): ");
        Month month;
        try { month = Month.valueOf(sc.nextLine().trim().toUpperCase()); }
        catch (IllegalArgumentException e) { show("Invalid month."); return; }
        List<Expense> list = svc.filterByMonth(year, month);
        if (list.isEmpty()) { show("No expenses for " + month + " " + year); return; }
        printHeader();
        list.forEach(e -> show(e.toString()));
        show(String.format("Total (%s %d): ₹%.2f", month, year, svc.totalAmount(list)));
    }

    // ── 6. Category breakdown ────────────────────────────────────
    private void categoryBreakdown() {
        show("\n─── Category Breakdown ────────");
        List<Expense> all = svc.getAll();
        if (all.isEmpty()) { show("No expenses."); return; }
        double total = svc.totalAmount(all);
        svc.categoryBreakdown(all).entrySet().stream()
           .sorted(Map.Entry.<Category, Double>comparingByValue().reversed())
           .forEach(en -> {
               double pct = (en.getValue() / total) * 100;
               String bar = "█".repeat((int)(pct / 5)) + "░".repeat(20 - (int)(pct / 5));
               show(String.format("%-13s ₹%8.2f  %5.1f%%  %s",
                       en.getKey().display(), en.getValue(), pct, bar));
           });
        show(String.format("%-13s ₹%8.2f", "TOTAL", total));
    }

    // ── 7. Top 5 ─────────────────────────────────────────────────
    private void top5() {
        show("\n─── Top 5 Expenses ────────────");
        List<Expense> top = svc.topExpenses(5);
        if (top.isEmpty()) { show("No expenses."); return; }
        printHeader();
        top.forEach(e -> show(e.toString()));
    }

    // ── 8. Delete ────────────────────────────────────────────────
    private void deleteExpense() {
        show("\n─── Delete Expense ────────────");
        prompt("Enter ID to delete: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            show(svc.deleteExpense(id) ? "✔ Deleted #" + id : "No expense with ID " + id);
        } catch (NumberFormatException e) {
            show("Invalid ID.");
        }
    }

    // ── Helpers ──────────────────────────────────────────────────

    private double readAmount() {
        while (true) {
            prompt("Amount (₹): ");
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { show("Enter a valid number."); }
        }
    }

    private Category readCategory() {
        show("Categories: " + Arrays.toString(Category.values()));
        prompt("Category (default OTHER): ");
        return Category.fromString(sc.nextLine().trim());
    }

    private LocalDate readDate(String msg) {
        prompt(msg);
        try { return LocalDate.parse(sc.nextLine().trim(), FMT); }
        catch (DateTimeParseException e) { return null; }
    }

    private int readYear() {
        prompt("Year (Enter = " + LocalDate.now().getYear() + "): ");
        String y = sc.nextLine().trim();
        try { return y.isEmpty() ? LocalDate.now().getYear() : Integer.parseInt(y); }
        catch (NumberFormatException e) { return LocalDate.now().getYear(); }
    }

    private void printHeader() {
        show(String.format("%-4s %-12s %-14s %9s  %s", "ID","Date","Category","Amount","Description"));
        show("─".repeat(60));
    }

    private void show(String s)   { System.out.println(s); }
    private void prompt(String s) { System.out.print(s); System.out.flush(); }
}