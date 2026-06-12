# Smart Expense Journal

A clean core Java expense journal with a polished Swing desktop UI and an optional console mode.

## Features

- Add expense with date, category, amount, and note
- List all expenses in a formatted desktop table
- Search by keyword across id, date, category, amount, and note
- Filter expenses by month
- Show total spend, category totals, monthly totals, and highest expense
- Delete expense by id with confirmation
- Save and load expenses using a JSON-lines style text file
- Input validation for menu choices, dates, months, amounts, ids, and notes
- Optional console mode for terminal use

## Requirements

- Java 8 or newer
- No external libraries

## Run

Run the desktop UI from the project root through the main file only:

```bash
java Main.java
```

Run the older console menu:

```bash
java Main.java console
```

## Desktop UI

The desktop UI includes:

- Add-expense form
- Search field
- Month filter using `yyyy-MM`
- Expense table
- Snapshot totals
- Delete selected row
- Save and reload actions

## Console Menu

```text
1. Add Expense
2. List All Expenses
3. Search Expenses
4. Filter By Month
5. Show Totals
6. Delete By Id
7. Save Now
8. Save And Exit
```

## Sample Flow

```text
Choose an option: 1
Date (yyyy-MM-dd, blank for today): 2026-06-12
Category: Food
Amount: 249.50
Note: Lunch with friends

Choose an option: 3
Keyword: lunch

Choose an option: 4
Month (yyyy-MM): 2026-06
```

## Data File

Expenses are saved in:

```text
src/main/resources/data/expenses.jsonl
```

Each line is a JSON-like object:

```json
{"id":1,"date":"2026-06-12","category":"FOOD","amount":"249.50","note":"Lunch with friends"}
```

## Package Structure

```text
com.smartexpensejournal
  app
  exception
  io
  model
  repository
  service
  util
  validation
```
