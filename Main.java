import java.util.Scanner;

public class Main {
    // --- Constant for Price Trend Analysis ---
    private static final double VOLATILITY_THRESHOLD = 0.05; // 5% difference is considered volatile

    public static void main(String[] args) {
        BPlusTree tradingSystem = new BPlusTree(5); 
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        do {
            System.out.println("\n--- Financial Trading System Menu ---");
            System.out.println("1. Insert New Trade");
            System.out.println("2. Update Existing Trade");
            System.out.println("3. Delete Trade");
            System.out.println("4. Run Range Query");
            System.out.println("5. Price Trend Detection");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            
            String choiceInput = scanner.next();
            try {
                choice = Integer.parseInt(choiceInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number (1-6).");
                continue;
            }

            switch (choice) {
                case 1: runInsertTrade(tradingSystem, scanner); break;
                case 2: runUpdateTrade(tradingSystem, scanner); break;
                case 3: runDeleteTrade(tradingSystem, scanner); break;
                case 4: runRangeQuery(tradingSystem, scanner); break;
                case 5: runPriceTrendDetection(tradingSystem, scanner); break;
                case 6: System.out.println("Exiting system. Thank you."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 6);

        scanner.close();
    }
    
    // --- Menu Implementations ---

    private static void runInsertTrade(BPlusTree tree, Scanner scanner) {
        System.out.print("Enter Stock ID : ");
        String stockId = scanner.next();
        System.out.print("Enter Timestamp : ");
        long timestamp = getLongInput(scanner);
        System.out.print("Enter Price : ");
        double price = getDoubleInput(scanner);
        System.out.print("Enter Volume : ");
        int volume = getIntInput(scanner);

        Trade newTrade = new Trade(stockId, timestamp, price, volume);
        tree.insert(newTrade);
        System.out.println("Success: Trade inserted.");
    }

    private static void runUpdateTrade(BPlusTree tree, Scanner scanner) {
        System.out.print("Enter Timestamp of trade to update: ");
        long timestamp = getLongInput(scanner);
        System.out.print("Enter NEW Price: ");
        double newPrice = getDoubleInput(scanner);
        System.out.print("Enter NEW Volume: ");
        int newVolume = getIntInput(scanner);

        if (tree.update(timestamp, newPrice, newVolume)) {
            System.out.println("Success: Trade at " + timestamp + " updated.");
        } else {
            System.out.println("Error: Trade not found.");
        }
    }
    
    private static void runDeleteTrade(BPlusTree tree, Scanner scanner) {
        System.out.print("Enter Timestamp of trade to delete: ");
        long timestamp = getLongInput(scanner);
        
        if (tree.delete(timestamp)) {
            System.out.println("Success: Trade at " + timestamp + " deleted.");
        } else {
            System.out.println("Error: Trade not found.");
        }
    }

    private static void runRangeQuery(BPlusTree tree, Scanner scanner) {
        System.out.print("Enter Start Timestamp: ");
        long start = getLongInput(scanner);
        System.out.print("Enter End Timestamp: ");
        long end = getLongInput(scanner);

        QueryResult result = tree.rangeQuery(start, end);

        if (result.foundTrades) {
            System.out.println("\n--- Query Results (" + start + " to " + end + ") ---");
            System.out.println("Total Volume: " + result.totalVolume);
            System.out.println("Maximum Price: " + result.maxPrice);
            System.out.println("Minimum Price: " + result.minPrice);
        } else {
            System.out.println("No trades found in the specified range.");
        }
    }

    private static void runPriceTrendDetection(BPlusTree tree, Scanner scanner) {
        System.out.print("Enter Start Timestamp for analysis: ");
        long start = getLongInput(scanner);
        System.out.print("Enter End Timestamp for analysis: ");
        long end = getLongInput(scanner);

        QueryResult result = tree.rangeQuery(start, end);

        if (!result.foundTrades) {
            System.out.println("Cannot detect trend: No trades found in the range.");
            return;
        }

        double maxPrice = result.maxPrice;
        double minPrice = result.minPrice;
        double averagePrice = (maxPrice + minPrice) / 2.0;

        if (averagePrice == 0) {
            System.out.println("Trend: Price data is zero.");
            return;
        }

        // Calculate volatility as a percentage change relative to the average
        double priceSpread = maxPrice - minPrice;
        double volatilityPercentage = priceSpread / averagePrice; 
        
        System.out.println("\n--- Price Trend Analysis (" + start + " to " + end + ") ---");
        System.out.printf("Price Range: %.2f to %.2f\n", minPrice, maxPrice);
        System.out.printf("Volatility (Spread/Avg): %.2f%%\n", volatilityPercentage * 100);

        if (volatilityPercentage > VOLATILITY_THRESHOLD) {
            System.out.println("Conclusion: HIGH VOLATILITY. Price spread exceeds " + (VOLATILITY_THRESHOLD * 100) + "%.");
        } else {
            System.out.println("Conclusion: STABLE. Price changes are minimal within the range.");
        }
    }
    
    // --- Robust Input Helpers ---
    private static long getLongInput(Scanner scanner) {
        // Ensures only a long integer is read, handling non-numeric input gracefully.
        while (!scanner.hasNextLong()) {
            System.out.println("Invalid input. Please enter a valid timestamp (integer).");
            scanner.next(); 
        }
        return scanner.nextLong();
    }

    private static double getDoubleInput(Scanner scanner) {
        // Ensures only a double is read.
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a valid price (decimal).");
            scanner.next(); 
        }
        return scanner.nextDouble();
    }

    private static int getIntInput(Scanner scanner) {
        // Ensures only an integer is read.
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid volume (integer).");
            scanner.next(); 
        }
        return scanner.nextInt();
    }
}

//--COMPLEXITY ANALYSIS SUMMARY--
//Primary ADT: Hybrid B+ Tree (Indexed by Timestamp) and Segment Tree (for Leaf Aggregation)
//N: Total Trades | B: B+ Tree Order/Leaf Size | k: Number of Leaves Spaned by Query
//Operation              | Time Complexity
//1. Single Insertion    | O(log N)
//2. Range Query         | O(log N + k * log B)
//3. Update              | O(log N + log B) [Conceptual]
//4. Deletion            | O(log N) - Worst Case O(N) with naive parent search
//Space Complexity: O(N) - Linear with the number of trades.
