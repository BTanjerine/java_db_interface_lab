
package jdbclab04;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnect{
    Connection connection;

    DBConnect() {
        connect_to_database();
    }

    public void connect_to_database() {
        try{
            System.out.println("Connecting to Database. . .");
            connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/kickt/Desktop/lab04/lab04_PTS.db");
            System.out.println("Database connected");

        }catch(Exception e) {
            System.out.println("Get Error u kno");
            System.out.println(e);
        }
    }
}
