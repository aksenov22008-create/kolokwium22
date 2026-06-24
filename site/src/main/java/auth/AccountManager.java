package auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountManager {
    public static int register(String password, String name) throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        String query = "INSERT INTO account (username, password) VALUES(?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, name);

        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        preparedStatement.setString(2, hashedPassword);
        preparedStatement.execute();

        ResultSet rs = preparedStatement.getGeneratedKeys();
        if (rs.next()) return rs.getInt(1);
        throw new RuntimeException("Nie udalo");
    }
    public static boolean authenticate(String password, String name) throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        String query = "SELECT password FROM account WHERE username =?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1,name);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            String dbPassword = rs.getString("password");
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(),dbPassword);
            return result.verified;
        }
        return false;
    }
    public static Account getAccount(int id,String name) throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        String tmp = "SELECT * FROM account WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(tmp);
        ps.setInt(1,id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            Account account =
                    new Account(
                            rs.getInt("id"),
                            rs.getString("username")
                    );

            rs.close();
            ps.close();

            return account;

        }
        rs.close();
        ps.close();

        return null;
    }
}
