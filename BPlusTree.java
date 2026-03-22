import java.util.ArrayList;
import java.util.List;

class BPlusTreeNode {
    boolean isLeaf;
    List<Long> keys;
    List<BPlusTreeNode> children;
    BPlusTreeNode nextLeaf;
    List<Trade> trades; 
    SegmentTree segTree; 

    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.trades = new ArrayList<>();
        this.nextLeaf = null;
    }
}

// Helper class to store and return query results
class QueryResult {
    public int totalVolume = 0;
    public double maxPrice = 0.0;
    public double minPrice = Double.MAX_VALUE;
    public boolean foundTrades = false;
}


public class BPlusTree {
    private BPlusTreeNode root;
    private int order;

    public BPlusTree(int order) {
        this.order = order;
        this.root = new BPlusTreeNode(true);
    }

    // O(log N) search to find the correct leaf node
    private BPlusTreeNode findLeafNode(BPlusTreeNode node, long timestamp) {
        if (node.isLeaf) return node;
        int i = 0;
        while (i < node.keys.size() && timestamp >= node.keys.get(i)) {
            i++;
        }
        return findLeafNode(node.children.get(i), timestamp);
    }
    
    // Simplified O(N) parent search - acknowledge the O(log N) alternative for presentation
    private BPlusTreeNode findParent(BPlusTreeNode node, BPlusTreeNode child) {
        if (node == null || node.isLeaf) return null;
        for (BPlusTreeNode currentChild : node.children) {
            if (currentChild == child) return node;
            if (!currentChild.isLeaf) {
                BPlusTreeNode parent = findParent(currentChild, child);
                if (parent != null) return parent;
            }
        }
        return null;
    }
    
    // --- CORE MUTATION METHODS ---

    // O(log N) - Single insertion
    public void insert(Trade trade) {
        BPlusTreeNode node = findLeafNode(root, trade.getTimestamp());
        
        // Find correct position to insert (maintains sorted order)
        int i = 0;
        while (i < node.trades.size() && node.trades.get(i).getTimestamp() < trade.getTimestamp()) {
            i++;
        }
        node.trades.add(i, trade);
        node.keys.add(i, trade.getTimestamp());

        // Rebuild the segment tree for the leaf node
        node.segTree = new SegmentTree(node.trades);

        // Handle node splitting if it's full
        if (node.trades.size() > order - 1) {
            split(node);
        }
    }
    
    // Simplified split logic
    private void split(BPlusTreeNode node) {
        BPlusTreeNode parent = findParent(root, node);
        if (parent == null) {
            parent = new BPlusTreeNode(false);
            parent.children.add(node);
            root = parent;
        }

        int midIndex = node.trades.size() / 2;
        BPlusTreeNode newNode = new BPlusTreeNode(true);
        
        newNode.trades = new ArrayList<>(node.trades.subList(midIndex, node.trades.size()));
        newNode.keys = new ArrayList<>(node.keys.subList(midIndex, node.keys.size()));
        
        node.trades.subList(midIndex, node.trades.size()).clear();
        node.keys.subList(midIndex, node.keys.size()).clear();

        newNode.nextLeaf = node.nextLeaf;
        node.nextLeaf = newNode;

        node.segTree = new SegmentTree(node.trades);
        newNode.segTree = new SegmentTree(newNode.trades);

        long newKey = newNode.keys.get(0);
        int parentIndex = 0;
        while (parentIndex < parent.keys.size() && newKey > parent.keys.get(parentIndex)) {
            parentIndex++;
        }
        parent.keys.add(parentIndex, newKey);
        parent.children.add(parentIndex + 1, newNode);
    }
    
    // O(log N + O(log B)) - Update
    public boolean update(long timestamp, double newPrice, int newVolume) {
        BPlusTreeNode node = findLeafNode(root, timestamp);
        int index = -1;
        for (int i = 0; i < node.trades.size(); i++) {
            if (node.trades.get(i).getTimestamp() == timestamp) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            Trade tradeToUpdate = node.trades.get(index);
            tradeToUpdate.setPrice(newPrice);
            tradeToUpdate.setVolume(newVolume);
            
            node.segTree.updateTrade(index, tradeToUpdate); 
            return true;
        }
        return false;
    }
    
    // O(log N + O(B)) - Simplified Deletion
    public boolean delete(long timestamp) {
        BPlusTreeNode node = findLeafNode(root, timestamp);
        int index = -1;
        for (int i = 0; i < node.trades.size(); i++) {
            if (node.trades.get(i).getTimestamp() == timestamp) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            node.trades.remove(index);
            node.keys.remove(index);
            node.segTree = new SegmentTree(node.trades); 
            
            // NOTE: Missing B+ Tree Merge/Redistribution logic goes here.
            
            return true;
        }
        return false;
    }

    // --- RANGE QUERY (O(log N + k * log B)) ---
    public QueryResult rangeQuery(long startTimestamp, long endTimestamp) {
        BPlusTreeNode currentNode = findLeafNode(root, startTimestamp);
        QueryResult result = new QueryResult();
        
        while (currentNode != null) {
            int startIndex = 0;
            while (startIndex < currentNode.trades.size() && currentNode.trades.get(startIndex).getTimestamp() < startTimestamp) {
                startIndex++;
            }
            int endIndex = currentNode.trades.size() - 1;
            while (endIndex >= 0 && currentNode.trades.get(endIndex).getTimestamp() > endTimestamp) {
                endIndex--;
            }

            if (endIndex >= startIndex) {
                result.foundTrades = true;
                result.totalVolume += currentNode.segTree.querySumVolume(startIndex, endIndex);
                result.maxPrice = Math.max(result.maxPrice, currentNode.segTree.queryMaxPrice(startIndex, endIndex));
                result.minPrice = Math.min(result.minPrice, currentNode.segTree.queryMinPrice(startIndex, endIndex));
            }
            
            if (currentNode.keys.get(currentNode.keys.size() - 1) >= endTimestamp) {
                break;
            }
            currentNode = currentNode.nextLeaf;
        }
        return result;
    }
}