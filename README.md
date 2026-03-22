# 📈 Financial Trading System

A trading system with GUI for managing and analyzing trade data using B+ Tree and Segment Tree.

---

## 🚀 Overview

This project uses a hybrid data structure approach:

* **B+ Tree** → indexing trades by timestamp
* **Segment Tree** → fast aggregation and range queries

The system supports efficient querying and analysis of trade data.

---

## ✨ Features

### 📊 Trade Management

* Insert, update, and delete trades
* Stores trade data (Stock ID, Timestamp, Price, Volume)
* Updates reflected in GUI table

### ⚡ Range Queries

* Total volume in a time range
* Maximum price
* Minimum price

### 📈 Trend Analysis

* Calculates volatility based on price range
* Classifies market as:

  * Stable
  * High Volatility

### 🖥️ GUI

* Built using Java Swing
* Input panel, table, and analytics panel
* Table row selection auto-fills input fields
* Displays results in output area

---

## 🧠 System Design

```
GUI
 ↓
B+ Tree (Indexing)
 ↓
Leaf Nodes (Trade Storage)
 ↓
Segment Tree (Aggregation)
```

---

## 🛠️ Tech Stack

* Java
* Java Swing
* B+ Tree
* Segment Tree

---

## 📂 Project Structure

```
FinancialTradingSystem/
├── Trade.java
├── SegmentTree.java
├── BPlusTree.java
├── TradingSystemGUI.java
├── QueryResult.java
```

---

## ▶️ Getting Started

### Compile

```bash
javac *.java
```

### Run

```bash
java TradingSystemGUI
```

---

## 📊 Performance

* Insert: O(log N)
* Update: O(log N)
* Delete: O(log N)
* Range Query: O(log N + k log B)

---

## 🔮 Future Improvements

* File/database storage
* Real-time data support
* Charts/visualization
* Multi-threading

---

## 👨‍💻 Author

Darsini

---

## ⭐ Summary

This project combines data structures and a GUI to manage and analyze trade data efficiently.
