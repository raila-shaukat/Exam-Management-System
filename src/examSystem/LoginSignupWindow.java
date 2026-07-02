package examSystem;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginSignupWindow {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Exam Management System");
        frame.setSize(450, 350);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(60, 50, 100, 25);
        frame.add(lblUser);

        JTextField txtUsername = new JTextField();
        txtUsername.setBounds(160, 50, 200, 25);
        frame.add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(60, 90, 100, 25);
        frame.add(lblPass);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setBounds(160, 90, 200, 25);
        frame.add(txtPassword);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setBounds(60, 130, 100, 25);
        frame.add(lblRole);

        String[] roles = {"Select Role", "Teacher", "Student"};
        JComboBox<String> cmbRole = new JComboBox<>(roles);
        cmbRole.setBounds(160, 130, 200, 25);
        frame.add(cmbRole);

        JLabel lblStatus = new JLabel("");
        lblStatus.setBounds(60, 165, 350, 25);
        lblStatus.setForeground(java.awt.Color.RED);
        frame.add(lblStatus);

        JButton btnSignup = new JButton("Sign Up");
        btnSignup.setBounds(100, 210, 100, 35);
        frame.add(btnSignup);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(220, 210, 100, 35);
        frame.add(btnLogin);

        // --- SIGN UP ---
        btnSignup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String user = txtUsername.getText().trim();
                String pass = new String(txtPassword.getPassword()).trim();
                String role = cmbRole.getSelectedItem().toString();

                if (user.isEmpty() || pass.isEmpty() || role.equals("Select Role")) {
                    lblStatus.setText("Please fill all fields!");
                    return;
                }

                try {
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO users(username,password,role) VALUES(?,?,?)");
                    ps.setString(1, user);
                    ps.setString(2, pass);
                    ps.setString(3, role);
                    ps.executeUpdate();
                    con.close();
                    JOptionPane.showMessageDialog(frame, "User registered successfully!");
                } catch (Exception ex) {
                    lblStatus.setText("Error: " + ex.getMessage());
                }
            }
        });

        // --- LOGIN ---
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String user = txtUsername.getText().trim();
                String pass = new String(txtPassword.getPassword()).trim();
                String role = cmbRole.getSelectedItem().toString();

                if (user.isEmpty() || pass.isEmpty() || role.equals("Select Role")) {
                    lblStatus.setText("Please enter Username, Password and Role!");
                    return;
                }

                try {
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM users WHERE username=? AND password=? AND role=?");
                    ps.setString(1, user);
                    ps.setString(2, pass);
                    ps.setString(3, role);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(frame, role + " login successful!");
                        if (role.equals("Teacher")) {
                            TeacherDashboard.show(user);
                        } else {
                            StudentDashboard.show(user);
                        }
                        frame.dispose();
                    } else {
                        lblStatus.setText("Invalid credentials! Signup first.");
                        JOptionPane.showMessageDialog(frame,
                            "New to Exam Management System?\nSignup first!");
                    }
                    con.close();
                } catch (Exception ex) {
                    lblStatus.setText("Error: " + ex.getMessage());
                }
            }
        });

        frame.setVisible(true);
    }
}