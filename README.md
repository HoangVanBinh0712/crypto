# 📊 Crypto Trading Platform Documentation

## 🧩 Entity Relationship Diagram (ERD)

The system contains the following main entities:

---

### **1. `user_info`**
Stores information about each user.

| Column   | Type           | Description        |
|----------|----------------|--------------------|
| id       | `bigint`       | Primary key        |
| username | `varchar(50)`  | Unique username    |
| email    | `varchar(100)` | User email address |

---

### **2. `wallet`**
Tracks user's balances in different currencies.

| Column   | Type            | Description                      |
|----------|------------------|----------------------------------|
| id       | `bigint`         | Primary key                      |
| user_id  | `bigint`         | Foreign key → `user_info.id`     |
| currency | `varchar(10)`    | Currency code (e.g., BTC, USDT)  |
| balance  | `numeric(19,6)`  | Current balance                  |

---

### **3. `price_snapshot`**
Stores the bid/ask price snapshots for a specific symbol.

| Column    | Type            | Description                        |
|-----------|------------------|------------------------------------|
| id        | `bigint`         | Primary key                        |
| ask_price | `numeric(19,6)`  | Ask price                          |
| bid_price | `numeric(19,6)`  | Bid price                          |
| timestamp | `timestamp(6)`   | Snapshot timestamp                 |
| ask_from  | `exchange_enum`  | Source exchange of ask price       |
| bid_from  | `exchange_enum`  | Source exchange of bid price       |
| symbol    | `symbol_enum`    | Trading pair symbol (e.g., BTCUSDT)|

---

### **4. `trade_transaction`**
Records all trade transactions.

| Column             | Type                 | Description                          |
|--------------------|----------------------|--------------------------------------|
| id                 | `bigint`             | Primary key                          |
| user_id            | `bigint`             | Foreign key → `user_info.id`         |
| price              | `numeric(19,6)`      | Executed price                       |
| quantity           | `numeric(19,6)`      | Quantity traded                      |
| total              | `numeric(19,6)`      | Total = price × quantity             |
| timestamp          | `timestamp(6)`       | Trade timestamp                      |
| exchange           | `exchange_enum`      | Exchange where trade occurred        |
| symbol             | `symbol_enum`        | Symbol (e.g., BTCUSDT)               |
| type               | `transaction_type_enum` | BUY or SELL                        |
| price_snapshot_id  | `bigint`             | Foreign key → `price_snapshot.id`    |


### **5. Indexes **
create index idx_price_snapshot_symbol_timestamp on price_snapshot (symbol, timestamp desc)

### **6. Relationship diagram **
![Relationship](/diagram.png)

---

## 📡 API Information

> Swagger UI:  
> `http://localhost:8080/api/swagger-ui/index.html`

---

### 🔹 **1. Task - 2: Get Latest Best Prices**
- **URL:** `/api/prices/best`
- **Method:** `GET`
- **Response:**
```json
{
  "latestBestPrices": [
    {
      "id": 1,
      "ask_price": 100.0,
      "bid_price": 99.0,
      "symbol": "BTCUSDT"
    }
  ]
}
```
- **Note:** This API returns the latest bid/ask record per symbol.

---

### 🔹 **2. Task - 4: Get User Wallets**
- **URL:** `/api/wallets/user/{userId}`
- **Method:** `GET`
- **Response:**
```json
[
  {
    "id": 1,
    "currency": "USDT",
    "balance": 500.0
  },
  {
    "id": 2,
    "currency": "BTC",
    "balance": 0.5
  }
]
```
- **Note:** Returns all wallet balances for a specific user.

---

### 🔹 **3. Task - 3: Trade Coin**
- **URL:** `/api/trade`
- **Method:** `POST`
- **Request:**
```json
{
  "userId": 1,
  "symbol": "BTCUSDT",
  "type": "BUY",
  "quantity": 0.1
}
```
- **Response:**
```json
{
  "id": 1,
  "symbol": "BTCUSDT",
  "type": "BUY",
  "price": 100.0,
  "quantity": 0.1,
  "total": 10.0
}
```

- **Validation:**
    - `userId`: Not null
    - `symbol`: Not blank and must be in BTCUSDT or ETHUSDT
    - `type`: Not blank and must be in BUY or SELL
    - `quantity`: Must be > 0

- **Note:**  
  Executes a trade operation (buy/sell).  
  Validates user balance and symbol before proceeding.

---
### 🔹 **4. Task - 5: Get Trading History**
- **URL:** `/api/trade/history/{userId}`
- **Method:** `GET`

- **Response:**
```json
{
  "userTradingHistory": [
    {
      "id": 1,
      "symbol": "BTCUSDT",
      "type": "BUY",
      "exchange": "BINANCE",
      "price": 105174.15,
      "quantity": 0.1,
      "total": 10517.415,
      "timestamp": "2025-06-07T13:54:26.945739",
      "tradingSnapshot": {
        "id": 7,
        "symbol": "BTCUSDT",
        "bidFrom": "HOUBI",
        "bidPrice": 105178.06,
        "askFrom": "BINANCE",
        "askPrice": 105174.15,
        "timestamp": "2025-06-07T13:54:20.321041"
      }
    },
    {
      "id": 2,
      "symbol": "BTCUSDT",
      "type": "SELL",
      "exchange": "HOUBI",
      "price": 105127,
      "quantity": 0.01,
      "total": 1051.27,
      "timestamp": "2025-06-07T14:07:17.703315",
      "tradingSnapshot": {
        "id": 161,
        "symbol": "BTCUSDT",
        "bidFrom": "HOUBI",
        "bidPrice": 105127,
        "askFrom": "BINANCE",
        "askPrice": 105120.43,
        "timestamp": "2025-06-07T14:07:10.329435"
      }
    }
  ]
}
```

- **Note:**  
  This API returns the trading history of the given user ID.
---
### 🔹 **5. Task - 1: Interval scheduler **
- Fetch data from Binance and Houbi parallel.
- Then compare the price of buying (smaller better) and price of selling (higher better).
- Then store data to the price_snapshot table.
---
## ✅ Notes
- I create sample Users and Wallets in `ServerReadyListener.java`
- You can read detailed in the [Docs](docs.xlsx)