package database;

import java.util.HashMap;                    // HashMap для зберігання підключень
import java.util.Map;                        // Інтерфейс Map
import java.sql.Connection;                 // Представляє підключення до БД
import java.sql.DriverManager;              // Створює підключення до БД
import java.sql.SQLException;               // Виняток при роботі з SQL

public class DatabaseConnection {

    static private final Map<String, Connection> connections = new HashMap<>(); // Зберігає всі відкриті підключення (ім'я -> Connection)

    static public Connection getConnection() {                                   // Отримати стандартне підключення
        return getConnection("");                                                // Викликає метод нижче з порожнім ім'ям
    }

    static public Connection getConnection(String name) {                        // Отримати підключення за його ім'ям
        return connections.get(name);                                            // Повертає Connection із HashMap
    }

    static public void connect(String filePath) {                                // Підключитися до БД без імені
        connect(filePath, "");                                                   // Викликає перевантажений метод
    }

    static public void connect(String filePath, String connectionName){          // Підключення до SQLite
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + filePath); // Відкриває файл SQLite
            connections.put(connectionName, connection);                         // Зберігає підключення у HashMap
        } catch (SQLException e) {                                               // Якщо виникла помилка SQL
            throw new RuntimeException(e);                                       // Перетворює її у RuntimeException
        }
    }

    static public void disconnect() {                                            // Закрити стандартне підключення
        disconnect("");                                                          // Викликає метод нижче
    }

    static public void disconnect(String connectionName){                        // Закрити підключення за ім'ям
        try {
            Connection connection = connections.get(connectionName);             // Отримує Connection із HashMap
            connection.close();                                                  // Закриває підключення до БД
            connections.remove(connectionName);                                  // Видаляє його з HashMap
        } catch (SQLException e) {                                               // Якщо сталася SQL-помилка
            throw new RuntimeException(e);                                       // Перетворює її у RuntimeException
        }
    }
}