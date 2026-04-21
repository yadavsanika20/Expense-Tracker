package expenseTracker;

import java.time.Month;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExpenseService {

    private final FileStore store;
    private final List<Expense> expenses;   // ✅ List → import java.util.List
    private int nextId;

    public ExpenseService() {
        this.store    = new FileStore();
        this.expenses = store.loadAll();    // ✅ this.expenses = (not __this.expenses__)
        this.nextId   = expenses.stream()  // ✅ expenses (not __expenses__)
                .mapToInt(Expense::getId)
                .max()
                .orElse(0) + 1;
    }

    // ADD a new expense
    public Expense addExpense(String description, double amount, Category category, LocalDate date) {
        Expense e = new Expense(nextId++, description, amount, category, date);
        expenses.add(e);
        store.saveAll(expenses);
        return e;
    }

    // DELETE an expense by ID
    public boolean deleteExpense(int id) {
        boolean removed = expenses.removeIf(e -> e.getId() == id);
        if (removed) store.saveAll(expenses);
        return removed;
    }

    // GET ALL expenses
    public List<Expense> getAll() {
        return Collections.unmodifiableList(expenses);
    }

    // filter() → keep only matching elements
    public List<Expense> filterByCategory(Category category) {  // ✅ List → import java.util.List
        return expenses.stream()                                 // ✅ expenses
                .filter(e -> e.getCategory() == category)
                .collect(Collectors.toList());                  // ✅ Collectors → import java.util.stream.Collectors
    }

    // filter() + sorted() → filter then sort by date
    public List<Expense> filterByMonth(int year, Month month) { // ✅ Month → import java.time.Month
        return expenses.stream()
                .filter(e -> e.getDate().getYear() == year
                          && e.getDate().getMonth() == month)
                .sorted(Comparator.comparing(Expense::getDate)) // ✅ Comparator → import java.util.Comparator
                .collect(Collectors.toList());
    }

    // filter by date range
    public List<Expense> filterByDateRange(LocalDate from, LocalDate to) {
        return expenses.stream()
                .filter(e -> !e.getDate().isBefore(from) && !e.getDate().isAfter(to))
                .sorted(Comparator.comparing(Expense::getDate))
                .collect(Collectors.toList());
    }

    // groupingBy() + summingDouble() → group by category, sum amounts per group
    public Map<Category, Double> categoryBreakdown(List<Expense> list) { // ✅ Map → import java.util.Map
        return list.stream()
                .collect(Collectors.groupingBy(                          // ✅ Collectors
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));
    }

    // monthly breakdown for a full year
    public Map<Month, Double> monthlyBreakdown(int year) {
        return expenses.stream()
                .filter(e -> e.getDate().getYear() == year)
                .collect(Collectors.groupingBy(
                        e -> e.getDate().getMonth(),
                        Collectors.summingDouble(Expense::getAmount)
                ));
    }

    // total sum of a list
    public double totalAmount(List<Expense> list) {
        return list.stream().mapToDouble(Expense::getAmount).sum();
    }

    // most expensive single expense
    public Optional<Expense> mostExpensive() {
        return expenses.stream().max(Comparator.comparingDouble(Expense::getAmount));
    }

    // sorted().limit() → top N results
    public List<Expense> topExpenses(int n) {
        return expenses.stream()                                          // ✅ expenses
                .sorted(Comparator.comparingDouble(Expense::getAmount).reversed()) // ✅ Comparator
                .limit(n)
                .collect(Collectors.toList());                           // ✅ Collectors
    }
}