import database.DatabaseConnection;                  // Підключення до бази даних
import database.ListenerAccount;                     // Клас ListenerAccount
import database.NotEnoughCreditsException;           // Власний Exception
import music.Playlist;                              // Playlist
import music.Song;                                  // Song
import org.junit.jupiter.api.AfterEach;             // Виконується після кожного тесту
import org.junit.jupiter.api.BeforeEach;            // Виконується перед кожним тестом
import org.junit.jupiter.api.Test;                  // Позначає тест

import java.sql.SQLException;                       // SQLException
import java.util.List;                              // List

import static org.junit.jupiter.api.Assertions.*;   // Усі assert

public class ListenerAccountTest {

    // ================= REGISTER =================

    @Test                                           // Тест реєстрації
    public void testRegister() throws Exception {

        int id =
                ListenerAccount.Persistence.register("User","Password"); // Реєструє користувача

        assertTrue(id > 0);                          // id повинен бути більше 0

    }

    // ================= LOGIN =================

    @Test
    public void testLogin() throws Exception {

        int id =
                ListenerAccount.Persistence.register("loginUser","password123"); // Реєстрація

        ListenerAccount account =
                ListenerAccount.Persistence.authenticate("loginUser","password123"); // Авторизація

        assertEquals(
                "loginUser",
                account.getUsername()                // Username повинен збігатися
        );

    }

    // ================= CREDITS =================

    @Test
    public void testEmptyCredits() throws Exception {

        ListenerAccount.Persistence.register("user1","pass1"); // Реєстрація

        ListenerAccount account =
                ListenerAccount.Persistence.authenticate("user1","pass1"); // Авторизація

        assertEquals(
                0,
                account.getCredits()                 // Новий акаунт має 0 кредитів
        );

    }

    @Test
    public void testAddCredits() throws Exception {

        ListenerAccount.Persistence.register("user2","pass2");

        ListenerAccount account =
                ListenerAccount.Persistence.authenticate("user2","pass2");

        account.addCredits(4);                       // Додає 4 кредити

        assertEquals(
                4,
                account.getCredits()                 // Повинно стати 4
        );

    }

    // ================= BUY SONG =================

    @Test
    public void testBuyOwnedSong() throws Exception {

        ListenerAccount.Persistence.init();          // Створює таблиці

        ListenerAccount.Persistence.register("user12","pass12");

        ListenerAccount account =
                ListenerAccount.Persistence.authenticate("user12","pass12");

        account.addCredits(5);                       // Дає 5 кредитів

        ListenerAccount.Persistence.addSong(
                account.getId(),
                1
        );                                           // Пісня вже куплена

        account.buySong(1);                          // Повторна покупка

        assertEquals(
                5,
                account.getCredits()                 // Кредити не повинні змінитися
        );

    }

    @Test
    public void testBuySong() throws Exception {

        ListenerAccount.Persistence.init();

        ListenerAccount.Persistence.register("user123","pass123");

        ListenerAccount account =
                ListenerAccount.Persistence.authenticate("user123","pass123");

        account.addCredits(1);                       // Дає 1 кредит

        account.buySong(1);                          // Купує пісню

        assertEquals(
                0,
                account.getCredits()                 // Після покупки залишиться 0
        );

    }

    @Test
    public void testBuySongWithout() throws Exception {

        ListenerAccount.Persistence.init();

        ListenerAccount.Persistence.register("user1234","pass1234");

        ListenerAccount account =
                ListenerAccount.Persistence.authenticate("user1234","pass1234");

        assertThrows(
                NotEnoughCreditsException.class,     // Очікується цей Exception
                () -> account.buySong(1)             // Купує без кредитів
        );

    }

    // ================= DATABASE =================

    @BeforeEach                                     // Перед кожним тестом
    void connect() throws SQLException {

        DatabaseConnection.connect("temp.db");       // Створює тимчасову БД

        ListenerAccount.Persistence.init();          // Створює всі таблиці

    }

    @AfterEach                                      // Після кожного тесту
    void disconnect() {

        DatabaseConnection.disconnect();             // Закриває БД

        new java.io.File("temp.db").delete();        // Видаляє тимчасову базу

    }

}