# Stock Portfolio Analysis & Management System 

A 3-tier Stock Portfolio Analysis & Management System developed using a web frontend, Java JDBC backend, and PostgreSQL with TimescaleDB. The project demonstrates relational database design, portfolio management, stock transaction tracking, and time-series data management.

---

## 📑 Table of Contents

- [Project Overview](#-project-overview)
- [Objectives](#-objectives)
- [Technology Stack](#-technology-stack)
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
- [Database Tables](#-database-tables)
- [Important Database Relationships](#-important-database-relationships)
- [Primary, Foreign and Composite Keys](#-primary-foreign-and-composite-keys)
- [Java Backend Explanation](#-java-backend-explanation)
- [JDBC Integration](#-jdbc-integration)
- [API Endpoints](#-api-endpoints)
- [Frontend Explanation](#-frontend-explanation)
- [Project Folder Structure](#-project-folder-structure)
- [Setup and Run Instructions](#-setup-and-run-instructions)
- [Demonstration Flow](#-demonstration-flow)
- [Team Responsibilities](#-team-responsibilities)
- [DBMS Concepts Demonstrated](#-dbms-concepts-demonstrated)
- [Current Implementation Notes](#-current-implementation-notes)
- [Future Enhancements](#-future-enhancements)
- [Security Note](#-security-note)
- [Repository Information](#-repository-information)
- [Conclusion](#-conclusion)

---

## 📌 Project Overview

The Stock Portfolio Analysis & Management System helps manage user information, portfolios, stock holdings, transactions, and stock price history.

DA3 uses PostgreSQL with TimescaleDB to store relational data and support time-series stock price management.

**Why TimescaleDB?**

TimescaleDB is used in DA3 because the project stores stock price history, which contains time-based data that changes continuously. It is built on PostgreSQL and provides specialized features for efficiently storing, querying, and analyzing time-series data.

The STOCK_PRICE_HISTORY table stores historical stock prices along with their timestamps. TimescaleDB converts this table into a hypertable using the create_hypertable() function, which organizes time-series data into time-based chunks.

Key reasons for using TimescaleDB:

- Time-Series Data Management: Efficiently handles historical stock prices recorded over time.
- Faster Time-Based Queries: Helps retrieve stock prices for specific dates or time ranges efficiently.
- Hypertable Support: Organizes historical price data into time-based chunks.
- PostgreSQL Compatibility: Supports standard PostgreSQL tables, SQL queries, constraints, and relationships.
- Historical Price Analysis: Provides a suitable foundation for analyzing stock price trends over time.

## 🎯 Objectives

- Manage portfolios, holdings, and stock information.
- Maintain stock transaction and price history records.
- Implement relational database concepts using PostgreSQL.
- Use TimescaleDB for time-series data management.
- Connect the Java backend to the database using JDBC.

## 💻 Technology Stack

| Component | Technology |
|---|---|
| Frontend | HTML5, CSS3, JavaScript |
| Charts | Chart.js, if enabled |
| Backend | Java |
| HTTP Server | Java Native HTTP Server, if used |
| Database | PostgreSQL with TimescaleDB |
| Database Connectivity | JDBC |
| JDBC Driver | PostgreSQL JDBC Driver |
| Database Tools | `psql` / pgAdmin |
| Code Editor | Visual Studio Code |
| Version Control | Git and GitHub |

## 🏗️ System Architecture

The project follows a three-tier architecture.

### 1. Presentation Layer

The frontend provides the interface for displaying and interacting with application data using HTML, CSS, and JavaScript.

### 2. Business Logic Layer

The Java backend processes application requests and communicates with the database through JDBC.

### 3. Data Persistence Layer

PostgreSQL stores the relational data, while TimescaleDB provides time-series functionality for historical stock prices.

### Architecture Diagram

```text
+----------------------------------+
|       Presentation Layer         |
|     HTML, CSS and JavaScript     |
+----------------------------------+
                |
                | HTTP Requests
                v
+----------------------------------+
|       Business Logic Layer       |
|          Java Backend            |
|          JDBC Connectivity       |
+----------------------------------+
                |
                | SQL / JDBC
                v
+----------------------------------+
|        Data Persistence Layer    |
|   PostgreSQL + TimescaleDB       |
| Relational and Time-Series Data  |
+----------------------------------+
```

## 🔄 Data Flow

1. The user interacts with the frontend.
2. The frontend sends requests to the Java backend.
3. The backend executes SQL operations through JDBC.
4. PostgreSQL/TimescaleDB processes the database operations.
5. The backend returns the results to the frontend.
6. The frontend displays the returned information.

The actual operations depend on the implemented backend logic.

## 📊 Dashboard Functionalities

The dashboard can provide a consolidated view of stock portfolio information, including:

- Stock details and prices.
- Portfolio and holding information.
- Transaction history.
- Historical stock price data.
- Portfolio valuation and charts, if implemented.

## 👤 Investor Management

The `APP_USER` table stores general user information, while `INVESTOR` stores investor-specific details such as risk profile.

The schema also includes `ADVISOR` and `ADMIN` tables for advisor and administrator information.

## 📈 Stock Management

The `STOCK` table stores stock identifiers, symbols, names, exchange information, sector, current price, and last-updated timestamp.

The stock records are associated with portfolio holdings, transactions, and historical price records through foreign keys.

## 💼 Portfolio Management

The `PORTFOLIO` table stores portfolio ownership, portfolio name, and cash balance.

The `HOLDING` table records the stocks associated with portfolios, including total quantity and average price.

## 💳 Transaction Management

The `TRANSACTIONS` table stores transaction identifiers, associated portfolios and stocks, quantities, transaction fees, and transaction dates.

The `BUY_TRANSACTION` and `SELL_TRANSACTION` tables store buy- and sell-specific brokerage information.

## ⭐ Watchlist Management

The schema contains three watchlist-related tables:

- `WATCHLIST_ITEM` — Stores user-associated watchlist identifiers and added dates.
- `WATCHLIST_STOCK` — Associates stocks with user and watchlist identifiers.
- `ADVISOR_WATCHLIST` — Stores advisor-associated watchlist tracking information.

## 📜 Stock Price History

The `STOCK_PRICE_HISTORY` table stores historical stock prices using the following fields:

- `PRICE_ID` — Price record identifier.
- `STOCK_ID` — Associated stock.
- `PRICE_DATE` — Timestamp of the price record.
- `OPENING_PRICE` — Opening price.
- `CLOSING_PRICE` — Closing price.
- `HIGH_PRICE` — Highest recorded price.
- `LOW_PRICE` — Lowest recorded price.

The `PRICE_DATE` column uses PostgreSQL's `TIMESTAMPTZ` type. The table is converted into a TimescaleDB hypertable partitioned by `PRICE_DATE`, supporting time-series data management.

## 🔍 Portfolio Analysis

Portfolio analysis can use data from `PORTFOLIO`, `HOLDING`, `STOCK`, and `TRANSACTIONS`.

Historical stock price records can support price trend analysis. Calculated returns, profit/loss values, and visual analytics depend on the implemented application logic.

## 🗄️ Database Tables

The supplied `schema_postgreSQL.sql` defines 17 tables.

| No. | Table Name | Purpose |
|---:|---|---|
| 1 | `APP_USER` | User details and account information |
| 2 | `INVESTOR` | Investor details and risk profile |
| 3 | `ADVISOR` | Advisor information and expertise |
| 4 | `ADMIN` | Administrator details and permissions |
| 5 | `CLIENT` | Client identification and names |
| 6 | `INDIVIDUAL` | Individual client records |
| 7 | `INSTITUTIONAL` | Institutional client records |
| 8 | `PORTFOLIO` | Portfolio ownership, names, and cash balances |
| 9 | `STOCK` | Stock details and current prices |
| 10 | `STOCK_PRICE_HISTORY` | Historical stock price time-series data |
| 11 | `HOLDING` | Stock quantities and average prices in portfolios |
| 12 | `TRANSACTIONS` | Stock transaction records |
| 13 | `BUY_TRANSACTION` | Buy-specific brokerage information |
| 14 | `SELL_TRANSACTION` | Sell-specific brokerage information |
| 15 | `WATCHLIST_ITEM` | User-associated watchlist identifiers |
| 16 | `WATCHLIST_STOCK` | Stocks associated with watchlists |
| 17 | `ADVISOR_WATCHLIST` | Advisor-associated watchlist tracking |

## 🔗 Important Database Relationships

The following relationships are explicitly defined through foreign keys in the schema.

| Parent Table | Child Table | Relationship |
|---|---|---|
| `APP_USER` | `INVESTOR` | Investor references an application user |
| `APP_USER` | `ADVISOR` | Advisor references an application user |
| `APP_USER` | `ADMIN` | Administrator references an application user |
| `APP_USER` | `PORTFOLIO` | Portfolio references its owner |
| `APP_USER` | `WATCHLIST_ITEM` | Watchlist item references a user |
| `CLIENT` | `INDIVIDUAL` | Individual record references a client |
| `CLIENT` | `INSTITUTIONAL` | Institutional record references a client |
| `PORTFOLIO` | `HOLDING` | Holding references a portfolio |
| `PORTFOLIO` | `TRANSACTIONS` | Transaction references a portfolio |
| `STOCK` | `HOLDING` | Holding references a stock |
| `STOCK` | `TRANSACTIONS` | Transaction references a stock |
| `STOCK` | `STOCK_PRICE_HISTORY` | Price history references a stock |
| `STOCK` | `WATCHLIST_STOCK` | Watchlist stock entry references a stock |
| `TRANSACTIONS` | `BUY_TRANSACTION` | Buy record references a transaction |
| `TRANSACTIONS` | `SELL_TRANSACTION` | Sell record references a transaction |
| `ADVISOR` | `ADVISOR_WATCHLIST` | Advisor watchlist record references an advisor |

The schema specifies `ON DELETE CASCADE` for the declared foreign keys.

**Note:** `WATCHLIST_STOCK` does not declare foreign keys linking its `USER_ID` and `WATCH_ID` to `WATCHLIST_ITEM`. Similarly, `ADVISOR_WATCHLIST` does not declare foreign keys for its `USER_ID` and `WATCH_ID` columns.

## 🔑 Primary, Foreign and Composite Keys

### Primary Keys

Primary keys uniquely identify records in their respective tables.

Examples include:

- `APP_USER`: `USER_ID`
- `PORTFOLIO`: `PORTFOLIO_ID`
- `STOCK`: `STOCK_ID`
- `HOLDING`: `HOLDING_ID`
- `TRANSACTIONS`: `TRANS_ID`

### Composite Primary Keys

The schema defines the following composite primary keys:

- `STOCK_PRICE_HISTORY`: `(PRICE_ID, PRICE_DATE)`
- `WATCHLIST_ITEM`: `(USER_ID, WATCH_ID)`
- `WATCHLIST_STOCK`: `(USER_ID, WATCH_ID, STOCK_ID)`
- `ADVISOR_WATCHLIST`: `(ADVISOR_ID, USER_ID, WATCH_ID)`

The `STOCK_PRICE_HISTORY` primary key includes the partitioning column `PRICE_DATE`, which is important for a TimescaleDB hypertable.

### Foreign Keys

Foreign keys connect related tables and maintain referential integrity.

Examples include:

- `PORTFOLIO.USER_ID` references `APP_USER.USER_ID`.
- `HOLDING.PORTFOLIO_ID` references `PORTFOLIO.PORTFOLIO_ID`.
- `HOLDING.STOCK_ID` references `STOCK.STOCK_ID`.
- `TRANSACTIONS.PORTFOLIO_ID` references `PORTFOLIO.PORTFOLIO_ID`.
- `TRANSACTIONS.STOCK_ID` references `STOCK.STOCK_ID`.
- `STOCK_PRICE_HISTORY.STOCK_ID` references `STOCK.STOCK_ID`.

## ☕ Java Backend Explanation

The Java backend acts as the application layer between the frontend and database.

Its responsibilities may include:

- Receiving frontend requests.
- Processing application operations.
- Executing SQL queries through JDBC.
- Returning results to the frontend.

## 🔌 JDBC Integration

Java Database Connectivity (JDBC) allows Java applications to communicate with PostgreSQL/TimescaleDB.

The general workflow is:

1. Configure the PostgreSQL JDBC driver.
2. Set the database URL, username, and password.
3. Establish a database connection.
4. Execute SQL statements.
5. Process query results.
6. Close database resources.

The connection URL follows this format:

```text
jdbc:postgresql://localhost:5432/database_name
```

Replace `database_name` with the actual database name.

`PreparedStatement` can be used for parameterized queries and safer handling of user-supplied values.

## 📡 API Endpoints

| Method | Endpoint | Intended Purpose |
|---|---|---|
| GET | `/api/dashboard` | Retrieve dashboard information |
| GET | `/api/users` | Retrieve user information |
| POST | `/api/users` | Create a user record |
| GET | `/api/stocks` | Retrieve stock information |
| POST | `/api/transactions` | Submit a transaction request |

The supported routes and CRUD operations depend on the backend implementation.

## 🖥️ Frontend Explanation

The frontend uses HTML, CSS, and JavaScript to display stock and portfolio information and handle user interactions.

If the frontend remains unchanged from DA2, it can continue communicating with the Java backend through the existing API routes, provided the backend is configured to use the DA3 database.

## 📁 Project Folder Structure

```text
DA3_Timescale_Project/
│
├── backend/
│   ├── Java backend files
│   └── PostgreSQL JDBC configuration
│
├── database/
│   └── schema_postgreSQL.sql
│   └── sample_data_postgreSQL.sql
|
├── frontend/
│   ├── index.html
│   ├── styles.css
│   └── app.js
│
└── README.md
```


## ⚙️ Setup and Run Instructions

### Prerequisites

- Java Development Kit (JDK).
- PostgreSQL with TimescaleDB installed and configured.
- PostgreSQL JDBC driver.
- `psql` or pgAdmin.
- Visual Studio Code or another code editor.
- A web browser.

### 1. Database Setup

1. Start the PostgreSQL service with TimescaleDB installed.
2. Create or select the target database.
3. Connect to the database using `psql` or pgAdmin.
4. Execute the schema file.
5. Verify that the tables and hypertable were created successfully.

Example command, run from the directory containing the `database` folder:

```bash
psql -U postgres -d database_name -f database/schema_postgreSQL.sql
```

Replace `postgres` and `database_name` with the correct database credentials and name.

The schema enables TimescaleDB using:

```sql
CREATE EXTENSION IF NOT EXISTS timescaledb;
```

It then creates `STOCK_PRICE_HISTORY` as a hypertable using `PRICE_DATE` as its time-partitioning column.

```sql
SELECT create_hypertable('stock_price_history', 'price_date');
```

The database user must have the required permissions, and TimescaleDB must be installed and available on the PostgreSQL server.

### 2. Backend Setup

1. Navigate to the backend directory.
2. Configure the PostgreSQL JDBC driver.
3. Update the database URL, username, and password.
4. Compile and run the Java application using the project's actual classpath and package structure.

Ensure the backend uses the PostgreSQL JDBC driver instead of the Oracle JDBC driver.

### 3. Frontend Setup

1. Open the frontend directory.
2. Launch the frontend using the project's existing method.
3. Verify that the API base URL points to the running Java backend.
4. Test the implemented features.

The frontend, backend, and database must all be running and configured correctly for database operations to work.

## 🎓 Demonstration Flow

1. Introduce the Stock Portfolio Analysis & Management System.
2. Explain the three-tier architecture.
3. Show the PostgreSQL/TimescaleDB database.
4. Demonstrate the 17 database tables.
5. Explain primary, foreign, and composite keys.
6. Show the `STOCK_PRICE_HISTORY` table and its hypertable configuration.
7. Explain how `PRICE_DATE` supports time-series partitioning.
8. Demonstrate JDBC connectivity.
9. Present the available frontend features.
10. Explain relational database and time-series concepts.

## 👥 Team Responsibilities

- **Sankhya Bhatia (25BCE1362)** — Backend Integration
- **Vaibhavi Gupta (25BCE1386)** — Frontend
- **Arya Rajendra Yadav (25BCE1374)** — Database

## ⚡ DBMS Concepts Demonstrated

### 1. Relational Database Design

The schema separates users, investors, advisors, clients, portfolios, stocks, holdings, and transactions into related tables.

### 2. Primary and Foreign Keys

Primary keys identify records uniquely, while foreign keys establish relationships and enforce referential integrity.

### 3. Composite Keys

Multiple-column primary keys are used in the historical price and watchlist-related tables.

### 4. Constraints

The schema uses `PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, and `NOT NULL` constraints, along with default values.

### 5. Cascading Deletes

Declared foreign keys use `ON DELETE CASCADE` to remove dependent records when a referenced parent record is deleted.

### 6. JDBC Integration

Java communicates with PostgreSQL/TimescaleDB through JDBC to execute database operations.

### 7. Time-Series Management

TimescaleDB extends PostgreSQL with hypertables for time-series data. In this project, `STOCK_PRICE_HISTORY` is configured as a hypertable partitioned by `PRICE_DATE`.

## 📝 Current Implementation Notes

- The supplied schema defines 17 tables.
- PostgreSQL uses `NUMERIC` for monetary and quantity fields.
- `STOCK_PRICE_HISTORY.PRICE_DATE` uses `TIMESTAMPTZ`.
- `STOCK_PRICE_HISTORY` has the composite primary key `(PRICE_ID, PRICE_DATE)`.
- `STOCK_PRICE_HISTORY` is converted into a TimescaleDB hypertable partitioned by `PRICE_DATE`.
- The schema enables the TimescaleDB extension.
- The Java backend must use PostgreSQL JDBC connection settings.
- The supplied schema does not define separate `USERS`, `STOCKS`, `WATCHLIST`, `DIVIDENDS`, or `PORTFOLIO_ANALYTICS` tables.
- API availability and frontend functionality depend on the actual application code.

## 🚀 Future Enhancements

- Add advanced portfolio performance and profit/loss analytics.
- Use time-series queries for historical stock price analysis.
- Integrate live stock market data.
- Improve dashboard visualizations and reporting.
- Strengthen authentication and role-based access control.

## 🔒 Security Note

Do not commit database credentials to the repository. Use secure configuration, appropriate database permissions, and parameterized SQL queries to protect database access.

## 🌐 Repository Information

- **Repository Name:** `stock-portfolio-analysis-system`
- **GitHub Owner:** `sankhyabhatia1612`
- **Repository URL:** [stock-portfolio-analysis-system](https://github.com/sankhyabhatia1612/stock-portfolio-analysis-system)
- **Project:** DA3 — PostgreSQL and TimescaleDB implementation

## 📌 Conclusion

DA3 extends the stock portfolio application by using PostgreSQL with TimescaleDB for relational and time-series data management. The schema retains portfolio and transaction relationships while configuring historical stock prices as a hypertable for time-series workloads.
