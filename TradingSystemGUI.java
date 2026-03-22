import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TradingSystemGUI {

private BPlusTree tradingSystem = new BPlusTree(5);
private DefaultTableModel tableModel;

private JTextField stockField, timeField, priceField, volumeField;
private JTextField startField, endField;
private JTextArea output;

public TradingSystemGUI() {

    JFrame frame = new JFrame("Financial Trading System");
    frame.setSize(1200, 700);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.getContentPane().setBackground(new Color(245, 247, 250));
    frame.setLocationRelativeTo(null);

    // ===== TITLE =====
    JLabel title = new JLabel("Financial Trading Dashboard", JLabel.CENTER);
    title.setFont(new Font("Segoe UI", Font.BOLD, 28));
    title.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
    frame.add(title, BorderLayout.NORTH);

    // ===== LEFT PANEL =====
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.setBorder(BorderFactory.createTitledBorder("Trade Input"));
    leftPanel.setPreferredSize(new Dimension(300, 400));
    leftPanel.setBackground(Color.WHITE);

    JPanel inputPanel = new JPanel(new GridLayout(10, 1, 10, 10));
    inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    inputPanel.setBackground(Color.WHITE);

    stockField = new JTextField();
    timeField = new JTextField();
    priceField = new JTextField();
    volumeField = new JTextField();

    inputPanel.add(label("Stock ID", stockField));
    inputPanel.add(label("Timestamp", timeField));
    inputPanel.add(label("Price", priceField));
    inputPanel.add(label("Volume", volumeField));

    JButton insertBtn = button("Insert", new Color(46, 204, 113));
    JButton updateBtn = button("Update", new Color(52, 152, 219));
    JButton deleteBtn = button("Delete", new Color(231, 76, 60));

    inputPanel.add(insertBtn);
    inputPanel.add(updateBtn);
    inputPanel.add(deleteBtn);

    leftPanel.add(inputPanel);
    frame.add(leftPanel, BorderLayout.WEST);

    // ===== TABLE =====
    String[] columns = {"Stock", "Timestamp", "Price", "Volume"};
    tableModel = new DefaultTableModel(columns, 0);

    JTable table = new JTable(tableModel);
    table.setRowHeight(28);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));

    table.setSelectionBackground(new Color(52, 152, 219));
    table.setSelectionForeground(Color.WHITE);
    table.setFillsViewportHeight(true);

    JScrollPane scrollPane = new JScrollPane(table);
    frame.add(scrollPane, BorderLayout.CENTER);

    // ===== ANALYTICS PANEL =====
    JPanel rightPanel = new JPanel(new GridLayout(6, 1, 15, 15));
    rightPanel.setBorder(BorderFactory.createTitledBorder("Analytics"));
    rightPanel.setPreferredSize(new Dimension(300, 400));
    rightPanel.setBackground(Color.WHITE);

    startField = new JTextField();
    endField = new JTextField();

    JButton rangeBtn = button("Range Query", new Color(155, 89, 182));
    JButton trendBtn = button("Trend Analysis", new Color(241, 196, 15));

    rightPanel.add(label("Start Timestamp", startField));
    rightPanel.add(label("End Timestamp", endField));
    rightPanel.add(rangeBtn);
    rightPanel.add(trendBtn);

    frame.add(rightPanel, BorderLayout.EAST);

    // ===== OUTPUT =====
    output = new JTextArea(6, 20);
    output.setFont(new Font("Consolas", Font.PLAIN, 13));
    output.setEditable(false);
    frame.add(new JScrollPane(output), BorderLayout.SOUTH);

    // ===== TABLE CLICK AUTO-FILL =====
    table.getSelectionModel().addListSelectionListener(e -> {
        int row = table.getSelectedRow();
        if (row != -1) {
            stockField.setText(tableModel.getValueAt(row, 0).toString());
            timeField.setText(tableModel.getValueAt(row, 1).toString());
            priceField.setText(tableModel.getValueAt(row, 2).toString());
            volumeField.setText(tableModel.getValueAt(row, 3).toString());
        }
    });

    // ===== BUTTON ACTIONS =====

    insertBtn.addActionListener(e -> {
        try {
            Trade t = new Trade(
                    stockField.getText(),
                    Long.parseLong(timeField.getText()),
                    Double.parseDouble(priceField.getText()),
                    Integer.parseInt(volumeField.getText())
            );

            tradingSystem.insert(t);

            tableModel.addRow(new Object[]{
                    t.getStockId(), t.getTimestamp(), t.getPrice(), t.getVolume()
            });

            output.append("Trade inserted successfully\n");
            clear();

        } catch (Exception ex) {
            output.append("Invalid input\n");
        }
    });

    updateBtn.addActionListener(e -> {
        try {
            long ts = Long.parseLong(timeField.getText());
            double price = Double.parseDouble(priceField.getText());
            int vol = Integer.parseInt(volumeField.getText());

            if (tradingSystem.update(ts, price, vol)) {
                updateTableRow(ts, price, vol);
                output.append("Trade updated\n");
            } else {
                output.append("Trade not found\n");
            }

        } catch (Exception ex) {
            output.append("Update error\n");
        }
    });

    deleteBtn.addActionListener(e -> {
        try {
            long ts = Long.parseLong(timeField.getText());

            if (tradingSystem.delete(ts)) {
                removeTableRow(ts);
                output.append("Trade deleted\n");
            } else {
                output.append("Trade not found\n");
            }

        } catch (Exception ex) {
            output.append("Delete error\n");
        }
    });

    rangeBtn.addActionListener(e -> {
        try {
            long start = Long.parseLong(startField.getText());
            long end = Long.parseLong(endField.getText());

            QueryResult r = tradingSystem.rangeQuery(start, end);

            if (r.foundTrades) {
                output.append("\nRange Results:\n");
                output.append("Volume: " + r.totalVolume + "\n");
                output.append("Max: " + r.maxPrice + "\n");
                output.append("Min: " + r.minPrice + "\n");
            } else {
                output.append("No data\n");
            }

        } catch (Exception ex) {
            output.append("Range error\n");
        }
    });

    trendBtn.addActionListener(e -> {
        try {
            long start = Long.parseLong(startField.getText());
            long end = Long.parseLong(endField.getText());

            QueryResult r = tradingSystem.rangeQuery(start, end);

            double vol = (r.maxPrice - r.minPrice) /
                    ((r.maxPrice + r.minPrice) / 2);

            output.append("\nTrend Analysis:\n");
            output.append("Volatility: " + (vol * 100) + "%\n");

            if (vol > 0.05)
                output.append("HIGH VOLATILITY\n");
            else
                output.append("STABLE MARKET\n");

        } catch (Exception ex) {
            output.append("Trend error\n");
        }
    });

    frame.setVisible(true);
}

