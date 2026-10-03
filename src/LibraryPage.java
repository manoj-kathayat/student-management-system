import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class LibraryPage extends JPanel {

    private JTable booksTable;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public LibraryPage(CardLayout cardLayout, JPanel mainPanel) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 242, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        JLabel heading = new JLabel("Library Management");
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

        JTextField searchField = new JTextField("Search by title, author or ID...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search by title, author or ID...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search by title, author or ID...");
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

        JButton addBookBtn = new JButton("+ Add Book");
        addBookBtn.setForeground(Color.WHITE);
        addBookBtn.setFont(new Font("Arial", Font.BOLD, 16));
        addBookBtn.setFocusPainted(false);
        addBookBtn.setBackground(new Color(15, 45, 90));
        gbc.gridx = 2;
        actionBar.add(addBookBtn, gbc);

        centerPanel.add(actionBar, BorderLayout.NORTH);

        String[] columns = {"Book ID", "Title", "Author", "Status", "Issued To", "Issue Date", "Due Date", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        booksTable = new JTable(tableModel);
        booksTable.setRowHeight(45);
        booksTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        booksTable.setFocusable(false);
        booksTable.getTableHeader().setBackground(new Color(15, 45, 90));
        booksTable.getTableHeader().setForeground(Color.WHITE);
        booksTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        booksTable.getTableHeader().setPreferredSize(new Dimension(100, 45));
        booksTable.setShowGrid(false);
        booksTable.setIntercellSpacing(new Dimension(0, 0));
        booksTable.setSelectionBackground(new Color(220, 235, 252));

        booksTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        booksTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
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

        booksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = booksTable.rowAtPoint(e.getPoint());
                int col = booksTable.columnAtPoint(e.getPoint());
                if (col == 7 && row >= 0) {
                    String status = tableModel.getValueAt(row, 3).toString();
                    if (status.equals("Available")) {
                        issueBook(row);
                    } else if (status.equals("Issued")) {
                        returnBook(row);
                    } else {
                        deleteBook(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadBooks();

        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty() || query.equals("Search by title, author or ID...")) {
                loadBooks();
            } else {
                searchBooks(query);
            }
        });
        addBookBtn.addActionListener(e -> openAddBookDialog());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadBooks();
            }
        });
    }

    private void loadBooks() {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM library_books";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String issueDate = rs.getDate("issue_date") != null ? dateFormat.format(rs.getDate("issue_date")) : "-";
                String dueDate = rs.getDate("due_date") != null ? dateFormat.format(rs.getDate("due_date")) : "-";
                tableModel.addRow(new Object[]{
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("status"),
                    rs.getString("issued_to") != null ? rs.getString("issued_to") : "-",
                    issueDate,
                    dueDate,
                    rs.getString("status").equals("Available") ? "Issue | Delete" : "Return | Delete"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    private void searchBooks(String query) {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM library_books WHERE title LIKE ? OR author LIKE ? OR book_id LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String like = "%" + query + "%";
            pstmt.setString(1, like);
            pstmt.setString(2, like);
            pstmt.setString(3, like);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String issueDate = rs.getDate("issue_date") != null ? dateFormat.format(rs.getDate("issue_date")) : "-";
                String dueDate = rs.getDate("due_date") != null ? dateFormat.format(rs.getDate("due_date")) : "-";
                tableModel.addRow(new Object[]{
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("status"),
                    rs.getString("issued_to") != null ? rs.getString("issued_to") : "-",
                    issueDate,
                    dueDate,
                    rs.getString("status").equals("Available") ? "Issue | Delete" : "Return | Delete"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage());
        }
    }

    private void openAddBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Book", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Book ID:"), gbc);
        JTextField idField = new JTextField();
        idField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Title:"), gbc);
        JTextField titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Author:"), gbc);
        JTextField authorField = new JTextField();
        authorField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(authorField, gbc);

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
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String sql = "INSERT INTO library_books (book_id, title, author, status) VALUES (?,?,?,'Available')";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.setString(2, title);
                pstmt.setString(3, author);
                pstmt.executeUpdate();
                loadBooks();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Insert failed: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void issueBook(int row) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Issue Book", true);
        dialog.setSize(500, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Select Student:"), gbc);
        String[] students = getStudentList();
        JComboBox<String> studentBox = new JComboBox<>(students);
        studentBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        gbc.gridx = 1; form.add(studentBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Due Date:"), gbc);
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dueDateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dueDateSpinner, "dd/MM/yyyy");
        dueDateSpinner.setEditor(editor);
        dueDateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 14);
        dueDateSpinner.setValue(cal.getTime());
        gbc.gridx = 1; form.add(dueDateSpinner, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton issueBtn = new JButton("Issue");
        JButton cancelBtn = new JButton("Cancel");
        issueBtn.setFont(new Font("Arial", Font.BOLD, 16));
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        issueBtn.setBackground(new Color(15, 45, 90));
        issueBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(Color.LIGHT_GRAY);
        buttonPanel.add(issueBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        issueBtn.addActionListener(e -> {
            String student = (String) studentBox.getSelectedItem();
            Date dueDate = (Date) dueDateSpinner.getValue();
            String bookId = tableModel.getValueAt(row, 0).toString();
            String sql = "UPDATE library_books SET status='Issued', issued_to=?, issue_date=CURDATE(), due_date=? WHERE book_id=?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, student);
                pstmt.setDate(2, new java.sql.Date(dueDate.getTime()));
                pstmt.setString(3, bookId);
                pstmt.executeUpdate();
                loadBooks();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Issue failed: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void returnBook(int row) {
        int confirm = JOptionPane.showConfirmDialog(this, "Return this book?", "Confirm Return", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String bookId = tableModel.getValueAt(row, 0).toString();
            String sql = "UPDATE library_books SET status='Available', issued_to=NULL, issue_date=NULL, due_date=NULL WHERE book_id=?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, bookId);
                pstmt.executeUpdate();
                loadBooks();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Return failed: " + ex.getMessage());
            }
        }
    }

    private void deleteBook(int row) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete book " + tableModel.getValueAt(row, 1) + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String bookId = tableModel.getValueAt(row, 0).toString();
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM library_books WHERE book_id=?")) {
                pstmt.setString(1, bookId);
                pstmt.executeUpdate();
                loadBooks();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
            }
        }
    }

    private String[] getStudentList() {
        java.util.ArrayList<String> list = new java.util.ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, full_name FROM students")) {
            while (rs.next()) {
                list.add(rs.getString("full_name") + " (" + rs.getInt("id") + ")");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list.toArray(new String[0]);
    }
}