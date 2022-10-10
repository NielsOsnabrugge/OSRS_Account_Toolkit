package utilities;
import java.sql.*;

import data.Account;

public class DatabaseAPI {
    private static Connection conn;
    private static PreparedStatement psm;

    private static Connection GetDatabaseConnection(){
        String url = "jdbc:mysql://84.87.6.144/osrs?autoReconnect=true&useSSL=false";
        String user = "root";
        String password = "adminadmin";
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(url,user,password);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private static void CloseConnections(){
        try{
            if(conn != null){
                conn.close();
            }
            if(psm != null){
                psm.close();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void InsertAccount(Account account){
        conn = GetDatabaseConnection();
        try {
            psm = conn.prepareStatement("insert into  account (name, email, date_of_birth, password) " +
                    "values (?, ?, ?, ?)");
            psm.setString(1, account.getName());
            psm.setString(2, account.getEmail());
            psm.setDate(3, Date.valueOf(account.getDateOfBirth()));
            psm.setString(4, account.getPassword());
            psm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CloseConnections();
    }
}
