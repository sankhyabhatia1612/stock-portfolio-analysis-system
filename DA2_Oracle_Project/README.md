# Stock Portfolio Analysis & Management System 

A 3-tier stock portfolio analysis and management application developed using a web frontend, Java JDBC backend, and Oracle Database 21c. The project demonstrates relational database design, SQL operations, database connectivity, portfolio management, and stock transaction tracking.

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

---

## 📌 Project Overview

The Stock Portfolio Analysis & Management System is a database-driven application developed using Java and Oracle Database. It helps users manage portfolios, track stock holdings, record transactions, and monitor stock prices through a centralized system.

The database schema models different user roles, clients, portfolios, stocks, holdings, transactions, and watchlist information using primary keys, foreign keys, constraints, and relational associations.

---

## 🎯 Objectives

- To manage stock portfolios and holdings efficiently.
- To record and track buy/sell transactions.
- To maintain stock information and price history.
- To implement database concepts such as relationships, constraints, and SQL operations.

---

## 💻 Technology Stack

| Component | Technology |
|---|---|
| Frontend | HTML5, CSS3, JavaScript |
| Charts | Chart.js, if enabled in the frontend |
| Backend | Java |
| HTTP Server | Java Native HTTP Server (`com.sun.net.httpserver`), if used by the implementation |
| Database | Oracle Database 21c XE |
| Database Connectivity | Java Database Connectivity (JDBC) |
| JDBC Driver | Oracle JDBC driver |
| SQL Tools | SQL*Plus / Oracle SQL Developer |
| Code Editor | Visual Studio Code |
| Version Control | Git and GitHub |

---

## 🏗️ System Architecture

The project follows a three-tier architecture.

### 1. Presentation Layer

The frontend provides the user interface for displaying and interacting with application data. It uses HTML, CSS, and JavaScript.

### 2. Business Logic Layer

The Java backend handles application requests and communicates with the Oracle database using JDBC. Its exact responsibilities depend on the implemented Java classes and API routes.

### 3. Data Persistence Layer

