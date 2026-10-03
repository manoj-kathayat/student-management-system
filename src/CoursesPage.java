import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CoursesPage extends JPanel {

    private JTable coursesTable;
    private DefaultTableModel tableModel;

    public CoursesPage(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 242, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        JLabel heading = new JLabel("Courses Management");
        heading.setFont(new Font("Arial", Font.BOLD, 42));
        topPanel.add(heading, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(240, 242, 245));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        JPanel actionBar = new JPanel(new GridBagLayout());
        actionBar.setBackground(new Color(240, 242, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField searchField = new JTextField("Search Course...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search Course...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search Course...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        actionBar.add(searchField, gbc);

        JButton searchBtn = new JButton("Search");
        searchBtn.setForeground(Color.BLACK);
        searchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchBtn.setFocusPainted(false);
        searchBtn.setBackground(Color.WHITE);
        gbc.gridx = 1;
        gbc.weightx = 0;
        actionBar.add(searchBtn, gbc);

        JButton addBtn = new JButton("+ Add Course");
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 16));
        addBtn.setFocusPainted(false);
        addBtn.setBackground(new Color(15, 45, 90));
        gbc.gridx = 2;
        actionBar.add(addBtn, gbc);

        centerPanel.add(actionBar, BorderLayout.NORTH);

        String[] columns = {"Course ID", "Course Name", "Duration", "Total Fee", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        coursesTable = new JTable(tableModel);
        coursesTable.setRowHeight(45);
        coursesTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        coursesTable.setFocusable(false);
        coursesTable.getTableHeader().setBackground(new Color(15, 45, 90));
        coursesTable.getTableHeader().setForeground(Color.WHITE);
        coursesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        coursesTable.getTableHeader().setPreferredSize(new Dimension(100, 45));
        coursesTable.setShowGrid(false);
        coursesTable.setIntercellSpacing(new Dimension(0, 0));
        coursesTable.setSelectionBackground(new Color(220, 235, 252));

        coursesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                }
                return c;
            }
        });

        coursesTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Segoe UI", Font.BOLD, 15));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    label.setBackground(new Color(220, 235, 252));
                    label.setOpaque(true);
                } else {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                    label.setOpaque(true);
                }
                label.setForeground(new Color(52, 152, 219));
                return label;
            }
        });

        coursesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = coursesTable.rowAtPoint(e.getPoint());
                int col = coursesTable.columnAtPoint(e.getPoint());
                if (col == 5 && row >= 0) {
                    int x = e.getX();
                    int cellX = coursesTable.getCellRect(row, col, true).x;
                    if (x < cellX + 50) {
                        editCourse(row);
                    } else {
                        deleteCourse(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadCourses();

        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty() || query.equals("Search Course...")) {
                loadCourses();
            } else {
                searchCourses(query);
            }
        });
        addBtn.addActionListener(e -> openAddCourseDialog());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadCourses();
            }
        });
    }

    private void loadCourses() {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM courses";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getString("duration"),
                    "₹ " + rs.getBigDecimal("total_fee"),
                    rs.getString("status"),
                    "Edit | Delete"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void searchCourses(String query) {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM courses WHERE course_name LIKE ? OR course_id LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String like = "%" + query + "%";
            pstmt.setString(1, like);
            pstmt.setString(2, like);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getString("duration"),
                    "₹ " + rs.getBigDecimal("total_fee"),
                    rs.getString("status"),
                    "Edit | Delete"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage());
        }
    }

    private void openAddCourseDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Course", true);
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Course ID:"), gbc);
        JTextField idField = new JTextField();
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Course Name:"), gbc);
        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Duration:"), gbc);
        JComboBox<String> durationBox = new JComboBox<>(new String[]{"1 Year", "2 Years", "3 Years", "4 Years"});
        durationBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(durationBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Total Fee (₹):"), gbc);
        JTextField feeField = new JTextField();
        feeField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(feeField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; form.add(new JLabel("Status:"), gbc);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active", "Inactive"});
        statusBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(statusBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton saveBtn = new JButton("Save");
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
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String duration = (String) durationBox.getSelectedItem();
            String fee = feeField.getText().trim();
            String status = (String) statusBox.getSelectedItem();
            if (id.isEmpty() || name.isEmpty() || fee.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String sql = "INSERT INTO courses (course_id, course_name, duration, total_fee, status) VALUES (?,?,?,?,?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, duration);
                pstmt.setBigDecimal(4, new java.math.BigDecimal(fee));
                pstmt.setString(5, status);
                pstmt.executeUpdate();
                loadCourses();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Insert failed: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void editCourse(int row) {
        String id = tableModel.getValueAt(row, 0).toString();
        String name = tableModel.getValueAt(row, 1).toString();
        String duration = tableModel.getValueAt(row, 2).toString();
        String fee = tableModel.getValueAt(row, 3).toString().replace("₹ ", "");
        String status = tableModel.getValueAt(row, 4).toString();

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Course", true);
        dialog.setSize(500, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Course ID:"), gbc);
        JTextField idField = new JTextField(id);
        idField.setEditable(false);
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Course Name:"), gbc);
        JTextField nameField = new JTextField(name);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Duration:"), gbc);
        JComboBox<String> durationBox = new JComboBox<>(new String[]{"1 Year", "2 Years", "3 Years", "4 Years"});
        durationBox.setSelectedItem(duration);
        durationBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(durationBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Total Fee (₹):"), gbc);
        JTextField feeField = new JTextField(fee);
        feeField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(feeField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; form.add(new JLabel("Status:"), gbc);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active", "Inactive"});
        statusBox.setSelectedItem(status);
        statusBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(statusBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton updateBtn = new JButton("Update");
        JButton cancelBtn = new JButton("Cancel");
        updateBtn.setFont(new Font("Arial", Font.BOLD, 16));
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        updateBtn.setBackground(new Color(15, 45, 90));
        updateBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(Color.LIGHT_GRAY);
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        updateBtn.addActionListener(e -> {
            String sql = "UPDATE courses SET course_name=?, duration=?, total_fee=?, status=? WHERE course_id=?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, (String) durationBox.getSelectedItem());
                pstmt.setBigDecimal(3, new java.math.BigDecimal(feeField.getText().trim()));
                pstmt.setString(4, (String) statusBox.getSelectedItem());
                pstmt.setString(5, id);
                pstmt.executeUpdate();
                loadCourses();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Update failed: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void deleteCourse(int row) {
        String courseId = tableModel.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete course " + tableModel.getValueAt(row, 1) + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM courses WHERE course_id = ?")) {
                pstmt.setString(1, courseId);
                pstmt.executeUpdate();
                loadCourses();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
            }
        }
    }
}