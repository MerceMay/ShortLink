# ShortLink - SaaS Short Link System

## 📖 Introduction

ShortLink is a high-performance, distributed SaaS short link system. It provides comprehensive short link management features including generation, grouping, statistical analysis, and high-concurrency handling.

The project adopts a microservice architecture but also supports an aggregated deployment mode for flexibility.

## 🏗 Architecture

The system is built using **Java 17** and **Spring Boot 3**, leveraging the **Spring Cloud Alibaba** ecosystem.

### Core Modules

- **`shortlink-admin`**: Management service. Handles user registration, login, link grouping, and personal settings.
- **`shortlink-project`**: Core business service. Handles short link creation, editing, deletion, redirection logic, and visit statistics.
- **`shortlink-gateway`**: API Gateway. Provides unified entry point, routing, and filtering.
- **`shortlink-aggregation`**: Aggregation service. Bundles `admin` and `project` modules for a simplified "monolithic-like" deployment, useful for demo or small-scale environments.

### Technology Stack

- **Backend**: Java 17, Spring Boot 3.0.7, Spring Cloud Alibaba 2022.0.0.0-RC2
- **Database**: MySQL 8.0, MyBatis Plus
- **Sharding**: Apache ShardingSphere 5.3.2 (Database sharding)
- **Cache**: Redis, Redisson
- **Message Queue**: Apache RocketMQ 5.3.1
- **Service Discovery**: Nacos
- **Frontend**: Vue.js (Assumed based on output structure), served via Nginx

## ✨ Features

- **User Management**: Registration, Login, User information management.
- **Link Grouping**: Organize short links into groups for better management.
- **Short Link Management**:
    - Create, Edit, Delete short links.
    - **Recycle Bin**: Recover deleted links within a retention period.
- **Statistics**: Detailed visit analytics (PV, UV, IP, etc.) and visualization.
- **High Performance**:
    - Database sharding for massive data handling.
    - Redis caching for hot data.
    - Bloom Filters (implied for existence checks) to prevent cache penetration.

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### Installation

1.  **Clone the repository**
    ```bash
    git clone <repository-url>
    cd shortlink
    ```

2.  **Start Infrastructure**
    Use Docker Compose to start MySQL, Redis, Nacos, RocketMQ, and Nginx.
    ```bash
    docker-compose up -d
    ```
    *Note: Ensure ports 3306, 6379, 8848, 9876, 80 are available.*

3.  **Database Initialization**
    The `docker-compose` setup mounts `./volumes/mysql/init` which contains `01_schema.sql` and `02_data.sql`. The database should be initialized automatically upon the first startup of the MySQL container.

4.  **Configuration**
    - Check `application.yml` in each module for specific configurations.
    - Nacos configuration: The system uses Nacos for service discovery. Ensure the services can register to the Nacos server (default `localhost:8848` or container name).

5.  **Build & Run**
    You can run the services individually or use the aggregation service.

    **Option A: Run Aggregation Service (simplest)**
    ```bash
    cd aggregation
    mvn spring-boot:run
    ```

    **Option B: Run Microservices**
    1.  Start `GatewayServiceApplication` (in `gateway`)
    2.  Start `ShortLinkAdminApplication` (in `admin`)
    3.  Start `ShortLinkApplication` (in `project`)

6.  **Access the Application**
    - **Frontend**: Access via `http://localhost` (served by Nginx).
    - **Nacos Console**: `http://localhost:8848/nacos`

## 📂 Directory Structure

```text
shortlink/
├── admin/               # Admin Service (User, Group, etc.)
├── aggregation/         # Aggregation Service (Bundled deployment)
├── gateway/             # Spring Cloud Gateway
├── project/             # Core Project Service (Link logic, Stats)
├── volumes/             # Docker volumes (MySQL data, Nginx html/conf, etc.)
│   ├── mysql/init/      # DB Init scripts
│   └── nginx/html/      # Frontend static files (dist-link)
├── docker-compose.yml   # Docker infrastructure setup
└── pom.xml              # Root Maven POM
```

## 🛠 Development

- **ShardingSphere Config**: Located in `src/main/resources/shardingsphere-config-*.yml`.
- **API Documentation**: (Add Swagger/Knife4j info if available)

## 🤝 Contribution

Contributions are welcome! Please submit a Pull Request.
