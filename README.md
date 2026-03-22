📈 Financial Trading System

A desktop-based trading analytics system built using advanced data structures for efficient processing of financial trade data.

🚀 Overview

This project implements a hybrid data structure architecture combining:

B+ Tree for efficient indexing of trades by timestamp
Segment Tree for fast aggregation and range-based analytics

The system supports dynamic trade operations and enables users to perform optimized queries on large datasets with minimal computational overhead.

🚀 Features
📊 Trade Management
Insert, update, and delete trades
Structured storage of trade data (Stock ID, Timestamp, Price, Volume)
Real-time synchronization with GUI table
⚡ Efficient Range Queries
Compute total traded volume within a time range
Identify maximum and minimum prices
Optimized query performance using hybrid tree structure
📈 Market Trend Analysis
Volatility detection based on price spread
Classifies market behavior into:
Stable
High Volatility
🖥️ Interactive GUI
Built using Java Swing
Clean dashboard layout (Input Panel, Data Table, Analytics Panel)
Table row selection auto-fills input fields
Real-time output logging

🧠 System Design

The system follows a layered architecture:

User Interface (GUI)
        ↓
B+ Tree (Time-based Indexing)
        ↓
Leaf Nodes (Trade Storage)
        ↓
Segment Tree (Aggregation Layer)
Design Highlights:
B+ Tree ensures O(log N) search and insertion
Segment Tree enables O(log B) aggregation within nodes
Combined approach provides efficient range queries:
O(log N + k log B)

🛠️ Tech Stack
Language: Java
GUI Framework: Java Swing
Core Concepts:
B+ Tree
Segment Tree
Time-series data processing
Algorithm optimization

📂 Project Structure
FinancialTradingSystem/
│
├── Trade.java
├── SegmentTree.java
├── BPlusTree.java
├── TradingSystemGUI.java
├── QueryResult.java

▶️ Getting Started
Compile
javac *.java
Run
java TradingSystemGUI

📊 Performance Overview
Operation	Complexity
Insert	O(log N)
Update	O(log N)
Delete	O(log N)
Range Query	O(log N + k log B)

🔮 Future Enhancements
Persistent storage (file/database integration)
Real-time streaming data support
Graph-based visualization (charts)
Multi-threaded processing for scalability

👨‍💻 Author
Darsini

⭐ Summary

This project demonstrates the ability to combine data structures, system design, and user interface development to build an efficient and scalable analytics application.
