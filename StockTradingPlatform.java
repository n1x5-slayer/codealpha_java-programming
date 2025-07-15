import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class StockTradingPlatform extends JFrame {
    private final JTextField stockField, quantityField;
    private final JTextArea portfolioArea;
    private final JLabel balanceLabel, marketLabel;
    private final Map<String, Integer> stockPrices = new HashMap<>();
    private final Map<String, Integer> portfolio = new HashMap<>();
    private int balance = 10000;

    public StockTradingPlatform() {
        setTitle("Stock Trading Platform");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(true);
        getContentPane().setBackground(new Color(245, 248, 255));

        JLabel heading = new JLabel("Stock Trading Portal", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setBounds(100, 15, 300, 30);
        add(heading);

        balanceLabel = new JLabel("💵 Balance: $" + balance);
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        balanceLabel.setBounds(50, 60, 200, 20);
        add(balanceLabel);

        marketLabel = new JLabel("📈 Market Rates: ");
        marketLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        marketLabel.setBounds(50, 85, 400, 20);
        add(marketLabel);

        generateMarketPrices();

        JLabel stockLabel = new JLabel("Stock Symbol:");
        stockLabel.setBounds(50, 120, 150, 20);
        add(stockLabel);

        stockField = createRoundedTextField();
        stockField.setBounds(50, 140, 170, 35);
        add(stockField);

        JLabel qtyLabel = new JLabel("Quantity:");
        qtyLabel.setBounds(240, 120, 150, 20);
        add(qtyLabel);

        quantityField = createRoundedTextField();
        quantityField.setBounds(240, 140, 170, 35);
        add(quantityField);

        JButton buyBtn = createRoundedButton("Buy");
        buyBtn.setBounds(50, 190, 100, 40);
        add(buyBtn);

        JButton sellBtn = createRoundedButton("Sell");
        sellBtn.setBounds(160, 190, 100, 40);
        add(sellBtn);

        JButton refreshBtn = createRoundedButton("Refresh Rates");
        refreshBtn.setBounds(270, 190, 140, 40);
        add(refreshBtn);

        portfolioArea = new JTextArea();
        portfolioArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(portfolioArea);
        scrollPane.setBounds(50, 250, 360, 180);
        add(scrollPane);

        buyBtn.addActionListener(e -> buyStock());
        sellBtn.addActionListener(e -> sellStock());
        refreshBtn.addActionListener(e -> generateMarketPrices());

        setVisible(true);
    }

    private void generateMarketPrices() {
        stockPrices.clear();
        stockPrices.put("AAPL", getRandomPrice());
        stockPrices.put("GOOGLE", getRandomPrice());
        stockPrices.put("TESLA", getRandomPrice());
        stockPrices.put("AMAZON", getRandomPrice());
        stockPrices.put("MSFT", getRandomPrice());

        StringBuilder sb = new StringBuilder("📉 ");
        for (Map.Entry<String, Integer> entry : stockPrices.entrySet()) {
            sb.append(entry.getKey()).append("=$").append(entry.getValue()).append("  ");
        }
        marketLabel.setText(sb.toString());
    }

    private int getRandomPrice() {
        return 6 + new Random().nextInt(5); // Random between 6–10
    }

    private void buyStock() {
        String symbol = stockField.getText().toUpperCase().trim();
        String qtyText = quantityField.getText().trim();

        if (!stockPrices.containsKey(symbol)) {
            JOptionPane.showMessageDialog(this, "Invalid stock symbol.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyText);
            int price = stockPrices.get(symbol);
            int cost = price * qty;

            if (qty <= 0) throw new Exception();
            if (cost > balance) {
                JOptionPane.showMessageDialog(this, "Insufficient balance.");
                return;
            }

            portfolio.put(symbol, portfolio.getOrDefault(symbol, 0) + qty);
            balance -= cost;

            int profit = qty <= 30 ? (qty * price * 3) : (qty * price * -2);
            balance += profit;

            updateBalanceAndPortfolio();
            JOptionPane.showMessageDialog(this, "✅ Bought " + qty + " shares of " + symbol);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter valid quantity.");
        }
    }

    private void sellStock() {
        String symbol = stockField.getText().toUpperCase().trim();
        String qtyText = quantityField.getText().trim();

        if (!portfolio.containsKey(symbol)) {
            JOptionPane.showMessageDialog(this, "You don't own this stock.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyText);
            int owned = portfolio.get(symbol);

            if (qty <= 0 || qty > owned) {
                JOptionPane.showMessageDialog(this, "Invalid quantity.");
                return;
            }

            int price = stockPrices.getOrDefault(symbol, getRandomPrice());
            int value = price * qty;

            balance += value;
            portfolio.put(symbol, owned - qty);
            if (portfolio.get(symbol) == 0) portfolio.remove(symbol);

            updateBalanceAndPortfolio();
            JOptionPane.showMessageDialog(this, "✅ Sold " + qty + " shares of " + symbol);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter valid quantity.");
        }
    }

    private void updateBalanceAndPortfolio() {
        balanceLabel.setText("💵 Balance: $" + balance);
        StringBuilder sb = new StringBuilder("📘 Portfolio:\n");
        for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" shares\n");
        }
        portfolioArea.setText(sb.toString());
    }

    private JTextField createRoundedTextField() {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return tf;
    }

    private JButton createRoundedButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(new Color(0, 123, 255));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StockTradingPlatform::new);
    }
}