// ===== HELPERS =====

private JPanel label(String text, JTextField field) {
    JPanel p = new JPanel(new BorderLayout());
    p.setBackground(Color.WHITE);
    JLabel lbl = new JLabel(text);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
    p.add(lbl, BorderLayout.NORTH);
    p.add(field, BorderLayout.CENTER);
    return p;
}

private JButton button(String text, Color color) {
    JButton b = new JButton(text);
    b.setBackground(color);
    b.setForeground(Color.WHITE);
    b.setFont(new Font("Segoe UI", Font.BOLD, 14));
    b.setFocusPainted(false);
    return b;
}

private void clear() {
    stockField.setText("");
    timeField.setText("");
    priceField.setText("");
    volumeField.setText("");
}

private void updateTableRow(long ts, double price, int vol) {
    for (int i = 0; i < tableModel.getRowCount(); i++) {
        if ((long) tableModel.getValueAt(i, 1) == ts) {
            tableModel.setValueAt(price, i, 2);
            tableModel.setValueAt(vol, i, 3);
            break;
        }
    }
}

private void removeTableRow(long ts) {
    for (int i = 0; i < tableModel.getRowCount(); i++) {
        if ((long) tableModel.getValueAt(i, 1) == ts) {
            tableModel.removeRow(i);
            break;
        }
    }
}

public static void main(String[] args) {
    new TradingSystemGUI();
}

}
