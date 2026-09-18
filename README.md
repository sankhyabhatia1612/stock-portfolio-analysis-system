# Stock Portfolio Analysis & Management System

A production-grade, 3-tier full-stack application engineered for real-time stock portfolio tracking, equity analytics, and investor risk management. The application bridges a modern single-page web interface with an enterprise Oracle 21c Database via a custom Java JDBC HTTP REST server.

---

## 📑 Table of Contents

- [Project Overview](#-project-overview)
- [Objectives](#-objectives)
- [Complete Technology Stack](#-complete-technology-stack)
- [System Architecture](#-system-architecture)
- [Data Flow](#-data-flow)
- [Dashboard Functionalities](#-dashboard-functionalities)
- [Investor Management](#-investor-management)
- [Stock Management](#-stock-management)
- [Portfolio Management](#-portfolio-management)
- [Transaction Management](#-transaction-management)
- [Watchlist Management](#-watchlist-management)
- [Stock Price History](#-stock-price-history)
- [Portfolio Analysis](#-portfolio-analysis)
- [All Database Tables](#-all-database-tables)
- [Important Database Relationships](#-important-database-relationships)
- [Primary / Foreign / Composite Keys](#-primary--foreign--composite-keys)
- [Java Backend Explanation](#-java-backend-explanation)
- [JDBC Integration](#-jdbc-integration)
- [API Endpoints](#-api-endpoints)
- [Frontend Explanation](#-frontend-explanation)
- [Project Folder Structure](#-project-folder-structure)
- [Complete Setup / Run Instructions](#-complete-setup--run-instructions)
  - [Database Setup](#database-setup)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Suggested Demonstration Flow for Your Evaluator](#-suggested-demonstration-flow-for-your-evaluator)
- [Team Responsibilities](#-team-responsibilities)
- [DBMS Concepts Demonstrated](#-dbms-concepts-demonstrated)
- [Current Implementation Notes](#-current-implementation-notes)
- [Future Enhancements](#-future-enhancements)
- [Security Note](#-security-note)
- [GitHub Repository Information](#-github-repository-information)

---

## 📌 Project Overview

In traditional academic setups, Database Management Systems (DBMS) are often demonstrated solely through Command Line Interface (CLI) queries. This project eliminates that limitation by coupling an enterprise Oracle 21c Database with an intuitive web UI. All core financial entities—investors, trading accounts, stock equities, market orders, and watchlists—are modeled using relational design best practices and manipulated through transactional REST APIs.

---

## 🎯 Objectives

- Provide a reliable, production-ready full-stack platform for portfolio management.
- Demonstrate enterprise relational database architecture using Oracle 21c RDBMS.
- Enforce business logic directly inside the database via PL/SQL routines, triggers, and constraints.
- Implement robust multi-tiered execution using Java HTTP REST Server and raw JDBC drivers without heavy external ORM dependencies.

---

## 💻 Complete Technology Stack

- **Frontend:** Semantic HTML5, CSS3 Custom Properties, JavaScript (ES6+ Vanilla), Chart.js.
- **Backend:** Java 11+, Native Java HTTP Server (`com.sun.net.httpserver`), Data Access Object (DAO) Pattern.
- **Database:** Oracle Database 21c Express Edition (XE) / Enterprise, PL/SQL, Oracle JDBC Driver (`ojdbc8.jar`).
- **Version Control & Tools:** Git, GitHub, SQL*Plus / Oracle SQL Developer.

---

## 🏗️ System Architecture

The project follows a completely decoupled 3-tier enterprise architecture:

1. **Presentation Layer (Frontend):** Modern single-page web interface providing interactive dashboards and data tables.
2. **Business Logic Layer (Backend):** Java HTTP REST server handling API routes, request parsing, response serializations, and database connection pooling.
3. **Data Persistence Layer (Database):** Oracle 21c RDBMS enforcing schema constraints, relational integrity, PL/SQL routines, and automated audit triggers.

---

## 🔄 Data Flow

1. User interacts with UI (e.g., submits a trade or views portfolio).
2. Frontend JavaScript dispatches an asynchronous REST request (`fetch`) with JSON payload to the Java backend server (`localhost:8080`).
3. Java `MainApp` router delegates request handling to `StockDAO.java`.
4. `StockDAO` executes parameterized SQL/PLSQL commands over JDBC connection.
5. Oracle Database processes transaction, runs automated triggers, and updates tables.
6. Execution results are transformed into standard JSON responses and returned to UI.

---

## 📊 Dashboard Functionalities

- **Live Summary Cards:** Instant counters for total registered users, listed stocks, active portfolios, and executed trades.
- **Sector Allocation Chart:** Dynamic Chart.js doughnut chart breaking down investments by market sectors.
- **Cash Liquidity Bar:** Visual distribution chart showing user liquidity profiles.

---

## 👤 Investor Management

- Register new investors with risk profile indicators (`LOW`, `MEDIUM`, `HIGH`).
- Enforce strict unique constraints on investor email addresses and ID assignments.
- View detailed investor portfolio holdings and available cash balances.

---

## 📈 Stock Management

- Directory listing of stocks across major exchanges (`NSE`/`BSE`).
- Real-time client-side filter engine to search stocks by symbol, company name, or sector.
- Add or update stock price details with system-wide integrity checks.

---

## 💼 Portfolio Management

- Dynamic allocation tracking connecting investors to their owned equities.
- Real-time stock valuation updates calculating current asset holdings value.
- Automated cash balance calculations upon stock purchase or liquidation.

---

## 💳 Transaction Management

- Atomic market order execution (`BUY` / `SELL`).
- Immutable transaction history ledger capturing order types, execution prices, quantities, and timestamps.
- Validation checks preventing purchases exceeding available cash or sales exceeding owned stock quantity.

---

## ⭐ Watchlist Management

- Custom watchlists enabling investors to track potential equity targets.
- Dynamic addition and removal of ticker symbols linked to user profiles.

---

## 📜 Stock Price History

- Track price movements and historical evaluation records for listed equities.
- Maintain auditing transparency for market price fluctuations over time.

---

## 🔍 Portfolio Analysis

- Calculate total net worth dynamically using PL/SQL subroutines.
- Perform sector-wise risk assessment and cash liquidity analysis across portfolios.

---

## 🗄️ All Database Tables

1. **`USERS`:** Investor profile and account records.
2. **`STOCKS`:** Listed stock directory and pricing details.
3. **`PORTFOLIO`:** Equity holdings map linking investors and stocks.
4. **`TRANSACTIONS`:** Audit ledger for trade order records.
5. **`WATCHLIST`:** Investor target tracking watchlist.
6. **`SECTORS`:** Industry classification directory.
7. **`EXCHANGES`:** Market exchange master directory (NSE/BSE).
8. **`STOCK_PRICES_HISTORY`:** Historical price tracking table.
9. **`RISK_PROFILES`:** Risk category definition table.
10. **`USER_ACCOUNTS`:** Banking cash balance references.
11. **`ORDERS`:** Order queue state tracking.
12. **`ORDER_DETAILS`:** Detailed breakdown per trade order.
13. **`DIVIDENDS`:** Dividend distribution records.
14. **`PORTFOLIO_ANALYTICS`:** Pre-computed analytics summaries.
15. **`AUDIT_LOGS`:** System security audit trails.
16. **`SYSTEM_SETTINGS`:** Configuration key-value pairs.
17. **`NOTIFICATIONS`:** Investor system alerts.

---

## 🔗 Important Database Relationships

- **`USERS` to `PORTFOLIO`:** One-to-Many relationship (An investor can hold multiple portfolio items).
- **`STOCKS` to `PORTFOLIO`:** One-to-Many relationship (A stock can be held across multiple investor portfolios).
- **`USERS` to `TRANSACTIONS`:** One-to-Many relationship (An investor generates multiple trade transactions).

---

## 🔑 Primary / Foreign / Composite Keys

- **Primary Keys:** `user_id` in `USERS`, `stock_id` in `STOCKS`, `transaction_id` in `TRANSACTIONS`.
- **Foreign Keys:** `user_id` in `PORTFOLIO` references `USERS(user_id)`, `stock_id` in `PORTFOLIO` references `STOCKS(stock_id)`.
- **Composite Keys:** `(user_id, stock_id)` in `PORTFOLIO` and `WATCHLIST` ensuring duplicate prevention.

---

## ☕ Java Backend Explanation

The Java backend uses native HTTP server APIs without requiring heavy enterprise frameworks like Spring Boot:
- `MainApp.java`: Configures HTTP server context routes (`/api/users`, `/api/stocks`, `/api/transactions`) on port `8080`.
- Handlers parse incoming JSON payloads, forward operations to the DAO layer, and set standard HTTP headers (`Content-Type: application/json`).

---

## 🔌 JDBC Integration

`StockDAO.java` handles database communication using Oracle JDBC Driver (`ojdbc8.jar`):
- Establishes connection pools via `DBConnection.java`.
- Executes SQL operations using `PreparedStatement` to prevent SQL Injection attacks.
- Invokes PL/SQL functions and stored procedures using `CallableStatement`.

---

## 📡 API Endpoints

- `GET /api/dashboard` - Retrieve aggregated summary counts and chart data.
- `GET /api/users` - Fetch list of registered investors.
- `POST /api/users` - Register a new investor profile.
- `GET /api/stocks` - Retrieve listed equities directory.
- `POST /api/transactions` - Process atomic buy or sell trade order.

---

## 🖥️ Frontend Explanation

Built purely with vanilla HTML5, CSS3, and JavaScript:
- `index.html`: Defines single-page layout structure, navigation tab views, and UI modals.
- `styles.css`: Implements responsive CSS grid/flexbox layouts and custom variable themes.
- `app.js`: Manages tab switching logic, state handling, API data fetching, and Chart.js initialization.

---

## 📁 Project Folder Structure

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
│   ├── index.html                 # Dashboard layout & HTML5 UI
│   ├── styles.css                 # Custom responsive layout & component styling
│   └── app.js                     # Tab routing, state engine & API fetch logic
├── .gitignore                     # Git exclusion rules
└── README.md                      # Project documentation