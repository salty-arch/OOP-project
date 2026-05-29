# Personal Finance Manager

A personal finance tracking application with dual interfaces — a modern web dashboard and a console-based CLI — both backed by a Java server and SQLite.

## Features

- **Income & Expense Tracking** — Log and categorize transactions
- **Budget Management** — Set category budgets and track spending vs limits
- **Financial Goals** — Set saving targets and spending limits with deadlines
- **Monthly Reports** — Generate HTML financial reports for any month/year
- **Admin Panel** — User management, activity logs, financial overview, password resets
- **Dual Interface** — Web UI (gradient theme, glassmorphism, pill navigation) or console CLI

## Tech Stack

- **Backend:** Java 21 with JDK `com.sun.net.httpserver.HttpServer`
- **Frontend:** Vanilla HTML, CSS, JavaScript (no frameworks)
- **Database:** SQLite via sqlite-jdbc
- **Build:** Maven

## Quick Start

1. **Build:**
   ```
   mvn compile
   ```

2. **Run the web server:**
   ```
   mvn exec:java
   ```
   Open http://localhost:7000 in your browser.

3. **Or run the console app:**
   ```
   java -cp "target\classes;%USERPROFILE%\.m2\repository\org\xerial\sqlite-jdbc\3.42.0.0\sqlite-jdbc-3.42.0.0.jar" main.Main
   ```

## Default Users

| Email | Password | Role |
|---|---|---|
| admin@gmail.com | mjk | Admin |
| salman@gmail.com | mkk | Client |

## Project Structure

```
src/
  api/          Web server controllers (login, dashboard, transactions, budgets, goals, reports, admin)
  main/         Console entry point
  model/        Data models (Client, Admin, User, FinancialGoals, budgeting)
  util/         Database helper, I/O helpers
webapp/         Static frontend files (HTML, CSS, JS)
pom.xml         Maven build configuration
```
