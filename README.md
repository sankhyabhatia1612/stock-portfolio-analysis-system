# Stock Portfolio Analysis & Management System

A production-grade, 3-tier full-stack application engineered for real-time stock portfolio tracking, equity analytics, and investor risk management. The application bridges a modern single-page web interface with an enterprise Oracle 21c Database via a custom Java JDBC HTTP REST server.

---

## 📌 Executive Summary

In traditional academic setups, Database Management Systems (DBMS) are often demonstrated using simple Command Line Interface (CLI) queries. This project eliminates this limitation by integrating a rich visual dashboard directly with an Oracle Relational Database Engine.

The system enforces strict relational integrity, ACID compliance, primary/foreign key constraints, and transactional auditing across stock market equities, portfolio cash balances, user watchlists, and market trades.

---

## 🛠️ System Architecture & Tech Stack

The application adopts a completely decoupled 3-tier enterprise architecture:

- **Presentation Layer (Frontend):** Built using semantic HTML5, modern CSS3 custom variables, vanilla JavaScript (ES6+), and Chart.js. Operates cleanly without external heavy dependencies or bundlers.
- **Business Logic Layer (Backend):** A multi-threaded, custom Java HTTP Server (`com.sun.net.httpserver`) built with Java Database Connectivity (JDBC) and Data Access Object (DAO) design patterns.
- **Data Persistence Layer (Database):** Enterprise Oracle 21c Relational Database Management System (RDBMS) featuring SQL DDL schemas, constraints, PL/SQL stored routines, triggers, and automated views.

---

## ⚡ DBMS Concepts & PL/SQL Features Implemented

This project extensively leverages advanced Relational Database Management System concepts:

### 1. Advanced SQL & Aggregate Functions
- **Aggregation & Analytics:** Uses `SUM()`, `AVG()`, `COUNT()`, and `GROUP BY` clauses to compute real-time metrics such as total portfolio asset values, average stock execution prices, and total active users.
- **Multi-Table Joins:** Utilizes `INNER JOIN` and `LEFT OUTER JOIN` queries across `USERS`, `PORTFOLIO`, `STOCKS`, and `TRANSACTIONS` tables for comprehensive audit reports.
- **Relational Integrity:** Strict enforcement of `PRIMARY KEY`, `FOREIGN KEY` (with `ON DELETE CASCADE`), `UNIQUE` email constraints, and `CHECK` constraints on stock prices and transaction quantities.

### 2. PL/SQL Stored Procedures & Functions
- **`CALCULATE_PORTFOLIO_VALUE(p_user_id NUMBER)`:** A PL/SQL function that dynamically aggregates the current market value of all equities owned by a specific investor plus their available cash balance.
- **`EXECUTE_TRADE_TRANSACTION(...)`:** A PL/SQL stored procedure that performs atomic buy/sell order processing, verifying cash sufficiency before logging records.

### 3. Automated Database Triggers & Views
- **`TRG_UPDATE_PORTFOLIO_CASH`:** An automated `AFTER INSERT` trigger on the `TRANSACTIONS` table that updates the investor's cash balance whenever a `BUY` or `SELL` trade is executed.
- **`DASHBOARD_SUMMARY_VIEW`:** A pre-compiled Oracle database view providing rapid, aggregated system metrics directly to the API endpoint without runtime computation overhead.

---

## 🔥 Core System Functionalities

### 1. Interactive Analytics Dashboard
- **Live Metric Cards:** Instant status readouts for Total Users, Active Portfolios, Listed Stocks, and Executed Trades.
- **Sector Distribution Chart:** Dynamic Chart.js Doughnut chart tracking investment allocations across market sectors (IT, Finance, Energy, Healthcare).
- **Cash Balance Allocation:** Visual Bar chart illustrating liquidity profiles across investor accounts.

### 2. Investor & Risk Administration
- **User Registration Engine:** Full onboarding system capturing Name, Email, and Risk Tolerance (`LOW`, `MEDIUM`, `HIGH`).
- **Constraint Checker:** Client-side and server-side validation preventing duplicate User IDs or duplicate email registrations.
- **Visual Risk Badges:** Dynamic color-coded pill indicators (`LOW` = Green, `MEDIUM` = Blue, `HIGH` = Red).

### 3. Equity & Market Management
- **Stock Directory:** List securities across major exchanges (`NSE`/`BSE`) with Ticker Symbols, Sectors, and Live Prices.
- **Instant Keyword Search:** Real-time client-side filter engine for searching stocks by symbol, company name, or sector.

### 4. Portfolio & Trade Auditing
- **Trade Execution Ledger:** Record atomic `BUY` and `SELL` market orders with automated timestamp logging.
- **Investor Watchlists:** Track high-potential stocks on a per-user basis.

---

## 📁 Project Repository Structure

```text
stock-portfolio-analysis-system/
├── backend/
│   ├── DBConnection.java          # Oracle JDBC Connection pool & Env loader
│   ├── DatabaseConnectionTest.java # JDBC connection verification script
│   ├── MainApp.java               # Custom Java HTTP REST Server (Port 8080)
│   ├── StockDAO.java              # Data Access Object executing SQL/PLSQL queries
│   └── ojdbc8.jar                 # Oracle Database 21c JDBC Driver
├── database/
│   ├── schema.sql                 # DDL script: Tables, Constraints, Triggers & Views
│   └── sample_data.sql            # DML script: Initial seed records
├── frontend/
│   ├── index.html                 # Dashboard layout & HTML5 semantic UI
│   ├── styles.css                 # Custom responsive layout & component styling
│   └── app.js                     # Tab routing, state engine & API fetch logic
├── .gitignore                     # Git exclusion rules
└── README.md                      # Project documentation