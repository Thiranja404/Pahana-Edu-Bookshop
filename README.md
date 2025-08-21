## Overview

This project is a comprehensive bookstore billing system built using modern enterprise Java technologies. It demonstrates the implementation of a 3-tier architecture with proper design patterns and best practices.

## Features

- **Customer Management** - Add, edit, and search customers
- **Item Catalog** - Manage inventory with SKU and pricing
- **Billing System** - Create bills with multiple items
- **Dashboard** - Overview of all operations
- **User Authentication** - Secure login system
- **Responsive Design** - Works on desktop and mobile

## Technology Stack

- **Backend:** Java 21, Jakarta EE 6.0, MySQL 8.4
- **Frontend:** JSP, Bootstrap 5, JavaScript
- **Build Tool:** Maven
- **Server:** Apache Tomcat 10.x
- **Database:** MySQL with HikariCP connection pooling

## Architecture

The application follows a 3-tier architecture:

- **Presentation Layer:** JSP pages, Servlets, JavaScript
- **Business Layer:** Service classes with business logic
- **Data Layer:** DAO pattern with MySQL database


┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Presentation   │    │   Business      │    │   Data Access   │
│     Tier        │◄──►│     Tier        │◄──►│     Tier        │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ • JSP Pages     │    │ • Service Layer │    │ • DAO Pattern   │
│ • Servlets      │    │ • Business Logic│    │ • MySQL DB      │
│ • JavaScript    │    │ • Validation    │    │ • Connection    │
│ • Bootstrap CSS │    │ • Transactions  │    │   Pooling       │
└─────────────────┘    └─────────────────┘    └─────────────────┘

## Design Patterns

- Model-View-Controller (MVC)
- Data Access Object (DAO)
- Singleton Pattern
- Service Layer Pattern
- Front Controller Pattern

## Project Structure

```
src/main/java/com/pahanaedu/bookstore/
├── controller/     # Servlet controllers
├── service/        # Business logic layer
├── dao/            # Data access layer
├── model/          # Domain objects
├── filter/         # Security filters
└── util/           # Utility classes

src/main/webapp/
├── WEB-INF/jsp/    # JSP view pages
├── assets/         # CSS and JavaScript
└── WEB-INF/web.xml # Configuration


