package music;

import database.DatabaseConnection;             // Клас для підключення до бази даних

import java.sql.PreparedStatement;             // Підготовлений SQL-запит
import java.sql.ResultSet;                     // Результат SELECT
import java.sql.SQLException;                  // SQL-помилки
import java.util.Objects;                      // Для equals() і hashCode()
import java.util.Optional;                     // Optional - може містити значення або бути пустим

public class Song {

    private String artist;                     // Виконавець
    private String title;                      // Назва пісні
    private int length;                        // Довжина пісні (секунди)

    public Song(String artist, String title, int length) { // Конструктор
        this.artist = artist;                  // Записує виконавця
        this.title = title;                    // Записує назву
        this.length = length;                  // Записує довжину
    }

    public String getArist() {                 // Getter для artist (у назві методу є помилка автора - має бути getArtist)
        return artist;                         // Повертає виконавця
    }

    public String getTitle() {                 // Getter для title
        return title;                          // Повертає назву
    }

    public int getLength() {                   // Getter для length
        return length;                         // Повертає довжину
    }

    @Override
    public boolean equals(Object o) {          // Порівнює два об'єкти Song

        if (this == o) return true;            // Якщо це той самий об'єкт

        if (o == null || getClass() != o.getClass()) // Якщо null або інший клас
            return false;

        Song song = (Song) o;                  // Перетворює Object у Song

        return length == song.length           // Порівнює length
                && Objects.equals(artist, song.artist) // Порівнює artist
                && Objects.equals(title, song.title);  // Порівнює title
    }

    @Override
    public int hashCode() {                    // Генерує hashCode()
        return Objects.hash(artist, title, length); // Hash із трьох полів
    }

    public static class Persistence {          // Робота з базою даних

        public static Optional<Song> read(int index) throws SQLException { // Зчитати пісню по id

            String sql =
                    "SELECT title, artist, length FROM song WHERE id = ?"; // SQL SELECT

            PreparedStatement statement =
                    DatabaseConnection.getConnection().prepareStatement(sql); // Створює PreparedStatement

            statement.setInt(1, index);        // Підставляє id пісні

            if (!statement.execute())          // Виконує SELECT
                throw new RuntimeException("SELECT failed"); // Якщо SELECT не виконався

            ResultSet result =
                    statement.getResultSet();  // Отримує ResultSet

            if (result.next()) {               // Якщо пісню знайдено

                Song song = new Song(          // Створює новий об'єкт Song

                        result.getString("artist"), // Читає artist

                        result.getString("title"),  // Читає title

                        result.getInt("length")     // Читає length

                );

                return Optional.of(song);      // Повертає Optional із Song

            } else {

                return Optional.empty();       // Якщо пісні немає

            }
        }

    }

}