package examSystem;

import java.sql.*;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/examdb",
                    "root",
                    "Raila@2003"
            );
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }
}