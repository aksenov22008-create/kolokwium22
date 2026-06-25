package database;

import at.favre.lib.crypto.bcrypt.BCrypt;     // Бібліотека для хешування та перевірки паролів
import javax.naming.AuthenticationException; // Помилка авторизації
import java.sql.PreparedStatement;           // Підготовлений SQL-запит
import java.sql.ResultSet;                   // Результат виконання SELECT
import java.sql.SQLException;                // SQL-помилки

public class Account {

    protected final int id;                  // id користувача (не можна змінити після створення)
    protected final String username;         // username користувача

    public Account(int id, String username) { // Конструктор
        this.id = id;                        // Записує id
        this.username = username;            // Записує username
    }

    public int getId() {                     // Getter для id
        return id;                           // Повертає id
    }

    public String getUsername() {            // Getter для username
        return username;                     // Повертає username
    }

    @Override
    public String toString() {               // Перетворює об'єкт у текст
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }

    public static class Persistence {        // Клас для роботи з базою даних

        public static void init() {          // Створює таблицю account
            try {

                String createSQLTable =
                        "CREATE TABLE IF NOT EXISTS account(" +
                                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                                "username TEXT NOT NULL," +
                                "password TEXT NOT NULL)";     // SQL для створення таблиці

                PreparedStatement statement =
                        DatabaseConnection.getConnection().prepareStatement(createSQLTable); // Створює PreparedStatement

                statement.executeUpdate();             // Виконує CREATE TABLE

            } catch (SQLException e) {                // Якщо SQL-помилка
                throw new RuntimeException(e);        // Перетворює її у RuntimeException
            }
        }

        public static int register(String username, String password) { // Реєстрація користувача

            String hashedPassword =
                    BCrypt.withDefaults().hashToString(12, password.toCharArray()); // Хешує пароль; 12 - складність; toCharArray() -> char[]

            try {

                String insertSQL =
                        "INSERT INTO account(username, password) VALUES (?, ?)"; // SQL INSERT

                PreparedStatement statement =
                        DatabaseConnection.getConnection().prepareStatement(insertSQL); // Створює PreparedStatement

                statement.setString(1, username);          // Підставляє username замість першого ?
                statement.setString(2, hashedPassword);    // Підставляє hash пароля замість другого ?

                statement.executeUpdate();                 // Виконує INSERT

                ResultSet resultSet =
                        statement.getGeneratedKeys();      // Отримує автоматично створений id

                if (resultSet.next())                     // Якщо id існує
                    return resultSet.getInt(1);           // Повернути id

                else
                    throw new SQLException();             // Якщо id не отримали

            } catch (SQLException e) {
                throw new RuntimeException(e);            // Перетворює SQLException
            }
        }

        public static Account authenticate(String username, String password)
                throws AuthenticationException {          // Авторизація користувача

            try {

                String sql =
                        "SELECT id, username, password FROM account WHERE username = ?"; // SQL SELECT

                PreparedStatement statement =
                        DatabaseConnection.getConnection().prepareStatement(sql); // Готує SQL

                statement.setString(1, username);         // Підставляє username

                if (!statement.execute())                 // Виконує SELECT
                    throw new RuntimeException("SELECT failed"); // Якщо SELECT не виконався

                ResultSet result =
                        statement.getResultSet();         // Отримує ResultSet

                if (!result.next()) {                    // Якщо користувача не знайдено
                    throw new AuthenticationException("No such user");
                }

                String hashedPassword =
                        result.getString(3);             // Отримує hash пароля з БД

                boolean okay =
                        BCrypt.verifyer()
                                .verify(password.toCharArray(), hashedPassword.toCharArray())
                                .verified;               // Порівнює введений пароль із hash

                if (!okay) {                            // Якщо пароль неправильний
                    throw new AuthenticationException("Wrong password");
                }

                return new Account(
                        result.getInt(1),               // id користувача
                        result.getString(2)             // username користувача
                );

            } catch (SQLException e) {
                throw new RuntimeException(e);          // SQL -> RuntimeException
            }
        }
    }
}