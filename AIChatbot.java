import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AIChatbot extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AIChatbot::new);
    }

    public AIChatbot() {
        setTitle("AI Chatbot");
        setSize(500, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(true);  
        getContentPane().setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("AI Chat Assistant", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(150, 20, 200, 30);
        add(title);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setBounds(30, 60, 420, 340);
        add(scroll);

        inputField = createRoundedTextField();
        inputField.setBounds(30, 420, 305, 35);
        add(inputField);

        JButton sendBtn = createRoundedButton("Send");
        sendBtn.setBounds(345, 420, 105, 35);
        add(sendBtn);

        sendBtn.addActionListener(e -> respond());

        botSay("👋 Hello! Ask me anything.");
        setVisible(true);
    }

    private void respond() {
        String userInput = inputField.getText().trim().toLowerCase();
        if (userInput.isEmpty()) return;

        userSay(userInput);
        String reply = getReply(userInput);
        botSay(reply);
        inputField.setText("");
    }

    private String getReply(String input) {
        if (input.contains("hello") || input.contains("hi") || input.contains("hey")) {
            return "Hello! How can I assist you today?";
        } else if (input.contains("java")) {
            return "Java is a popular object-oriented programming language.";
        } else if (input.contains("your name")) {
            return "I'm ChatBot, your Java assistant.";
        } else if (input.contains("grade")) {
            return "I can help you manage student grades and performance.";
        } else if (input.contains("bye") || input.contains("goodbye")) {
            return "Goodbye! See you soon.";
        } else if (input.contains("thank")) {
            return "You're welcome!";
        } else {
            return "Sorry, I didn’t get that. Please try asking differently.";
        }
    }

    private void userSay(String msg) {
        chatArea.append("👤 You: " + msg + "\n");
    }

    private void botSay(String msg) {
        chatArea.append(" Bot: " + msg + "\n\n");
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
