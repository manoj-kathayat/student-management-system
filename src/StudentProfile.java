import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentProfile extends JPanel {

    private CardLayout mainCardLayout;
    private JPanel mainPanel;
    private CardLayout centerCardLayout;
    private JPanel centerCardPanel;
    private int currentStudentId = -1;
    private JLabel nameLabel, courseLabel, statusBadge, avatarLabel;
    private JPanel personalInfoPanel, feesSummaryPanel, admissionPanel, marksPanel, recentPaymentPanel;
    private JComboBox<String> semesterCombo;

    public StudentProfile(CardLayout mainCardLayout, JPanel mainPanel) {
        this.mainCardLayout = mainCardLayout;
        this.mainPanel = mainPanel;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 242, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        navPanel.setBackground(new Color(240, 242, 245));
        JButton backBtn = new JButton("← Back");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.setBackground(new Color(15, 45, 90));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel heading = new JLabel("Student Profile");
        heading.setFont(new Font("Arial", Font.BOLD, 36));
        navPanel.add(backBtn);
        navPanel.add(heading);
        topPanel.add(navPanel, BorderLayout.NORTH);

        JPanel profileSummary = createProfileSummaryPanel();
        topPanel.add(profileSummary, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonBar.setBackground(new Color(240, 242, 245));
        JButton personalBtn = new JButton("📋 Personal & Fees");
        JButton academicBtn = new JButton("📚 Academic & Marks");
        styleToggleButton(personalBtn);
        styleToggleButton(academicBtn);
        buttonBar.add(personalBtn);
        buttonBar.add(academicBtn);

        centerCardLayout = new CardLayout();
        centerCardPanel = new JPanel(centerCardLayout);
        centerCardPanel.setBackground(new Color(240, 242, 245));
        centerCardPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        personalInfoPanel = new JPanel();
        feesSummaryPanel = new JPanel();
        admissionPanel = new JPanel();
        marksPanel = new JPanel();
        recentPaymentPanel = new JPanel();

        JPanel personalFeesCard = createPersonalFeesCard();
        JPanel academicMarksCard = createAcademicMarksCard();

        centerCardPanel.add(personalFeesCard, "PERSONAL");
        centerCardPanel.add(academicMarksCard, "ACADEMIC");

        JScrollPane cardScrollPane = new JScrollPane(centerCardPanel);
        cardScrollPane.setBorder(null);
        cardScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        cardScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        actionPanel.setBackground(new Color(240, 242, 245));
        JButton editBtn = createActionButton("✎ Edit Student", new Color(52, 152, 219));
        JButton deleteBtn = createActionButton("🗑 Delete Student", new Color(231, 76, 60));
        JButton addPaymentBtn = createActionButton("+ Add Payment", new Color(46, 204, 113));
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(addPaymentBtn);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(new Color(240, 242, 245));
        centerWrapper.add(buttonBar, BorderLayout.NORTH);
        centerWrapper.add(cardScrollPane, BorderLayout.CENTER);
        centerWrapper.add(actionPanel, BorderLayout.SOUTH);
        add(centerWrapper, BorderLayout.CENTER);

        personalBtn.addActionListener(e -> centerCardLayout.show(centerCardPanel, "PERSONAL"));
        academicBtn.addActionListener(e -> centerCardLayout.show(centerCardPanel, "ACADEMIC"));
        centerCardLayout.show(centerCardPanel, "PERSONAL");

        backBtn.addActionListener(e -> mainCardLayout.show(mainPanel, "Students"));
        editBtn.addActionListener(e -> editStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        addPaymentBtn.addActionListener(e -> addPayment());
    }

    public void setStudentId(int id) {
        this.currentStudentId = id;
        loadStudentData();
    }

    private void loadStudentData() {
        if (currentStudentId == -1) return;
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentStudentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("full_name");
                nameLabel.setText(name);
                courseLabel.setText(rs.getString("course"));
                statusBadge.setText(" " + rs.getString("status") + " ");
                statusBadge.setBackground(rs.getString("status").equals("Active")
                        ? new Color(46, 204, 113) : Color.RED);
                avatarLabel.setText(name == null || name.isEmpty()
                        ? "?" : name.substring(0, 1).toUpperCase());
                updatePersonalInfo(rs);
                updateFeesSummary();
                updateAdmissionDetails(rs);
                updateMarksTable();
                updateRecentPayment();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private JPanel createProfileSummaryPanel() {
        JPanel profile = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        profile.setBackground(Color.WHITE);
        profile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        avatarLabel = new JLabel("?", SwingConstants.CENTER);
        avatarLabel.setPreferredSize(new Dimension(80, 80));
        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(new Color(15, 45, 90));
        avatarLabel.setForeground(Color.WHITE);
        avatarLabel.setFont(new Font("Arial", Font.BOLD, 36));
        avatarLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        nameLabel = new JLabel("Student Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        courseLabel = new JLabel("Course");
        courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        courseLabel.setForeground(Color.GRAY);
        textPanel.add(nameLabel);
        textPanel.add(courseLabel);

        statusBadge = new JLabel(" Active ");
        statusBadge.setOpaque(true);
        statusBadge.setBackground(new Color(46, 204, 113));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setFont(new Font("Arial", Font.BOLD, 16));
        statusBadge.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        profile.add(avatarLabel);
        profile.add(textPanel);
        profile.add(Box.createHorizontalStrut(20));
        profile.add(statusBadge);
        return profile;
    }

    private JPanel createPersonalFeesCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        personalInfoPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        personalInfoPanel.setBackground(Color.WHITE);
        personalInfoPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        feesSummaryPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        feesSummaryPanel.setBackground(Color.WHITE);
        feesSummaryPanel.setBorder(BorderFactory.createTitledBorder("Fees Summary"));
        card.add(personalInfoPanel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(feesSummaryPanel);
        return card;
    }

    private JPanel createAcademicMarksCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        admissionPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        admissionPanel.setBackground(Color.WHITE);
        admissionPanel.setBorder(BorderFactory.createTitledBorder("Admission Details"));

        JPanel marksWrapper = new JPanel(new BorderLayout());
        marksWrapper.setBackground(Color.WHITE);
        marksWrapper.setBorder(BorderFactory.createTitledBorder("Marks"));

        JPanel semBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        semBar.setBackground(Color.WHITE);
        JLabel semLbl = new JLabel("Semester:");
        semLbl.setFont(new Font("Arial", Font.BOLD, 14));
        semBar.add(semLbl);
        semesterCombo = new JComboBox<>(new String[]{
                "Semester 1", "Semester 2", "Semester 3", "Semester 4", "Semester 5", "Semester 6"
        });
        semesterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        semesterCombo.addActionListener(e -> updateMarksTable());
        semBar.add(semesterCombo);
        marksWrapper.add(semBar, BorderLayout.NORTH);

        marksPanel = new JPanel(new BorderLayout());
        marksPanel.setBackground(Color.WHITE);
        marksWrapper.add(marksPanel, BorderLayout.CENTER);

        recentPaymentPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        recentPaymentPanel.setBackground(Color.WHITE);
        recentPaymentPanel.setBorder(BorderFactory.createTitledBorder("Recent Payment"));

        card.add(admissionPanel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(marksWrapper);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(recentPaymentPanel);
        return card;
    }

    private void updatePersonalInfo(ResultSet rs) throws SQLException {
        personalInfoPanel.removeAll();
        addInfoRow(personalInfoPanel, "Student ID:", String.valueOf(rs.getInt("id")));
        addInfoRow(personalInfoPanel, "Email:", rs.getString("email"));
        addInfoRow(personalInfoPanel, "Phone:", rs.getString("phone"));
        addInfoRow(personalInfoPanel, "Gender:", rs.getString("gender"));
        Date dob = rs.getDate("dob");
        addInfoRow(personalInfoPanel, "Date of Birth:", dob == null ? "-" : dob.toString());
        addInfoRow(personalInfoPanel, "Address:", rs.getString("address"));
        personalInfoPanel.revalidate();
        personalInfoPanel.repaint();
    }

    private void updateFeesSummary() {
        feesSummaryPanel.removeAll();
        double totalPaid = 0;
        String lastPayment = "None";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT COALESCE(SUM(amount),0) AS total FROM fees_payments WHERE student_id = ?")) {
            pstmt.setInt(1, currentStudentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) totalPaid = rs.getDouble("total");
        } catch (SQLException ex) { ex.printStackTrace(); }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT amount, payment_date FROM fees_payments WHERE student_id = ? ORDER BY payment_date DESC LIMIT 1")) {
            pstmt.setInt(1, currentStudentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                lastPayment = "₹ " + rs.getDouble("amount") + " (" + rs.getDate("payment_date") + ")";
            }
        } catch (SQLException ex) { ex.printStackTrace(); }

        double totalFees = getTotalFeesForStudent();
        double pending = totalFees - totalPaid;
        addInfoRow(feesSummaryPanel, "Total Fees:", "₹ " + totalFees);
        addInfoRow(feesSummaryPanel, "Paid Amount:", "₹ " + totalPaid);
        addInfoRow(feesSummaryPanel, "Pending Amount:", "₹ " + pending);
        addInfoRow(feesSummaryPanel, "Last Payment:", lastPayment);
        feesSummaryPanel.revalidate();
        feesSummaryPanel.repaint();
    }

    private double getTotalFeesForStudent() {
        String sql = "SELECT c.total_fee FROM students s JOIN courses c ON s.course = c.course_name WHERE s.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentStudentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("total_fee");
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }

    private void updateAdmissionDetails(ResultSet rs) throws SQLException {
        admissionPanel.removeAll();
        Date adm = rs.getDate("admission_date");
        addInfoRow(admissionPanel, "Admission Date:", adm == null ? "-" : adm.toString());
        addInfoRow(admissionPanel, "Enrollment No.:", "ENR" + rs.getInt("id"));
        addInfoRow(admissionPanel, "Course:", rs.getString("course"));
        addInfoRow(admissionPanel, "Status:", rs.getString("status"));
        admissionPanel.revalidate();
        admissionPanel.repaint();
    }

    private void updateMarksTable() {
        if (currentStudentId == -1) return;
        String semester = (String) semesterCombo.getSelectedItem();
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Subject", "Internal (30)", "External (70)", "Total (100)"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        String sql = "SELECT subject, internal_marks, external_marks FROM marks WHERE student_id = ? AND semester = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentStudentId);
            pstmt.setString(2, semester);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int i = rs.getInt("internal_marks");
                int e = rs.getInt("external_marks");
                model.addRow(new Object[]{rs.getString("subject"), i, e, i + e});
            }
        } catch (SQLException ex) { ex.printStackTrace(); }

        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(800, 220));
        marksPanel.removeAll();
        marksPanel.add(sp, BorderLayout.CENTER);
        marksPanel.revalidate();
        marksPanel.repaint();
    }

    private void updateRecentPayment() {
        recentPaymentPanel.removeAll();
        String sql = "SELECT payment_id, payment_mode, amount, receipt_no FROM fees_payments WHERE student_id = ? ORDER BY payment_date DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentStudentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                addInfoRow(recentPaymentPanel, "Payment ID:", rs.getString("payment_id"));
                addInfoRow(recentPaymentPanel, "Mode:", rs.getString("payment_mode"));
                addInfoRow(recentPaymentPanel, "Amount:", "₹ " + rs.getDouble("amount"));
                addInfoRow(recentPaymentPanel, "Receipt No.:", rs.getString("receipt_no"));
            } else {
                addInfoRow(recentPaymentPanel, "No payments", "");
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        recentPaymentPanel.revalidate();
        recentPaymentPanel.repaint();
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel val = new JLabel(value == null ? "-" : value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(lbl);
        panel.add(val);
    }

    private void editStudent() {
        if (currentStudentId == -1) return;

        String name = "", email = "", phone = "", course = "", gender = "", address = "", status = "";
        java.sql.Date dob = null, adm = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM students WHERE id = ?")) {
            pstmt.setInt(1, currentStudentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("full_name");
                email = rs.getString("email");
                phone = rs.getString("phone");
                dob = rs.getDate("dob");
                course = rs.getString("course");
                gender = rs.getString("gender");
                address = rs.getString("address");
                adm = rs.getDate("admission_date");
                status = rs.getString("status");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage());
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Student", true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Full Name:"), gbc);
        JTextField nameField = new JTextField(name);
        gbc.gridx = 1; form.add(nameField, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Email:"), gbc);
        JTextField emailField = new JTextField(email);
        gbc.gridx = 1; form.add(emailField, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Phone:"), gbc);
        JTextField phoneField = new JTextField(phone);
        gbc.gridx = 1; form.add(phoneField, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Course:"), gbc);
        JComboBox<String> courseBox = new JComboBox<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT course_name FROM courses WHERE status='Active' OR course_name='" + course + "' ORDER BY course_name")) {
            while (rs.next()) courseBox.addItem(rs.getString("course_name"));
        } catch (SQLException ex) { ex.printStackTrace(); }
        courseBox.setSelectedItem(course);
        gbc.gridx = 1; form.add(courseBox, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Gender:"), gbc);
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderBox.setSelectedItem(gender);
        gbc.gridx = 1; form.add(genderBox, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Address:"), gbc);
        JTextField addressField = new JTextField(address);
        gbc.gridx = 1; form.add(addressField, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Status:"), gbc);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active", "DeActive"});
        statusBox.setSelectedItem(status);
        gbc.gridx = 1; form.add(statusBox, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Date of Birth:"), gbc);
        SpinnerDateModel dobModel = new SpinnerDateModel();
        JSpinner dobSpinner = new JSpinner(dobModel);
        dobSpinner.setEditor(new JSpinner.DateEditor(dobSpinner, "yyyy-MM-dd"));
        if (dob != null) dobSpinner.setValue(dob);
        gbc.gridx = 1; form.add(dobSpinner, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Admission Date:"), gbc);
        SpinnerDateModel admModel = new SpinnerDateModel();
        JSpinner admSpinner = new JSpinner(admModel);
        admSpinner.setEditor(new JSpinner.DateEditor(admSpinner, "yyyy-MM-dd"));
        if (adm != null) admSpinner.setValue(adm);
        gbc.gridx = 1; form.add(admSpinner, gbc); y++;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        saveBtn.setBackground(new Color(15, 45, 90));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 15));
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 15));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        final int studentId = currentStudentId;
        saveBtn.addActionListener(e -> {
            String sql = "UPDATE students SET full_name=?, email=?, phone=?, dob=?, course=?, gender=?, address=?, admission_date=?, status=? WHERE id=?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, emailField.getText().trim());
                pstmt.setString(3, phoneField.getText().trim());
                pstmt.setDate(4, new java.sql.Date(((java.util.Date) dobSpinner.getValue()).getTime()));
                pstmt.setString(5, (String) courseBox.getSelectedItem());
                pstmt.setString(6, (String) genderBox.getSelectedItem());
                pstmt.setString(7, addressField.getText().trim());
                pstmt.setDate(8, new java.sql.Date(((java.util.Date) admSpinner.getValue()).getTime()));
                pstmt.setString(9, (String) statusBox.getSelectedItem());
                pstmt.setInt(10, studentId);
                pstmt.executeUpdate();
                loadStudentData();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Update failed: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void deleteStudent() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM students WHERE id = ?")) {
                pstmt.setInt(1, currentStudentId);
                pstmt.executeUpdate();
                mainCardLayout.show(mainPanel, "Students");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
            }
        }
    }

    private void addPayment() {
        if (currentStudentId == -1) return;

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
        gbc.gridx = 1; form.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Payment Mode:"), gbc);
        JComboBox<String> modeBox = new JComboBox<>(new String[]{"Online", "Cash", "Cheque", "Card"});
        gbc.gridx = 1; form.add(modeBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Receipt No.:"), gbc);
        JTextField receiptField = new JTextField();
        gbc.gridx = 1; form.add(receiptField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton saveBtn = new JButton("Save Payment");
        JButton cancelBtn = new JButton("Cancel");
        saveBtn.setBackground(new Color(15, 45, 90));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 15));
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 15));
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        final int studentId = currentStudentId;
        saveBtn.addActionListener(e -> {
            String amtStr = amountField.getText().trim();
            String receipt = receiptField.getText().trim();
            if (amtStr.isEmpty() || receipt.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields.");
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amtStr);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount.");
                return;
            }
            String paymentId = "PAY" + System.currentTimeMillis();
            String sql = "INSERT INTO fees_payments (payment_id, student_id, amount, payment_mode, receipt_no, payment_date) VALUES (?,?,?,?,?,CURDATE())";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, paymentId);
                pstmt.setInt(2, studentId);
                pstmt.setDouble(3, amount);
                pstmt.setString(4, (String) modeBox.getSelectedItem());
                pstmt.setString(5, receipt);
                pstmt.executeUpdate();
                updateFeesSummary();
                updateRecentPayment();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Insert failed: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void styleToggleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(15, 45, 90));
        btn.setBorder(BorderFactory.createLineBorder(new Color(15, 45, 90), 2));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 45));
    }

    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 45));
        return btn;
    }
}