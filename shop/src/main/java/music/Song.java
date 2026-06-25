package music;

import database.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class Song {
    private String artist;
    private String title;
    private int length;

    public Song(String artist, String title, int length) {
        this.artist = artist;
        this.title = title;
        this.length = length;
    }

    public String getArist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public int getLength() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return length == song.length && Objects.equals(artist, song.artist) && Objects.equals(title, song.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artist, title, length);
    }

    public static class Persistence{
        public static Optional<Song> read(int index) throws SQLException {
            String sql = "SELECT title, artist, length FROM song WHERE id = ?";
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            statement.setInt(1,index);

            if (!statement.execute()){
                throw new RuntimeException("SELECT failed");
            }
            ResultSet result = statement.getResultSet();
            if(result.next()){
                Song song = new Song(result.getString("artist"),
                        result.getString("title"),
                        result.getInt("length")
                );
                return Optional.of(song);
            }else{
                return Optional.empty();
            }
        }
    }
}