Oracle Database 21c stores the application's relational data, including users, investors, portfolios, stocks, holdings, transactions, and watchlist records.

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
|       Oracle Database 21c        |
| Tables, Keys and Constraints     |
+----------------------------------+
```

---

## 🔄 Data Flow

The general data flow of the application is:

1. The user interacts with the frontend.
2. The frontend sends a request to the Java backend when the relevant API is implemented.
3. The backend processes the request and communicates with Oracle Database through JDBC.
4. Oracle executes the corresponding SQL operation.
5. The backend returns the result to the frontend.
6. The frontend displays the returned information.

For operations that modify records, the database changes depend on the SQL statements and transaction-handling logic implemented by the backend.

---

## 📊 Dashboard Functionalities

The dashboard can provide a consolidated view of stock portfolio information.

Possible dashboard components include:

- Summary of registered users.
- Stock directory and stock prices.
- Portfolio and holding information.
- Transaction history.
- Historical stock price information.
- Portfolio valuation and investment distribution charts.

The availability of individual cards, charts, and calculations depends on the current frontend and backend implementation.

---

## 👤 Investor Management

The database separates general user information from investor-specific information.

The `APP_USER` table stores general user details, while the `INVESTOR` table stores investor-specific information.

The schema supports:

- User identification and personal information.
- Email addresses with a unique constraint.
- Account status and creation timestamp.
- Investor risk profile information.
- A foreign key connecting investor records to application users.

The schema defines the investor's risk profile as a column in `INVESTOR`. It does not define a separate `RISK_PROFILES` table.

---

## 📈 Stock Management

The `STOCK` table stores information about listed equities.

Its columns include:

- `STOCK_ID` — Primary key.
- `STOCK_SYMBOL` — Unique stock symbol.
- `STOCK_NAME` — Stock name.
- `EXCHANGE` — Exchange information.
- `SECTOR` — Sector classification.
- `CURRENT_PRICE` — Current recorded price.
- `LAST_UPDATED` — Timestamp of the last update.

These fields support maintaining a stock directory and associating stocks with portfolio holdings, transactions, and historical prices.

The schema stores exchange and sector information directly in `STOCK`; it does not define separate `EXCHANGES` or `SECTORS` tables.

---

## 💼 Portfolio Management

The `PORTFOLIO` table stores portfolio information.

Important columns include:

- `PORTFOLIO_ID` — Unique portfolio identifier.
- `USER_ID` — References the portfolio owner in `APP_USER`.
- `PORTFOLIO_NAME` — Portfolio name.
- `CASH_BALANCE` — Portfolio cash balance.

The `HOLDING` table stores stock positions associated with portfolios.

Its important columns include:

- `HOLDING_ID` — Unique holding identifier.
- `PORTFOLIO_ID` — References `PORTFOLIO`.
- `STOCK_ID` — References `STOCK`.
- `TOTAL_QUANTITY` — Quantity held.
- `AVG_PRICE` — Average recorded price.

Together, these tables represent portfolios and the stocks associated with them.

---

## 💳 Transaction Management

The `TRANSACTIONS` table records stock transaction information.

Its columns include:

- `TRANS_ID` — Transaction identifier.
- `PORTFOLIO_ID` — Associated portfolio.
- `STOCK_ID` — Associated stock.
- `QUANTITY` — Transaction quantity.
- `TRANS_FEE` — Transaction fee.
- `TRANS_DATE` — Transaction date.

The schema also defines two transaction-related tables:

- `BUY_TRANSACTION` — Stores buy-specific brokerage information.
- `SELL_TRANSACTION` — Stores sell-specific brokerage information.

Both tables reference `TRANSACTIONS` through `TRANS_ID`.


---

## ⭐ Watchlist Management

The schema defines three watchlist-related tables.

### `WATCHLIST_ITEM`

Stores a user's watchlist identifier and the date it was added.

The primary key is the combination of `USER_ID` and `WATCH_ID`.

### `WATCHLIST_STOCK`

Associates a stock with a watchlist identifier and user identifier. It also contains a `REFERENCE_NO` field.

Its composite primary key consists of:

`(USER_ID, WATCH_ID, STOCK_ID)`

### `ADVISOR_WATCHLIST`

Stores advisor-associated watchlist tracking information, including `TRACKED_AT`.

Its composite primary key consists of:

`(ADVISOR_ID, USER_ID, WATCH_ID)`

These tables establish watchlist-related associations. Additional application logic may be needed to enforce relationships that are not explicitly defined by foreign keys in the supplied schema.

---

## 📜 Stock Price History

The `STOCK_PRICE_HISTORY` table stores historical stock price records.

Its columns include:

- `PRICE_ID` — Primary key.
- `STOCK_ID` — References the associated stock.
- `PRICE_DATE` — Date of the price record.
- `OPENING_PRICE` — Opening price.
- `CLOSING_PRICE` — Closing price.
- `HIGH_PRICE` — Highest recorded price.
- `LOW_PRICE` — Lowest recorded price.

The table allows historical price information to be associated with stocks.

Historical comparisons, charts, and price trend calculations can be implemented using these records.

---

## 🔍 Portfolio Analysis

Portfolio analysis can use the data stored in `PORTFOLIO`, `HOLDING`, `STOCK`, and `TRANSACTIONS`.

The system maintains portfolio holdings, quantities, average purchase prices, cash balances, and transaction records. These details can support portfolio monitoring and analysis. Any calculated returns, profit/loss values, or visual analytics depend on the implemented application logic.

---

## 🗄️ Database Tables

The supplied `schema.sql` defines the following 17 tables.

| No. | Table Name | Purpose |
|---:|---|---|
| 1 | `APP_USER` | General user information and account details |
| 2 | `INVESTOR` | Investor-specific details and risk profile |
| 3 | `ADVISOR` | Advisor information and expertise |
| 4 | `ADMIN` | Administrator information and permissions |
| 5 | `CLIENT` | Client identification and names |
| 6 | `INDIVIDUAL` | Individual client records |
| 7 | `INSTITUTIONAL` | Institutional client records |
| 8 | `PORTFOLIO` | Portfolio names, owners, and cash balances |
| 9 | `STOCK` | Stock symbols, names, exchanges, sectors, and prices |
| 10 | `STOCK_PRICE_HISTORY` | Historical stock price records |
| 11 | `HOLDING` | Stock quantities and average prices in portfolios |
| 12 | `TRANSACTIONS` | Stock transaction details |
| 13 | `BUY_TRANSACTION` | Buy-specific brokerage information |
| 14 | `SELL_TRANSACTION` | Sell-specific brokerage information |
| 15 | `WATCHLIST_ITEM` | User-associated watchlist identifiers |
| 16 | `WATCHLIST_STOCK` | Stocks associated with watchlist identifiers |
| 17 | `ADVISOR_WATCHLIST` | Advisor-associated watchlist tracking records |


---

## 🔗 Important Database Relationships

The following relationships are explicitly defined through foreign keys in the supplied schema.

| Parent Table | Child Table | Relationship |
|---|---|---|
| `APP_USER` | `INVESTOR` | Investor references an application user |
| `APP_USER` | `ADVISOR` | Advisor references an application user |
| `APP_USER` | `ADMIN` | Administrator references an application user |
| `APP_USER` | `PORTFOLIO` | Portfolio references its owner |
| `CLIENT` | `INDIVIDUAL` | Individual record references a client |
| `CLIENT` | `INSTITUTIONAL` | Institutional record references a client |
| `PORTFOLIO` | `HOLDING` | Holding references a portfolio |
| `STOCK` | `HOLDING` | Holding references a stock |
| `PORTFOLIO` | `TRANSACTIONS` | Transaction references a portfolio |
| `STOCK` | `TRANSACTIONS` | Transaction references a stock |
| `STOCK` | `STOCK_PRICE_HISTORY` | Historical price record references a stock |
| `TRANSACTIONS` | `BUY_TRANSACTION` | Buy record references a transaction |
| `TRANSACTIONS` | `SELL_TRANSACTION` | Sell record references a transaction |
| `APP_USER` | `WATCHLIST_ITEM` | Watchlist item references a user |
| `STOCK` | `WATCHLIST_STOCK` | Watchlist stock entry references a stock |
| `ADVISOR` | `ADVISOR_WATCHLIST` | Advisor watchlist record references an advisor |

The schema also specifies `ON DELETE CASCADE` for its defined foreign keys. This means deleting a referenced parent record can automatically delete related child records.

---

## 🔑 Primary, Foreign and Composite Keys

### Primary Keys

The following columns are explicitly declared as primary keys:

| Table | Primary Key |
|---|---|
| `APP_USER` | `USER_ID` |
| `INVESTOR` | `USER_ID` |
| `ADVISOR` | `ADVISOR_ID` |
| `ADMIN` | `ADMIN_ID` |
| `CLIENT` | `CLIENT_ID` |
| `INDIVIDUAL` | `IND_ID` |
| `INSTITUTIONAL` | `INS_ID` |
| `PORTFOLIO` | `PORTFOLIO_ID` |
| `STOCK` | `STOCK_ID` |
| `STOCK_PRICE_HISTORY` | `PRICE_ID` |
| `HOLDING` | `HOLDING_ID` |
| `TRANSACTIONS` | `TRANS_ID` |
| `BUY_TRANSACTION` | `BUY_ID` |
| `SELL_TRANSACTION` | `SELL_ID` |

### Composite Primary Keys

The following tables use multiple columns as their primary keys:

- `WATCHLIST_ITEM`: `(USER_ID, WATCH_ID)`
- `WATCHLIST_STOCK`: `(USER_ID, WATCH_ID, STOCK_ID)`
- `ADVISOR_WATCHLIST`: `(ADVISOR_ID, USER_ID, WATCH_ID)`

Composite primary keys help prevent duplicate combinations of the specified column values.

### Foreign Keys

Examples of foreign keys defined in the schema include:

- `INVESTOR.USER_ID` references `APP_USER.USER_ID`.
- `PORTFOLIO.USER_ID` references `APP_USER.USER_ID`.
- `HOLDING.PORTFOLIO_ID` references `PORTFOLIO.PORTFOLIO_ID`.
- `HOLDING.STOCK_ID` references `STOCK.STOCK_ID`.
- `TRANSACTIONS.PORTFOLIO_ID` references `PORTFOLIO.PORTFOLIO_ID`.
- `TRANSACTIONS.STOCK_ID` references `STOCK.STOCK_ID`.
- `STOCK_PRICE_HISTORY.STOCK_ID` references `STOCK.STOCK_ID`.
- `BUY_TRANSACTION.TRANS_ID` references `TRANSACTIONS.TRANS_ID`.
- `SELL_TRANSACTION.TRANS_ID` references `TRANSACTIONS.TRANS_ID`.

The other foreign keys are listed in the database relationships section.

---

## ☕ Java Backend Explanation

The Java backend provides the application layer between the frontend and Oracle Database.

Depending on the actual implementation, its responsibilities may include:

- Receiving frontend requests.
- Processing application operations.
- Establishing JDBC connections.
- Executing SQL statements.
- Returning database results to the frontend.

If the backend uses the following classes, their responsibilities are:

- `MainApp.java` — Application entry point and HTTP server configuration.
- `DBConnection.java` — Database connection configuration.
- `DatabaseConnectionTest.java` — Testing database connectivity.
- `StockDAO.java` — Data access operations involving SQL queries.

These descriptions should be adjusted if the current Java files use different class names or responsibilities.

---

## 🔌 JDBC Integration

Java Database Connectivity (JDBC) allows Java applications to communicate with relational databases.

The general connection workflow is:

1. Load or configure the Oracle JDBC driver as required.
2. Configure the Oracle database connection URL.
3. Supply the database username and password.
4. Establish a database connection.
5. Execute SQL statements.
6. Process query results.
7. Close database resources.

`PreparedStatement` can be used for parameterized SQL statements. It helps prevent SQL injection when user-supplied values are passed as parameters instead of being concatenated into SQL strings.

`CallableStatement` can be used to invoke stored procedures and functions if they are implemented in the database.

The actual database operations available depend on the Java source code and database objects.

---

## 📡 API Endpoints

The following are example endpoints mentioned in the original project documentation. Verify them against the current Java backend before treating them as the complete implemented API.

| Method | Endpoint | Intended Purpose |
|---|---|---|
| GET | `/api/dashboard` | Retrieve dashboard information |
| GET | `/api/users` | Retrieve user information |
| POST | `/api/users` | Create a user record |
| GET | `/api/stocks` | Retrieve stock information |
| POST | `/api/transactions` | Submit a transaction request |

The API route names do not have to match Oracle table names. For example, `/api/users` may be used as a route even when the corresponding database table is `APP_USER`.

The exact supported methods, response formats, validation rules, and CRUD operations must be confirmed from the backend implementation.

---

## 🖥️ Frontend Explanation

The frontend is intended to provide a web-based interface for interacting with stock portfolio information.

If the project contains the following files, their typical responsibilities are:

- `index.html` — Defines the page structure and interface.
- `styles.css` — Defines visual styling, layouts, and responsive behaviour.
- `app.js` — Implements frontend interactions and API requests.

JavaScript can use the Fetch API to send requests to the Java backend and display the returned data.

The frontend should communicate with the API routes configured in the backend.

---

## 📁 Project Folder Structure

The DA2 folder is intended to contain the frontend, backend, database scripts, and this README.

```text
DA2_Oracle_Project/
│
├── backend/
│   ├── DBConnection.java
│   ├── DatabaseConnectionTest.java
│   ├── MainApp.java
│   ├── StockDAO.java
│   └── ojdbc8.jar
│
├── database/
│   ├── schema.sql
│   └── sample_data.sql
│
├── frontend/
│   ├── index.html
│   ├── styles.css
│   └── app.js
│
└── README.md
```

This is the intended structure based on the original project documentation. If your actual folders or filenames differ, update this section to match the repository.

---

## ⚙️ Setup and Run Instructions

### Prerequisites

Install or configure the following:

- Oracle Database 21c XE.
- Java Development Kit (JDK).
- Oracle SQL*Plus or Oracle SQL Developer.
- A compatible Oracle JDBC driver.
- A web browser.
- Visual Studio Code or another code editor.

### 1. Database Setup

1. Start the Oracle database service.
2. Open SQL*Plus or Oracle SQL Developer.
3. Connect to the appropriate Oracle database or pluggable database.
4. Execute the schema script.

If you are using SQL*Plus and the script is located at `DA2_Oracle_Project/database/schema.sql`, navigate to the DA2 project directory and execute:

```sql
@database/schema.sql
```

If a `sample_data.sql` file exists and is compatible with the schema, execute:

```sql
@database/sample_data.sql
```

**Note:** The supplied `schema.sql` defines the database tables. A separate sample-data script was mentioned in the original documentation, but its contents have not been verified here.

Ensure that the database user has the necessary permissions to create tables and constraints.

### 2. Backend Setup

Open a terminal in the DA2 project directory.

Navigate to the backend folder:

```bash
cd backend
```

Compile the Java files using the JDBC driver.

On Windows:

```cmd
javac -cp ".;ojdbc8.jar" *.java
```

On Linux or macOS:

```bash
javac -cp ".:ojdbc8.jar" *.java
```

Run the Java application using the actual main class and package configuration in your source code.

If `MainApp` is in the default package:

Windows:

```cmd
java -cp ".;ojdbc8.jar" MainApp
```

Linux or macOS:

```bash
java -cp ".:ojdbc8.jar" MainApp
```

If your Java files declare a package, use the fully qualified main class name and compile from the appropriate directory instead.

The server address and port depend on the configuration in the Java application.

### 3. Frontend Setup

1. Open the `frontend` directory.
2. Locate `index.html`.
3. Launch it in a browser or use a local development server.
4. Confirm that the frontend API base URL matches the running backend.
5. Test the features that are implemented.

Opening the frontend alone will not guarantee database connectivity. The Java backend and Oracle database must also be running, and the API requests must be configured correctly.

---

## 🎓 Suggested Demonstration Flow

The following flow can be used to present the project, provided the corresponding functionality is implemented.

1. **Project Introduction:** Explain the purpose of the stock portfolio application.
2. **Architecture:** Present the frontend, Java backend, and Oracle database layers.
3. **Database Schema:** Show the 17 tables defined in `schema.sql`.
4. **Key Relationships:** Explain primary keys, foreign keys, and composite keys.
5. **User and Investor Data:** Demonstrate the relationship between `APP_USER` and `INVESTOR`.
6. **Stock Data:** Show the `STOCK` table and its price-related columns.
7. **Portfolio and Holdings:** Explain how `PORTFOLIO` and `HOLDING` represent portfolio positions.
8. **Transactions:** Demonstrate the relationships among `TRANSACTIONS`, `BUY_TRANSACTION`, and `SELL_TRANSACTION`.
9. **Watchlists:** Explain the roles of `WATCHLIST_ITEM`, `WATCHLIST_STOCK`, and `ADVISOR_WATCHLIST`.
10. **JDBC Connectivity:** Show the Java database connection and a successful query.
11. **Frontend Demonstration:** Demonstrate the available UI features and database operations.
12. **DBMS Concepts:** Explain constraints, referential integrity, and relational design.

---

## 👥 Team Responsibilities

- **Sankhya Bhatia (25BCE1362)** — Backend Integration
- **Vaibhavi Gupta (25BCE1386)** — Frontend
- **Arya Rajendra Yadav (25BCE1374)** — Database

---

## ⚡ DBMS Concepts Demonstrated

### 1. Relational Database Design

The project separates user information, investor details, advisor details, client information, portfolios, stocks, holdings, and transactions into related tables.

### 2. Primary Keys

Primary keys uniquely identify records in their respective tables.

Examples include `USER_ID`, `PORTFOLIO_ID`, `STOCK_ID`, and `TRANS_ID`.

### 3. Foreign Keys

Foreign keys establish relationships between tables and help maintain referential integrity.

### 4. Composite Keys

The watchlist-related tables use composite primary keys to identify records using combinations of columns.

### 5. Constraints

The schema includes constraints such as:

- `PRIMARY KEY`
- `FOREIGN KEY`
- `UNIQUE`
- `NOT NULL`

Some columns also have default values.

### 6. Cascading Deletes

The defined foreign keys use `ON DELETE CASCADE`, allowing related child records to be deleted when the referenced parent record is deleted.

### 7. SQL and JDBC

The Java backend can execute SQL statements through JDBC to interact with Oracle Database.

### 8. Relational Queries

The schema supports relational queries and joins across connected tables, such as portfolios and holdings or stocks and price history.

### 9. Transaction Processing

The database contains transaction-related tables. Application-level transaction handling and ACID behaviour depend on the actual SQL and Java implementation.

---

## 📝 Current Implementation Notes

- The supplied schema defines 17 tables.
- `APP_USER` stores general user information.
- `INVESTOR`, `ADVISOR`, and `ADMIN` store role-specific information.
- `PORTFOLIO` stores portfolio information and cash balances.
- `HOLDING` stores stock positions associated with portfolios.
- `STOCK` stores stock details and current recorded prices.
- `STOCK_PRICE_HISTORY` stores historical price records.
- `TRANSACTIONS` stores transaction details.
- `BUY_TRANSACTION` and `SELL_TRANSACTION` store buy- and sell-specific brokerage information.
- Watchlist data is represented using three separate tables.
- The supplied schema does not define separate `USERS`, `STOCKS`, `WATCHLIST`, `DIVIDENDS`, or `PORTFOLIO_ANALYTICS` tables.
- The schema file does not itself establish which frontend features or API endpoints are operational.

---

## 🚀 Future Enhancements

Add advanced portfolio performance and profit/loss analytics.
Integrate live stock market data.
Improve dashboard visualizations and reporting.
Strengthen authentication and role-based access control.

---

## 🔒 Security Note

Database credentials should not be committed to the repository. Passwords must be securely handled, and database access should use appropriate permissions and validated queries.

---

## 🌐 Repository Information

- **Repository Name:** `stock-portfolio-analysis-system`
- **GitHub Owner:** `sankhyabhatia1612`
- **Repository URL:** https://github.com/sankhyabhatia1612/stock-portfolio-analysis-system
- **DA2 Project:** Oracle Database 21c implementation

---

## 📌 Conclusion

The project demonstrates the use of Java, JDBC, and Oracle Database to organize stock portfolio information and related transactions. Its relational schema provides a foundation for portfolio management and future enhancements.

