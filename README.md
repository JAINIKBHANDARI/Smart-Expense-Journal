# Smart Expense Journal

A Core Java expense tracker with a clean desktop UI and an optional console mode.

## Tech Used

- Core Java
- Swing UI
- OOP and encapsulation
- Collections: `ArrayList`, `HashMap`, `TreeMap`, `LinkedHashMap`
- File handling with CSV text files
- Exception handling and input validation

## Features

- Add expense with date, category, amount, and note from a form
- View expenses in a table
- View all expenses in a formatted table
- Edit an existing expense by ID
- Delete expense by ID with confirmation
- Search expense notes by keyword
- Filter expenses by category
- Filter expenses by month and year
- Show total spending
- Show category-wise spending summary
- Show monthly spending summary
- Show highest expense
- Show recent 5 expenses
- Set monthly budget and show warning when crossed
- Export report to `expense_report.txt`
- Data remains saved after closing the app
- Handles missing or invalid data files safely

## Folder Structure

```text
SMART EXPENSE JOURNAL/
  Main.java
  Expense.java
  ExpenseManager.java
  FileHandler.java
  InputHelper.java
  ReportGenerator.java
  data/
    expenses.csv
    budgets.csv
  README.md
```

## How To Run

Open terminal in the project folder.

```powershell
javac *.java
java Main
```

This opens the desktop UI.

For console mode:

```powershell
java Main console
```

Or double-click:

```text
RUN_APP.bat
```

In VS Code:

1. Open the project folder.
2. Open `Main.java`.
3. Compile with `javac *.java`.
4. Run with `java Main`.

## UI

The default UI has:

- Expense form on the left
- Expense table in the center
- Search/category/month filters
- Summary and budget panel
- Add, edit, delete, save, reload, recent, and export buttons

## Sample Console Output

```text
==============================================================================
                            SMART EXPENSE JOURNAL
                       Core Java Console Expense Tracker
==============================================================================
Total Expenses: 3      Total Spending: INR 1450.00
------------------------------------------------------------------------------
 1. Add Expense
 2. View All Expenses
 3. Edit Expense
 4. Delete Expense
 5. Search Notes
 6. Filter By Category
 7. Filter By Month And Year
 8. View Spending Summary
 9. Recent 5 Expenses
10. Set Monthly Budget
11. Export Text Report
12. Save And Exit
------------------------------------------------------------------------------
Choose option:
```

Expense table:

```text
-----------------------------------------------------------------------------------------------
ID    Date         Category                Amount  Note
-----------------------------------------------------------------------------------------------
1     2026-06-12   Food                INR 250.00  Lunch with friends
2     2026-06-13   Travel              INR 900.00  Bus pass
-----------------------------------------------------------------------------------------------
Rows: 2
```

## Class Explanation

- `Main`: Runs the menu loop and connects user actions with the manager, file handler, and reports.
- `Expense`: Model class that stores one expense with private fields and getters/setters.
- `ExpenseManager`: Handles business logic like add, edit, delete, search, filter, summaries, recent expenses, and budgets.
- `FileHandler`: Reads and writes expenses/budgets using CSV files and skips corrupted lines safely.
- `InputHelper`: Handles user input validation so invalid input does not crash the program.
- `ReportGenerator`: Prints tables/summaries and creates the export report text.

## What I Learned

- How to structure a Java console project using OOP.
- How to use collections for fast lookup and reporting.
- How to persist data using file handling.
- How to validate user input safely.
- How to separate UI, business logic, and file handling.

## Interview Explanation

Smart Expense Journal is a Core Java console project that helps users manage daily expenses. I used OOP by separating the project into classes like `Expense`, `ExpenseManager`, `FileHandler`, `InputHelper`, and `ReportGenerator`. Expenses are stored in an `ArrayList`, and a `HashMap` is used for quick search by ID during edit and delete operations. The app loads data once at startup from CSV files and saves only after add, edit, delete, or budget updates, which keeps it fast. It also supports filtering, summaries, budget warnings, and report export, so it demonstrates collections, file handling, validation, and clean Java design.

## Possible Interview Questions

**Q1. Why did you use `ArrayList`?**  
I used `ArrayList` because expenses are mainly displayed, filtered, and iterated in order, and `ArrayList` is efficient for that.

**Q2. Why did you use `HashMap`?**  
I used `HashMap<Integer, Expense>` to find expenses quickly by ID while editing or deleting.

**Q3. How is data saved?**  
The app saves expenses in `data/expenses.csv` and monthly budgets in `data/budgets.csv`.

**Q4. How do you handle invalid data?**  
While loading CSV files, invalid or corrupted lines are skipped with a warning instead of crashing the app.

**Q5. How did you improve performance?**  
Data is loaded once at startup. File saving happens only after add, edit, delete, or budget changes. Sorted expense data is cached and rebuilt only when the list changes.

**Q6. What OOP concepts are used?**  
Encapsulation, class separation, object modeling, and single-responsibility style design.

**Q7. What future improvements can be added?**  
Authentication, charts, recurring expenses, category suggestions, backup files, and unit tests.
