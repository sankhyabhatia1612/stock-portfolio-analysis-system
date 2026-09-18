# Stock Portfolio Analysis & Management System

A production-grade, 3-tier full-stack application engineered for real-time stock portfolio tracking, equity analytics, and investor risk management. The application bridges a modern single-page web interface with an enterprise Oracle 21c Database via a custom Java JDBC HTTP REST server.

---

## 📌 Project Summary

In traditional academic setups, Database Management Systems (DBMS) are often demonstrated solely through Command Line Interface (CLI) queries. This project bridges that gap by coupling a high-performance Oracle 21c Relational Database with a modern visual web dashboard.

All financial entities—investors, portfolios, stock equities, market trades, and watchlists—are modeled according to relational design best practices and manipulated through transactional JDBC REST endpoints.

---

## 🛠️ System Architecture & Tech Stack

The application adopts a completely decoupled 3-tier enterprise architecture:

- **Presentation Layer (Frontend):** Built with semantic HTML5, modern CSS variables, and vanilla JavaScript (ES6+). Operates cleanly without heavy bundlers or client-side frameworks.
- **Business Logic Layer (Backend):** A multi-threaded, custom Java HTTP Server (`com.sun.net.httpserver`) built with Java Database Connectivity (JDBC) and Data Access Object (DAO) design patterns.
- **Data Persistence Layer (Database):** Enterprise Oracle 21c Relational Database Management System (RDBMS) featuring SQL DDL schemas, constraints, PL/SQL stored routines, triggers, and automated views.

---

## ⚡ DBMS Concepts & PL/SQL Features Implemented

The database architecture leverages advanced Relational Database Management System concepts:

### 1. Advanced SQL & Aggregate Functions
- **Aggregation & Analytics:** Uses `SUM()`, `AVG()`, `COUNT()`, and `GROUP BY` clauses to compute real-time metrics such as total portfolio asset values, average stock execution prices, and total active users.
- **Multi-Table Joins:** Utilizes `INNER JOIN` and `LEFT OUTER JOIN` queries across `USERS`, `PORTFOLIO`, `STOCKS`, and `TRANSACTIONS` tables for audit reports.
- **Relational Integrity:** Strict enforcement of `PRIMARY KEY`, `FOREIGN KEY` (with `ON DELETE CASCADE`), `UNIQUE` email constraints, and `CHECK` constraints on stock prices and transaction quantities.

### 2. PL/SQL Stored Procedures & Functions
- **`CALCULATE_PORTFOLIO_VALUE(p_user_id NUMBER)`:** A PL/SQL function that dynamically aggregates the current market value of all equities owned by a specific investor plus their available cash balance.
- **`EXECUTE_TRADE_TRANSACTION(...)`:** A PL/SQL stored procedure that performs atomic buy/sell order processing, verifying cash sufficiency before logging records.

### 3. Automated Database Triggers & Views
- **`TRG_UPDATE_PORTFOLIO_CASH`:** An automated `AFTER INSERT` trigger on the `TRANSACTIONS` table that updates the investor's cash balance whenever a `BUY` or `SELL` trade is executed.
- **`DASHBOARD_SUMMARY_VIEW`:** A pre-compiled Oracle database view providing rapid, aggregated system metrics directly to the API endpoint without runtime computation overhead.

---

## 🔥 Core System Functionalities

- **Interactive Analytics Dashboard:** Real-time summary metrics, trade history ledger, and visual distribution charts for sectors and account cash balances.
- **Investor & Risk Administration:** Onboarding engine for registering users with specific risk profiles (`LOW`, `MEDIUM`, `HIGH`) and strict unique constraint validation.
- **Equity Directory & Search:** Comprehensive table of listed stocks (`NSE`/`BSE`) with ticker symbols, prices, and instant keyword filtering.
- **Trade Execution Ledger:** Record atomic `BUY` and `SELL` transactions with automated timestamp logging and balance calculations.

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