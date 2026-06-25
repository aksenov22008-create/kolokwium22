package database;

import music.Playlist;                   // Клас Playlist
import music.Song;                       // Клас Song

import javax.naming.AuthenticationException; // Помилка авторизації
import java.sql.PreparedStatement;       // Підготовлений SQL-запит
import java.sql.ResultSet;               // Результат SELECT
import java.sql.SQLException;            // SQL-помилки
import java.util.List;                   // Інтерфейс List

public class ListenerAccount extends Account {   // Наслідує Account

    public ListenerAccount(int id, String name) {
        super(id, name);                        // Викликає конструктор Account
    }

    // ------------------- BUY SONG -------------------

    public void buySong(int songId) throws SQLException, NotEnoughCreditsException {

        if (Persistence.hasSong(this.id, songId)) {      // Перевіряє чи вже куплена ця пісня
            return;                                      // Якщо так - нічого не робить
        }

        if (getCredits() <= 0) {                         // Якщо кредитів немає
            throw new NotEnoughCreditsException();       // Кидає власний Exception
        }

        Persistence.addSong(this.id, songId);           // Записує покупку в таблицю owned_songs

        addCredits(-1);                                // Забирає 1 кредит
    }

    // ------------------- CREDITS -------------------

    public int getCredits() throws SQLException {
        return Persistence.getCredits(this.id);         // Повертає кількість кредитів користувача
    }

    public void addCredits(int amount) throws SQLException {
        Persistence.addCredits(this.id, amount);        // Додає або віднімає кредити
    }

    // ------------------- PLAYLIST -------------------

    public Playlist createPlaylist(List<Integer> songIds)
            throws SQLException, NotEnoughCreditsException {

        Playlist playlist = new Playlist();             // Створює новий Playlist

        for (var id : songIds) {                        // Проходить по всіх id пісень

            if (!Persistence.hasSong(this.id, id)) {    // Якщо пісня ще не куплена
                buySong(id);                            // Купує її
            }

            var optionalSong = Song.Persistence.read(id); // Читає пісню з БД

            if (optionalSong.isPresent())               // Якщо Optional не пустий
                playlist.add(optionalSong.get());       // Додає пісню в Playlist
            else
                throw new SQLException();               // Якщо пісню не знайдено
        }

        return playlist;                               // Повертає Playlist
    }

    // =====================================================
    //                PERSISTENCE
    // =====================================================

    public static class Persistence {

        public static void init() throws SQLException {

            Account.Persistence.init();                 // Створює таблицю account

            {
                String sql =
                        "CREATE TABLE IF NOT EXISTS listener_account(" +
                                "id_account INTEGER NOT NULL PRIMARY KEY," +
                                "credits INTEGER NOT NULL)";    // Таблиця кредитів

                PreparedStatement statement =
                        DatabaseConnection.getConnection().prepareStatement(sql); // CREATE TABLE

                statement.executeUpdate();              // Виконує CREATE TABLE
            }

            {
                String sql =
                        "CREATE TABLE IF NOT EXISTS owned_songs(" +
                                "id_account INTEGER NOT NULL," +
                                "id_song INTEGER NOT NULL," +
                                "PRIMARY KEY(id_account,id_song))"; // Таблиця куплених пісень

                PreparedStatement statement =
                        DatabaseConnection.getConnection().prepareStatement(sql);

                statement.executeUpdate();
            }
        }

        // ---------------- REGISTER ----------------

        public static int register(String username, String password)
                throws SQLException {

            try {

                int id =
                        Account.Persistence.register(username, password); // Створює Account

                String sql =
                        "INSERT INTO listener_account(id_account,credits) VALUES(?,0)"; // Створює ListenerAccount

                PreparedStatement statement =
                        DatabaseConnection.getConnection().prepareStatement(sql);

                statement.setInt(1, id);                // Підставляє id користувача

                statement.executeUpdate();              // INSERT

                return id;                             // Повертає id

            } catch (SQLException | RuntimeException e) {

                throw new RuntimeException(e);

            }
        }

        // ---------------- GET CREDITS ----------------

        private static int getCredits(int id)
                throws SQLException {

            String sql =
                    "SELECT credits FROM listener_account WHERE id_account=?"; // Отримує кредити

            PreparedStatement statement =
                    DatabaseConnection.getConnection().prepareStatement(sql);

            statement.setInt(1, id);                   // Підставляє id

            ResultSet resultSet =
                    statement.executeQuery();          // Виконує SELECT

            if (resultSet.next()) {                    // Якщо запис знайдено

                return resultSet.getInt("credits");    // Повертає credits

            }

            throw new SQLException();                  // Якщо запису немає
        }

        // ---------------- ADD CREDITS ----------------

        private static void addCredits(int id, int amount)
                throws SQLException {

            int currentCredits =
                    getCredits(id);                    // Поточні кредити

            String sql =
                    "UPDATE listener_account SET credits=? WHERE id_account=?"; // UPDATE

            PreparedStatement statement =
                    DatabaseConnection.getConnection().prepareStatement(sql);

            statement.setInt(1, currentCredits + amount); // Нова кількість кредитів

            statement.setInt(2, id);                     // id користувача

            statement.executeUpdate();                   // UPDATE
        }

        // ---------------- ADD SONG ----------------

        public static void addSong(int accountId, int songId)
                throws SQLException {

            String sql =
                    "INSERT INTO owned_songs VALUES(?,?)"; // Додає куплену пісню

            PreparedStatement statement =
                    DatabaseConnection.getConnection().prepareStatement(sql);

            statement.setInt(1, accountId);             // id користувача

            statement.setInt(2, songId);                // id пісні

            statement.executeUpdate();                  // INSERT
        }

        // ---------------- HAS SONG ----------------

        public static boolean hasSong(int accountId, int songId)
                throws SQLException {

            String sql =
                    "SELECT * FROM owned_songs WHERE id_account=? AND id_song=?"; // Перевіряє чи існує запис

            PreparedStatement statement =
                    DatabaseConnection.getConnection().prepareStatement(sql);

            statement.setInt(1, accountId);             // id користувача

            statement.setInt(2, songId);                // id пісні

            return statement.executeQuery().next();     // true якщо запис знайдено
        }

        // ---------------- LOGIN ----------------

        public static ListenerAccount authenticate(String username,
                                                   String password)
                throws AuthenticationException {

            Account account =
                    Account.Persistence.authenticate(username, password); // Перевіряє логін і пароль

            return new ListenerAccount(
                    account.getId(),                   // id
                    account.getUsername()              // username
            );
        }

    }
}