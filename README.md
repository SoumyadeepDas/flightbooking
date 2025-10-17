# ✈️ Flight Booking System (Spring Boot + Amadeus API)

This project is a **Flight Booking System** built using **Java, Spring Boot, and H2 Database**.  
It integrates the **Amadeus Flight Offers API** to fetch **real-time flight prices**, allowing users to book one-way or round-trip flights.

---

## 🧩 Features

- 🔍 Search real-time flights (via Amadeus API)
- 🧑 Register users with categories (Student, Senior Citizen, Armed Forces)
- 💺 Add multiple passengers in a booking
- 💰 Automatic fare calculation (based on passenger count & trip type)
- 💾 Store booking data in H2 in-memory DB
- 🧾 Input validation and error handling
- 🌐 RESTful APIs with Postman testing support
- ⚙️ Clear modular structure (Controller → Service → Repository → Model → DTO)

---

## 🛠️ Tech Stack

| Layer | Technology Used |
|-------|----------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot |
| **Database** | H2 (In-memory) |
| **External API** | Amadeus Flight Offers API |
| **Build Tool** | Maven |
| **IDE** | IntelliJ IDEA Ultimate |
| **Validation** | Jakarta Validation |
| **Logging** | SLF4J + Logback |
