package Backend;

import java.sql.Connection;

public class TestDBConnection {

    public static void main(String[] args) {
        // Try getting the connection
        Connection conn = DBConnection.getConnection();
        
        if (conn != null) {
            System.out.println("Connection successful!");
        } else {
            System.out.println("Connection failed.");
        }
    }
}
