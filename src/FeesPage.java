import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FeesPage extends JPanel {

    private JComboBox<String> studentCombo;
    private JLabel totalFeesLabel, paidFeesLabel, pendingFeesLabel;
    private JTable paymentTable;
    private DefaultTableModel paymentModel;
    private JTextArea monthlyCollectionArea;
    private int currentStudentId;

    public FeesPage(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 242, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        JLabel heading = new JLabel("Fees Management");
        heading.setFont(new Font("Arial", Font.BOLD, 42));
        topPanel.add(heading, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(240, 242, 245));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel selectPanel = new JPanel(new GridBagLayout());
        selectPanel.setBackground(Color.WHITE);
        selectPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel studentLabel = new JLabel("Select Student:");
        studentLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectPanel.add(studentLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        studentCombo = new JComboBox<>();
        studentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        selectPanel.add(studentCombo, gbc);
        centerPanel.add(selectPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(new Color(240, 242, 245));
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        totalFeesLabel = createFeeCard(summaryPanel, "Total Fees", "₹ 0", new Color(52, 152, 219));
        paidFeesLabel = createFeeCard(summaryPanel, "Paid Amount", "₹ 0", new Color(46, 204, 113));
        pendingFeesLabel = createFeeCard(summaryPanel, "Pending Amount", "₹ 0", new Color(231, 76, 60));
        centerPanel.add(summaryPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] paymentColumns = {"Payment ID", "Date", "Amount", "Mode", "Receipt No."};
        paymentModel = new DefaultTableModel(paymentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        paymentTable = new JTable(paymentModel);
        paymentTable.setRowHeight(40);
        paymentTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        paymentTable.getTableHeader().setBackground(new Color(15, 45, 90));
        paymentTable.getTableHeader().setForeground(Color.WHITE);
        paymentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        paymentTable.setShowGrid(true);
        paymentTable.setGridColor(new Color(220, 220, 220));
        JScrollPane tableScroll = new JScrollPane(paymentTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Payment History"));
        centerPanel.add(tableScroll);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        monthlyCollectionArea = new JTextArea(8, 40);
        monthlyCollectionArea.setEditable(false);
        monthlyCollectionArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        monthlyCollectionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane summaryScroll = new JScrollPane(monthlyCollectionArea);
        summaryScroll.setBorder(BorderFactory.createTitledBorder("Monthly Fee Collection (This Month)"));
        centerPanel.add(summaryScroll);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addPaymentBtn = new JButton("+ Add Payment");
        addPaymentBtn.setBackground(new Color(15, 45, 90));
        addPaymentBtn.setForeground(Color.WHITE);
        addPaymentBtn.setFont(new Font("Arial", Font.BOLD, 18));
        addPaymentBtn.setPreferredSize(new Dimension(180, 45));
        addPaymentBtn.addActionListener(e -> openAddPaymentDialog());
        buttonPanel.add(addPaymentBtn);
        centerPanel.add(buttonPanel);

        loadStudentCombo();
        studentCombo.addActionListener(e -> {
            if (studentCombo.getSelectedItem() != null) {
                String selected = (String) studentCombo.getSelectedItem();
                currentStudentId = Integer.parseInt(selected.split("\\(")[1].replace(")", ""));
                loadFeesData();
            }
        });

        if (studentCombo.getItemCount() > 0) {
            studentCombo.setSelectedIndex(-1);
            studentCombo.setSelectedIndex(0);
        }
        loadMonthlyCollection();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadStudentCombo();
                if (studentCombo.getItemCount() > 0) {
                    studentCombo.setSelectedIndex(-1);
                    studentCombo.setSelectedIndex(0);
                }
                loadMonthlyCollection();
            }
        });
    }

    private void loadStudentCombo() {
        studentCombo.removeAllItems();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, full_name FROM students")) {
            while (rs.next()) {
                studentCombo.addItem(rs.getString("full_name") + " (" + rs.getInt("id") + ")");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load students: " + ex.getMessage());
        }
    }

    private void loadFeesData() {
        if (currentStudentId == 0) return;
        double totalFee = 0;
        String courseSql = "SELECT c.total_fee FROM students s JOIN courses c ON s.course = c.course_name WHERE s.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(courseSql)) {
            pstmt.setInt(1, currentStudentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) totalFee = rs.getDouble("total_fee");
        } catch (SQLException ex) { ex.printStackTrace(); }

        double paid = 0;
        String paidSql = "SELECT COALESCE(SUM(amount),0) AS total FROM fees_payments WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(paidSql)) {
            pstmt.setInt(1, currentStudentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) paid = rs.getDouble("total");
        } catch (SQLException ex) { ex.printStackTrace(); }

        totalFeesLabel.setText("₹ " + totalFee);
        paidFeesLabel.setText("₹ " + paid);
        pendingFeesLabel.setText("₹ " + (totalFee - paid));

        paymentModel.setRowCount(0);
        String paySql = "SELECT payment_id, payment_date, amount, payment_mode, receipt_no FROM fees_payments WHERE student_id = ? ORDER BY payment_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(paySql)) {
            pstmt.setInt(1, currentStudentId);
            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            while (rs.next()) {
                paymentModel.addRow(new Object[]{
                    rs.getString("payment_id"),
                    sdf.format(rs.getDate("payment_date")),
                    "₹ " + rs.getBigDecimal("amount"),
                    rs.getString("payment_mode"),
                    rs.getString("receipt_no")
                });
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadMonthlyCollection() {
        StringBuilder sb = new StringBuilder();
        sb.append("Date\t\tAmount\n");
        String sql = "SELECT DATE(payment_date) as date, SUM(amount) as total FROM fees_payments WHERE MONTH(payment_date) = MONTH(CURDATE()) AND YEAR(payment_date) = YEAR(CURDATE()) GROUP BY DATE(payment_date)";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            double grandTotal = 0;
            while (rs.next()) {
                sb.append(rs.getDate("date")).append("\t\t₹ ").append(rs.getBigDecimal("total")).append("\n");
                grandTotal += rs.getDouble("total");
            }
            sb.append("--------------------------------\n");
            sb.append("Total:\t\t₹ ").append(grandTotal);
        } catch (SQLException ex) { ex.printStackTrace(); }
        monthlyCollectionArea.setText(sb.toString());
    }

    private JLabel createFeeCard(JPanel parent, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        parent.add(card);
        return valueLabel;
    }

    private void openAddPaymentDialog() {
        if (currentStudentId == 0) {
            JOptionPane.showMessageDialog(this, "Select a student first.");
            return;
        }
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Payment", true);
        dialog.setSize(500, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Amount (₹):"), gbc);
        JTextField amountField = new JTextField();
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Payment Mode:"), gbc);
        JComboBox<String> modeBox = new JComboBox<>(new String[]{"Online", "Cash", "Cheque", "Card"});
        modeBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(modeBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Receipt No.:"), gbc);
        JTextField receiptField = new JTextField();
        receiptField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(receiptField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton saveBtn = new JButton("Save Payment");
        JButton cancelBtn = new JButton("Cancel");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 16));
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        saveBtn.setBackground(new Color(15, 45, 90));
        saveBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(Color.LIGHT_GRAY);
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveBtn.addActionListener(e -> {
            String amountStr = amountField.getText().trim();
            String mode = (String) modeBox.getSelectedItem();
            String receipt = receiptField.getText().trim();
            if (amountStr.isEmpty() || receipt.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String paymentId = "PAY" + System.currentTimeMillis();
            String sql = "INSERT INTO fees_payments (payment_id, student_id, amount, payment_mode, receipt_no, payment_date) VALUES (?,?,?,?,?,CURDATE())";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, paymentId);
                pstmt.setInt(2, currentStudentId);
                pstmt.setDouble(3, amount);
                pstmt.setString(4, mode);
                pstmt.setString(5, receipt);
                pstmt.executeUpdate();
                loadFeesData();
                loadMonthlyCollection();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Insert failed: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
}