import database.DatabaseConnection;
import database.ListenerAccount;
import music.Playlist;
import music.Song;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class SongTest {
    @Test
    public void testRead() throws SQLException {
        DatabaseConnection.connect("songs.db");

        Optional<Song> song =Song.Persistence.read(4);
        Song expected = new Song("Bob Dylan","Like a Rolling Stone",373);
        assertTrue(song.isPresent());
        assertEquals(expected,song.get());

    }
    @Test
    public void testReadFail() throws SQLException {
        DatabaseConnection.connect("songs.db");
        Optional<Song> song = Song.Persistence.read(68);
        assertTrue(song.isEmpty());
    }
    private static Stream<Arguments> args(){
        return Stream.of(arguments(40,"The Beatles","Help!",138),
                arguments(41,"The Beach Boys","California Girls",165),
                arguments(42,"The Temptations","Ain't Too Proud to Beg",154));
    }
    @ParameterizedTest
    @MethodSource("args")
    public void testReadMany(int id ,String artist,String title ,int length) throws SQLException {
        DatabaseConnection.connect("songs.db","");
        Optional<Song> song =Song.Persistence.read(id);
        Song expectedSong = new Song(artist,title,length);

        assertTrue(song.isPresent());
        assertEquals(expectedSong,song.get());
    }
    //2e
    @ParameterizedTest
    @CsvFileSource(files = "songs.csv", numLinesToSkip = 1)
    public void testReadCsv(int id ,String artist,String title ,int length) throws SQLException {
        DatabaseConnection.connect("songs.db","");
        Optional<Song> song =Song.Persistence.read(id);
        Song expectedSong = new Song(artist,title,length);

        assertTrue(song.isPresent());
        assertEquals(expectedSong,song.get());
    }
    //3e
    @Test
    public void testCreatePlaylist() throws Exception {
        ListenerAccount.Persistence.register("user", "pass");
        ListenerAccount account = ListenerAccount.Persistence.authenticate("user", "pass");
        account.addCredits(3);
        Playlist expected = new Playlist();
        expected.add(Song.Persistence.read(1).get());
        expected.add(Song.Persistence.read(2).get());
        Playlist actual = account.createPlaylist(List.of(1, 2));
        assertEquals(expected, actual);
    }
    @BeforeEach
    void connect() {
        DatabaseConnection.connect("songs.db");
    }

    @AfterEach
    void disconnect() {
        DatabaseConnection.disconnect();
    }


}
