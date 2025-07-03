# Hotel Data Merge

A Spring Boot project that merges hotel data from multiple suppliers, cleans and standardizes it, and exposes it via a REST API with filtering support.

---

## 🛠 Features

- Fetch hotel data from 3 supplier APIs: Acme, Patagonia, and Paperflies.
- Normalize and merge duplicate hotel entries using simple rules.
- Periodic in-memory caching every 15 minutes for performance.
- Supplier sources can be configured via `application.yml`.

---

## 📦 Tech Stack

- Java, Spring Boot
- Maven
- Lombok
- JUnit 5 / Mockito for unit testing
- Docker

---

## 🔧 Setup & Run

### 1. Download Docker
- https://www.docker.com/products/docker-desktop/

### 2. Build Docker Image

```bash
docker build -t hotel .
```

### 2. Run Container

```bash
docker run -p 8080:8080 --name hotel-app hotel
```
The app will start on `http://localhost:8080`.

## API Endpoint

### `GET /hotels`

**Query Params:**
- `hotels`: comma-separated hotel IDs (optional)
- `destination`: destination ID (optional)

**Examples:**

- Get by hotel IDs and destination:

```
GET /hotels?hotels=iJhz,f8c9&destination=5432
```

## Scalability & Performance Notes

- Supplier fetching is done **in parallel** using `CompletableFuture` for performance.
- In-memory cache is refreshed every 15 minutes via `@Scheduled`.
- Merging is optimized using `ConcurrentHashMap` and deduplication logic.
- Indexing cached data using `Map<String, Hotel>` or `Multimap<destinationId, Hotel>`.
- Since hotel data is read-only, small and changes when suppliers have new data so we prefer to use in-memory storage.
---


