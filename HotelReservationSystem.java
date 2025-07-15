import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class HotelReservationSystem extends JFrame {
    private JTextField nameField;
    private JComboBox<String> roomBox;
    private JTextField nightsField;
    private JButton bookBtn, cancelBtn, viewBtn;
    private final String FILE_NAME = "reservations.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HotelReservationSystem::new);
    }

    public HotelReservationSystem() {
        setTitle("Hotel Reservation System");
        setSize(450, 400);
        setLayout(null);
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("Hotel Reservation", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBounds(100, 20, 250, 30);
        add(title);

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setBounds(50, 70, 100, 20);
        add(nameLabel);

        nameField = createRoundedTextField();
        nameField.setBounds(50, 90, 330, 35);
        add(nameField);

        JLabel roomLabel = new JLabel("Room Type:");
        roomLabel.setBounds(50, 140, 100, 20);
        add(roomLabel);

        roomBox = new JComboBox<>(new String[]{"Standard - $100/night", "Deluxe - $200/night", "Suite - $350/night"});
        roomBox.setBounds(50, 160, 330, 35);
        add(roomBox);

        JLabel nightsLabel = new JLabel("No. of Nights:");
        nightsLabel.setBounds(50, 210, 100, 20);
        add(nightsLabel);

        nightsField = createRoundedTextField();
        nightsField.setBounds(50, 230, 330, 35);
        add(nightsField);

        bookBtn = createRoundedButton("Book Room");
        bookBtn.setBounds(50, 280, 150, 40);
        add(bookBtn);

        cancelBtn = createRoundedButton("Cancel Booking");
        cancelBtn.setBounds(230, 280, 150, 40);
        add(cancelBtn);

        viewBtn = createRoundedButton("View Bookings");
        viewBtn.setBounds(140, 330, 150, 35);
        add(viewBtn);

        bookBtn.addActionListener(e -> handleBooking());
        cancelBtn.addActionListener(e -> cancelBooking());
        viewBtn.addActionListener(e -> viewBookings());

        setVisible(true);
    }

    private void handleBooking() {
        String name = nameField.getText().trim();
        String nightsText = nightsField.getText().trim();

        if (name.isEmpty() || nightsText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            int nights = Integer.parseInt(nightsText);
            int costPerNight = switch (roomBox.getSelectedIndex()) {
                case 0 -> 100;
                case 1 -> 200;
                case 2 -> 350;
                default -> 0;
            };

            int totalCost = nights * costPerNight;
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Total Payment: $" + totalCost + "\nProceed to book?", "Confirm Booking",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String record = name + "," + roomBox.getSelectedItem() + "," + nights + "," + totalCost;
                BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
                writer.write(record);
                writer.newLine();
                writer.close();
                JOptionPane.showMessageDialog(this, "✅ Booking confirmed!");
                nameField.setText("");
                nightsField.setText("");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid number of nights.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cancelBooking() {
        String name = JOptionPane.showInputDialog(this, "Enter your name to cancel booking:");
        if (name == null || name.trim().isEmpty()) return;

        try {
            File input = new File(FILE_NAME);
            File temp = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(input));
            BufferedWriter writer = new BufferedWriter(new FileWriter(temp));

            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (!line.toLowerCase().startsWith(name.toLowerCase() + ",")) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    found = true;
                }
            }

            writer.close();
            reader.close();

            if (input.delete() && temp.renameTo(input)) {
                JOptionPane.showMessageDialog(this,
                        found ? "❌ Booking canceled." : "No booking found for this name.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void viewBookings() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            StringBuilder sb = new StringBuilder("📃 Booking Details:\n\n");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                sb.append("👤 Name: ").append(data[0])
                        .append("\n🏨 Room: ").append(data[1])
                        .append("\n🛏️ Nights: ").append(data[2])
                        .append("\n💰 Paid: $").append(data[3])
                        .append("\n-----------------------------\n");
            }
            reader.close();

            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            JScrollPane pane = new JScrollPane(area);
            pane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this, pane, "View All Bookings", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(btn.getBackground().darker());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(0, 123, 255));
            }
        });

        return btn;
    }
}
