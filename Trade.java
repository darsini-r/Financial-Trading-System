public class Trade {
    private String stockId;
    private long timestamp;
    private double price;
    private int volume;

    public Trade(String stockId, long timestamp, double price, int volume) {
        this.stockId = stockId;
        this.timestamp = timestamp;
        this.price = price;
        this.volume = volume;
    }

    // Getters and Setters (Required for Update)
    public String getStockId() { return stockId; }
    public long getTimestamp() { return timestamp; }
    public double getPrice() { return price; }
    public int getVolume() { return volume; }

    public void setPrice(double price) { this.price = price; }
    public void setVolume(int volume) { this.volume = volume; }

    @Override
    public String toString() {
        return "Trade{ID='" + stockId + "', TS=" + timestamp + ", Price=" + price + ", Volume=" + volume + '}';
    }
}