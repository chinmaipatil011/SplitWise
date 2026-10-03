# Splitwise Clone 💸

A group expense-sharing backend built with **Java, Spring Boot, and PostgreSQL**, inspired by Splitwise. It simplifies shared expense management through automated splitting, balance calculation, and settlement tracking.

## 🚀 Features

- **Authentication:** User registration and login using JWT and BCrypt password hashing.
- **Group Management:** Create groups and manage member associations.
- **Expense Tracking:** Create, retrieve, update, and delete expenses.
- **Equal Splitting:** Automatically divide expenses among group members, including remainder handling.
- **Balance Calculation:** Calculate net balances to determine who owes whom.
- **Settlements:** Track payments between group members.
- **Validation & Error Handling:** Input validation and centralized exception handling.

## 🛠️ Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot 4.1.1
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA, Hibernate
- **Security:** Spring Security, JWT, BCrypt
- **Build Tool:** Maven
- **API Testing:** Postman

## 🏗️ Architecture

The application follows a layered architecture:

```text
Client (Postman)
      |
 REST Controllers
      |
    Services
 (Business Logic)
      |
  Repositories
      |
  PostgreSQL
```

## ⚙️ Setup & Installation

**1. Clone the repository**

```bash
git clone https://github.com/YOUR_USERNAME/splitwise-clone.git
cd splitwise-clone
```

**2. Create a PostgreSQL database**

```sql
CREATE DATABASE splitwise_db;
```

**3. Configure environment variables**

Set the following variables in your environment:

- `DB_USERNAME` — PostgreSQL username
- `DB_PASSWORD` — PostgreSQL password
- `JWT_SECRET` — Base64-encoded secret of at least 256 bits

Configure `application.properties` to reference these variables. Do not commit credentials or secrets.

**4. Run the application**

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS/Linux:

```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`.

## 🔐 Authentication

Register a user, then log in through `/api/auth/login` to receive a JWT. Include it in the `Authorization` header for protected endpoints:

```http
Authorization: Bearer <your_token>
```

## 📡 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/users` | Register user |
| POST | `/api/auth/login` | User login |
| GET | `/api/expenses/group/{groupId}` | Get group expenses |
| PUT | `/api/expenses/{expenseId}` | Update expense |
| DELETE | `/api/expenses/{expenseId}` | Delete expense |

See the controllers for the complete list of endpoints.

## 👩‍💻 Author

**Chinmai Patil**  
[GitHub](https://github.com/YOUR_USERNAME)
