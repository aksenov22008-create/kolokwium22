import com.sun.jdi.connect.Connector;                 // (Не використовується, можна видалити)
import database.DatabaseConnection;                   // Підключення до БД
import database.ListenerAccount;                      // Клас ListenerAccount
import database.NotEnoughCreditsException;            // Власний Exception
import music.Playlist;                               // Playlist
import music.Song;                                   // Song
import org.junit.jupiter.params.provider.CsvFileSource; // Дані для тестів із CSV
import static org.junit.jupiter.api.Assertions.*;     // Усі assert
import org.junit.jupiter.api.AfterEach;              // Виконується після кожного тесту
import org.junit.jupiter.api.BeforeEach;             // Виконується перед кожним тестом
import org.junit.jupiter.api.Test;                   // Позначає звичайний тест
import org.junit.jupiter.params.ParameterizedTest;   // Параметризований тест
import org.junit.jupiter.params.provider.Arguments;  // Аргументи для MethodSource
import org.junit.jupiter.params.provider.MethodSource; // Дані беруться з методу

import java.sql.SQLException;                        // SQLException
import java.util.List;                               // List
import java.util.Optional;                           // Optional
import java.util.stream.Stream;                      // Stream для MethodSource

import static org.junit.jupiter.api.Assertions.assertEquals; // assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue;   // assertTrue
import static org.junit.jupiter.params.provider.Arguments.arguments; // arguments()

public class SongTest {

    @Test                                                // Звичайний тест
    public void testRead() throws SQLException {

        DatabaseConnection.connect("songs.db");          // Підключається до songs.db

        Optional<Song> song = Song.Persistence.read(4);  // Читає пісню з id=4

        Song expected =
                new Song("Bob Dylan","Like a Rolling Stone",373); // Очікувана пісня

        assertTrue(song.isPresent());                    // Перевіряє що Optional не пустий

        assertEquals(expected,song.get());               // Перевіряє чи пісні однакові

    }

    @Test
    public void testReadFail() throws SQLException {

        DatabaseConnection.connect("songs.db");          // Підключення

        Optional<Song> song =
                Song.Persistence.read(68);               // Такої пісні немає

        assertTrue(song.isEmpty());                      // Optional повинен бути пустим

    }

    private static Stream<Arguments> args(){             // Дані для ParameterizedTest

        return Stream.of(

                arguments(40,"The Beatles","Help!",138),           // 1 тест

                arguments(41,"The Beach Boys","California Girls",165), // 2 тест

                arguments(42,"The Temptations","Ain't Too Proud to Beg",154) // 3 тест

        );

    }

    @ParameterizedTest                                   // Один тест виконується кілька разів
    @MethodSource("args")                                // Дані беруться з args()

    public void testReadMany(int id,
                             String artist,
                             String title,
                             int length)
            throws SQLException {

        DatabaseConnection.connect("songs.db","");       // Підключення

        Optional<Song> song =
                Song.Persistence.read(id);               // Читає пісню

        Song expectedSong =
                new Song(artist,title,length);           // Створює очікувану пісню

        assertTrue(song.isPresent());                    // Перевіряє Optional

        assertEquals(expectedSong,song.get());           // Порівнює пісні

    }

    // ---------------- CSV ----------------

    @ParameterizedTest                                   // Параметризований тест

    @CsvFileSource(
            files = "songs.csv",                         // Дані беруться із CSV
            numLinesToSkip = 1                           // Пропустити заголовок
    )

    public void testReadCsv(int id,
                            String artist,
                            String title,
                            int length)
            throws SQLException {

        DatabaseConnection.connect("songs.db","");       // Підключення

        Optional<Song> song =
                Song.Persistence.read(id);               // Читає пісню

        Song expectedSong =
                new Song(artist,title,length);           // Очікуваний результат

        assertTrue(song.isPresent());                    // Optional не пустий

        assertEquals(expectedSong,song.get());           // Перевірка

    }

    // ---------------- PLAYLIST ----------------

    @Test

    public void testCreatePlaylist() throws Exception {

        ListenerAccount.Persistence.register("user","pass"); // Реєструє користувача

        ListenerAccount account =
                ListenerAccount.Persistence.authenticate("user","pass"); // Авторизація

        account.addCredits(3);                          // Дає 3 кредити

        Playlist expected = new Playlist();             // Очікуваний Playlist

        expected.add(Song.Persistence.read(1).get());   // Перша пісня

        expected.add(Song.Persistence.read(2).get());   // Друга пісня

        Playlist actual =
                account.createPlaylist(List.of(1,2));   // Створює Playlist

        assertEquals(expected,actual);                  // Порівнює Playlist

    }

    @BeforeEach                                         // Перед кожним тестом

    void connect(){

        DatabaseConnection.connect("songs.db");         // Відкрити БД

    }

    @AfterEach                                          // Після кожного тесту

    void disconnect(){

        DatabaseConnection.disconnect();                // Закрити БД

    }

}