import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class StudentGradeTracker extends JFrame {
    private JTextField usernameField, nameField, scoreField;
    private JPasswordField passwordField;
    private ArrayList<Student> students = new ArrayList<>();
    private final String teacherUsername = "teacher";
    private final String teacherPassword = "admin";
    private final String GRADES_FILE = "grades.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentGradeTracker::new);
    }

    public StudentGradeTracker() {
        showLoginInterface();
    }

    private void showLoginInterface() {
        setTitle("Teacher Login");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 248, 255));
        setResizable(true);

        JLabel heading = new JLabel("Teacher Login", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setBounds(90, 20, 240, 30);
        add(heading);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 70, 150, 20);
        add(userLabel);

        usernameField = createRoundedTextField();
        usernameField.setBounds(50, 90, 300, 35);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 130, 150, 20);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 150, 300, 35);
        add(passwordField);

        JButton loginBtn = createRoundedButton("Login");
        loginBtn.setBounds(140, 200, 120, 40);
        add(loginBtn);

        loginBtn.addActionListener(e -> handleLogin());

        setVisible(true);
    }

    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = String.valueOf(passwordField.getPassword()).trim();

        if (user.equals(teacherUsername) && pass.equals(teacherPassword)) {
            getContentPane().removeAll();
            repaint();
            showGradeTrackerUI();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials.");
        }
    }

    private void showGradeTrackerUI() {
        setTitle("Student Grade Tracker");
        setSize(420, 350);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 248, 255));

        JLabel heading = new JLabel("Student Grade Tracker", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setBounds(90, 15, 240, 30);
        add(heading);

        JLabel nameLabel = new JLabel("Student Name:");
        nameLabel.setBounds(50, 60, 150, 20);
        add(nameLabel);

        nameField = createRoundedTextField();
        nameField.setBounds(50, 80, 300, 35);
        add(nameField);

        JLabel scoreLabel = new JLabel("Score (0–100):");
        scoreLabel.setBounds(50, 120, 150, 20);
        add(scoreLabel);

        scoreField = createRoundedTextField();
        scoreField.setBounds(50, 140, 300, 35);
        add(scoreField);

        JButton addBtn = createRoundedButton("Add Student");
        addBtn.setBounds(50, 190, 135, 40);
        add(addBtn);

        JButton reportBtn = createRoundedButton("Generate Report");
        reportBtn.setBounds(215, 190, 135, 40);
        add(reportBtn);

        addBtn.addActionListener(e -> addStudent());
        reportBtn.addActionListener(e -> showReportWindow());

        setVisible(true);
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String scoreText = scoreField.getText().trim();

        if (name.isEmpty() || scoreText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter name and score.");
            return;
        }

        try {
            int score = Integer.parseInt(scoreText);
            if (score < 0 || score > 100) throw new NumberFormatException();

            Student student = new Student(name, score);
            students.add(student);
            appendGradeToFile(student);

            nameField.setText("");
            scoreField.setText("");
            JOptionPane.showMessageDialog(this, "✅ Student added and saved.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid score between 0 and 100.");
        }
    }

    private void appendGradeToFile(Student student) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GRADES_FILE, true))) {
            writer.write(student.getName() + "," + student.getScore());
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file.");
        }
    }

    private void showReportWindow() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ No students added yet.");
            return;
        }

        JDialog reportDialog = new JDialog(this, "Summary Report", true);
        reportDialog.setSize(400, 350);
        reportDialog.setLocationRelativeTo(this);
        reportDialog.setLayout(new BorderLayout());

        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        reportArea.setEditable(false);
        reportArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        reportDialog.add(scrollPane, BorderLayout.CENTER);

        int total = 0, high = Integer.MIN_VALUE, low = Integer.MAX_VALUE;
        String highName = "", lowName = "";

        StringBuilder sb = new StringBuilder("📊 Summary Report\n----------------------\n");
        for (Student s : students) {
            int sc = s.getScore();
            sb.append(s.getName()).append(" → ").append(sc).append("\n");
            total += sc;

            if (sc > high) {
                high = sc;
                highName = s.getName();
            }
            if (sc < low) {
                low = sc;
                lowName = s.getName();
            }
        }

        double avg = total / (double) students.size();
        sb.append("----------------------\n");
        sb.append(String.format("📈 Average Score: %.2f\n", avg));
        sb.append("🏅 Highest: " + highName + " (" + high + ")\n");
        sb.append("📉 Lowest: " + lowName + " (" + low + ")\n");

        reportArea.setText(sb.toString());
        reportDialog.setVisible(true);
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

    static class Student {
        private final String name;
        private final int score;

        public Student(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }
}
