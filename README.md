# Smart Expense Journal

Core Java console application for tracking expenses.

## Project Status

Structure only. Implementation code will be added later.

## Planned Features

- Add expense with date, category, amount, and note
- List all expenses
- Search expenses by keyword
- Filter expenses by month
- Show category and monthly totals
- Delete expense by id
- Save and load data using a JSON-like text file
- Validate console input
- Simple command menu loop

## Planned Run Steps

```bash
javac -d out src/main/java/com/smartexpensejournal/**/*.java
java -cp out com.smartexpensejournal.Main
```

## Planned Sample Commands

```text
1. Add Expense
2. List All Expenses
3. Search Expenses
4. Filter By Month
5. Show Totals
6. Delete Expense
7. Save
8. Exit
```

## Package Structure

```text
src/main/java/com/smartexpensejournal
src/main/java/com/smartexpensejournal/app
src/main/java/com/smartexpensejournal/model
src/main/java/com/smartexpensejournal/service
src/main/java/com/smartexpensejournal/repository
src/main/java/com/smartexpensejournal/io
src/main/java/com/smartexpensejournal/validation
src/main/java/com/smartexpensejournal/util
src/main/java/com/smartexpensejournal/exception
src/main/resources/data
src/test/java/com/smartexpensejournal
```
