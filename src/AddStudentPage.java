import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddStudentPage extends JPanel {

    private JPanel formContainer;
    private JComboBox<String> courseBox;

    AddStudentPage() {
        setLayout(null);
        setBackground(new Color(240, 242, 245));

        formContainer = new JPanel();
        formContainer.setLayout(null);
        formContainer.setBackground(new Color(240, 242, 245));
        add(formContainer);

        JLabel addHeading = new JLabel("Add Student");
        addHeading.setFont(new Font("Arial", Font.BOLD, 42));
        addHeading.setBounds(40, 20, 400, 60);
        formContainer.add(addHeading);

        // Full Name
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        nameLabel.setBounds(60, 100, 200, 35);
        JTextField nameField = new JTextField();
        nameField.setBounds(250, 95, 350, 45);
        nameField.setFont(new Font("Arial", Font.PLAIN, 20));
        formContainer.add(nameLabel);
        formContainer.add(nameField);

        // Email
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 22));
        emailLabel.setBounds(60, 165, 200, 35);
        JTextField emailField = new JTextField();
        emailField.setBounds(250, 160, 350, 45);
        emailField.setFont(new Font("Arial", Font.PLAIN, 20));
        formContainer.add(emailLabel);
        formContainer.add(emailField);

        // Phone
        JLabel phoneLabel = new JLabel("Phone");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 22));
        phoneLabel.setBounds(60, 230, 200, 35);
        JTextField phoneField = new JTextField();
        phoneField.setBounds(250, 225, 350, 45);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 20));
        formContainer.add(phoneLabel);
        formContainer.add(phoneField);

        // Date of Birth
        JLabel dobLabel = new JLabel("Date Of Birth");
        dobLabel.setFont(new Font("Arial", Font.BOLD, 22));
        dobLabel.setBounds(60, 295, 200, 35);
        SpinnerDateModel dobModel = new SpinnerDateModel();
        JSpinner dobSpinner = new JSpinner(dobModel);
        dobSpinner.setBounds(250, 290, 350, 45);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dobSpinner, "dd/MM/yyyy");
        dobSpinner.setEditor(editor);
        dobSpinner.setFont(new Font("Arial", Font.PLAIN, 20));
        formContainer.add(dobLabel);
        formContainer.add(dobSpinner);

        // Course - loaded from DB
        JLabel courseLabel = new JLabel("Course");
        courseLabel.setFont(new Font("Arial", Font.BOLD, 22));
        courseLabel.setBounds(60, 360, 200, 35);
        courseBox = new JComboBox<>();
        courseBox.setBounds(250, 355, 350, 45);
        courseBox.setFont(new Font("Arial", Font.PLAIN, 20));
        formContainer.add(courseLabel);
        formContainer.add(courseBox);

        // Gender
        JLabel genderLabel = new JLabel("Gender");
        genderLabel.setFont(new Font("Arial", Font.BOLD, 22));
        genderLabel.setBounds(60, 425, 200, 35);
        JRadioButton maleBtn = new JRadioButton("Male");
        JRadioButton femaleBtn = new JRadioButton("Female");
        JRadioButton otherBtn = new JRadioButton("Other");
        maleBtn.setBounds(250, 420, 100, 40);
        femaleBtn.setBounds(360, 420, 120, 40);
        otherBtn.setBounds(490, 420, 100, 40);
        maleBtn.setFont(new Font("Arial", Font.PLAIN, 20));
        femaleBtn.setFont(new Font("Arial", Font.PLAIN, 20));
        otherBtn.setFont(new Font("Arial", Font.PLAIN, 20));
        maleBtn.setBackground(new Color(240, 242, 245));
        femaleBtn.setBackground(new Color(240, 242, 245));
        otherBtn.setBackground(new Color(240, 242, 245));
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleBtn);
        genderGroup.add(femaleBtn);
        genderGroup.add(otherBtn);
        formContainer.add(maleBtn);
        formContainer.add(femaleBtn);
        formContainer.add(otherBtn);
        formContainer.add(genderLabel);

        // Address
        JLabel addressLabel = new JLabel("Address");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 22));
        addressLabel.setBounds(60, 490, 200, 35);
        JTextField addressField = new JTextField();
        addressField.setBounds(250, 485, 350, 60);
        addressField.setFont(new Font("Arial", Font.PLAIN, 20));
        formContainer.add(addressLabel);
        formContainer.add(addressField);

        // Admission Date
        JLabel admissiondateLabel = new JLabel("Admission Date");
        admissiondateLabel.setFont(new Font("Arial", Font.BOLD, 22));
        admissiondateLabel.setBounds(60, 570, 200, 35);
        SpinnerDateModel admissiondateModel = new SpinnerDateModel();
        JSpinner admissiondateSpinner = new JSpinner(admissiondateModel);
        admissiondateSpinner.setBounds(250, 565, 350, 45);
        JSpinner.DateEditor editor2 = new JSpinner.DateEditor(admissiondateSpinner, "dd/MM/yyyy");
        admissiondateSpinner.setEditor(editor2);
        admissiondateSpinner.setFont(new Font("Arial", Font.PLAIN, 20));
        formContainer.add(admissiondateLabel);
        formContainer.add(admissiondateSpinner);

        // Status
        JLabel statusLabel = new JLabel("Status");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 22));
        statusLabel.setBounds(60, 635, 200, 35);
        String status[] = {"Select Status", "Active", "DeActive"};
        JComboBox<String> statusBox = new JComboBox<>(status);
        statusBox.setBounds(250, 630, 350, 45);
        statusBox.setFont(new Font("Arial", Font.PLAIN, 20));
        formContainer.add(statusLabel);
        formContainer.add(statusBox);

        // Add Button
        JButton addBtn = new JButton("Add Student");
        addBtn.setBounds(290, 700, 220, 55);
        addBtn.setBackground(new Color(15, 45, 90));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 20));
        addBtn.setFocusPainted(false);
        addBtn.setBorder(BorderFactory.createEmptyBorder());
        formContainer.add(addBtn);

        formContainer.setPreferredSize(new Dimension(700, 800));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                centerFormContainer();
            }
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadCourses();
            }
        });
        centerFormContainer();

        loadCourses();

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            Object courseSel = courseBox.getSelectedItem();
            String course = courseSel == null ? "" : courseSel.toString();
            String gender = maleBtn.isSelected() ? "Male" : femaleBtn.isSelected() ? "Female" : otherBtn.isSelected() ? "Other" : "";
            String statusVal = (String) statusBox.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() ||
                course.isEmpty() || course.equals("Select Course") || gender.isEmpty() ||
                statusVal.equals("Select Status")) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            java.util.Date dob = (java.util.Date) dobSpinner.getValue();
            java.util.Date admissionDate = (java.util.Date) admissiondateSpinner.getValue();
            java.sql.Date sqlDob = new java.sql.Date(dob.getTime());
            java.sql.Date sqlAdmission = new java.sql.Date(admissionDate.getTime());

            String sql = "INSERT INTO students (full_name, email, phone, dob, course, gender, address, admission_date, status) VALUES (?,?,?,?,?,?,?,?,?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, phone);
                pstmt.setDate(4, sqlDob);
                pstmt.setString(5, course);
                pstmt.setString(6, gender);
                pstmt.setString(7, address);
                pstmt.setDate(8, sqlAdmission);
                pstmt.setString(9, statusVal);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student added successfully!");
                nameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                addressField.setText("");
                if (courseBox.getItemCount() > 0) courseBox.setSelectedIndex(0);
                genderGroup.clearSelection();
                statusBox.setSelectedIndex(0);
                dobSpinner.setValue(new java.util.Date());
                admissiondateSpinner.setValue(new java.util.Date());
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadCourses() {
        Object prev = courseBox.getSelectedItem();
        courseBox.removeAllItems();
        courseBox.addItem("Select Course");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT course_name FROM courses WHERE status='Active' ORDER BY course_name")) {
            while (rs.next()) {
                courseBox.addItem(rs.getString("course_name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (prev != null) {
            for (int i = 0; i < courseBox.getItemCount(); i++) {
                if (courseBox.getItemAt(i).equals(prev)) {
                    courseBox.setSelectedIndex(i);
                    return;
                }
            }
        }
        if (courseBox.getItemCount() > 0) courseBox.setSelectedIndex(0);
    }

    private void centerFormContainer() {
        if (formContainer != null) {
            int parentWidth = getWidth();
            int parentHeight = getHeight();
            int w = formContainer.getPreferredSize().width;
            int h = formContainer.getPreferredSize().height;
            int x = (parentWidth - w) / 2;
            int y = (parentHeight - h) / 2;
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            formContainer.setBounds(x, y, w, h);
        }
    }
}