package expenseTracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileStore {

    // ✅ Define FILE name and HEADER as constants at the top
    private static final String FILE = "expenses.csv";
    private static final String HEADER = "id,date,category,amount,description";

    // READING — loads all expenses from file into a List
    public List<Expense> loadAll() {
        List<Expense> list = new ArrayList<>();  // ✅ list declared here
        Path path = Paths.get(FILE);             // ✅ path declared here

        if (!Files.exists(path)) return list;    // if no file yet, return empty list

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;                         // ✅ line declared here
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (firstLine && line.startsWith("id")) { firstLine = false; continue; } // skip header
                firstLine = false;
                try {
                    list.add(Expense.fromCsv(line));
                } catch (Exception e) {
                    System.err.println("Skipping bad line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return list; // ✅ must return the list
    }

    // WRITING — saves entire list back to file
    public void saveAll(List<Expense> expenses) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(FILE))) {
            bw.write(HEADER);   // ✅ write "id,date,category,amount,description"
            bw.newLine();
            for (Expense e : expenses) {
                bw.write(e.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving file: " + e.getMessage());
        }
    }
}