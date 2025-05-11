package Backend;

import java.sql.*;

public class UserDAO {

   public static boolean validateUser(String username, String password) {
    System.out.println("Validating user: " + username + ", " + password); // debug log
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();
        boolean result = rs.next();
        System.out.println("Login success: " + result); // debug log
        return result;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}



}
