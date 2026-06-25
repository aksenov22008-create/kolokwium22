import music.Playlist;
import music.Song;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlaylistTest {
    @Test
    public void testEmptyPlaylist(){
        Playlist playlist = new Playlist();
        assertTrue(playlist.isEmpty());
    }
    @Test
    public void testSingleElement(){
        Playlist playlist = new Playlist();
        playlist.add(new Song("Jan Kowalski","Test",183));
        assertEquals(1,playlist.size());
    }
    @Test
    public void testSameElements(){
        Playlist playlist = new Playlist();
        Song song = new Song("Jan Kowalski","Test",183);
        playlist.add(song);
        assertTrue(playlist.contains(song));
    }
    @Test
    public void testEqualElement(){
        Playlist playlist =new Playlist();
        Song song = new Song("Jan Kowalski","Test",183);
        Song samesong = new Song("Jan Kowalski","Test",183);
        playlist.add(song);
        assertTrue(playlist.contains(samesong));
        assertEquals(playlist.get(0),samesong);
    }
    @Test
    public void testAtSecond(){
        Playlist playlist = new Playlist();
        Song song1 = new Song("John Doe","Test1",100);
        Song song2 = new Song("Mary Sue ","Test2",150);
        Song song3 = new Song("Marty Sue","Test3",200);
        playlist.add(song1);//0-100
        playlist.add(song2);//100-250
        playlist.add(song3);//250-450
        assertEquals(song1 ,playlist.atSecond(0));
        assertEquals(song1 ,playlist.atSecond(50));
        assertEquals(song2 ,playlist.atSecond(200));
        assertEquals(song3 ,playlist.atSecond(300));
    }
    private IndexOutOfBoundsException doesThrowExeptionCommon(int seconds){
        Playlist playlist = new Playlist();
        Song song1 = new Song("John Doe","Test1",100);
        Song song2 = new Song("Mary Sue ","Test2",150);
        Song song3 = new Song("Marty Sue","Test3",200);
        playlist.add(song1);//0-100
        playlist.add(song2);//100-250
        playlist.add(song3);//250-450
        return assertThrows(IndexOutOfBoundsException.class, ()-> playlist.atSecond(seconds));
    }
    @Test
    public void TestDoesThrowException(){
        assertEquals("Zbyt duzy czas",doesThrowExeptionCommon(1500).getMessage());
    }
    @Test
    public void TestDoesThrowNegaiveException(){
        assertEquals("Ujemny czas",doesThrowExeptionCommon(-1000).getMessage());
    }

}
