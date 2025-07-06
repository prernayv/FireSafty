package firesaftyproject;
import java.sql.*;

public class Conn {
    Connection c;
    Statement s;
    
    Conn() throws SQLException {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create the connection
            c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/firesafty",
                "root",
                "Pre$1311"
            );
            
            // Create statement
            if (c != null) {
                s = c.createStatement();
                System.out.println("Database connection established successfully");
            } else {
                throw new SQLException("Failed to establish database connection");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Please add mysql-connector-java to your project", e);
        } catch (SQLException e) {
            throw new SQLException("Database connection error: " + e.getMessage() + 
                "\nPlease check:\n" +
                "1. MySQL Server is running on port 3306\n" +
                "2. Database 'firesafty' exists\n" +
                "3. Username 'root' and password are correct", e);
        }
    }
}

