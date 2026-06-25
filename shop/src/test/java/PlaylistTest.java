import music.Playlist;                             // Клас Playlist
import music.Song;                                 // Клас Song
import org.junit.jupiter.api.Test;                 // Анотація тесту

import static org.junit.jupiter.api.Assertions.*;  // Усі assert

public class PlaylistTest {

    @Test                                           // Простий тест
    public void testEmptyPlaylist(){

        Playlist playlist = new Playlist();         // Створює порожній Playlist

        assertTrue(playlist.isEmpty());             // Перевіряє що Playlist порожній

    }

    @Test
    public void testSingleElement(){

        Playlist playlist = new Playlist();         // Новий Playlist

        playlist.add(new Song("Jan Kowalski","Test",183)); // Додає одну пісню

        assertEquals(1,playlist.size());            // Повинен містити 1 елемент

    }

    @Test
    public void testSameElements(){

        Playlist playlist = new Playlist();         // Новий Playlist

        Song song =
                new Song("Jan Kowalski","Test",183); // Створює Song

        playlist.add(song);                         // Додає її

        assertTrue(playlist.contains(song));        // Перевіряє що саме цей об'єкт є у Playlist

    }

    @Test
    public void testEqualElement(){

        Playlist playlist = new Playlist();         // Новий Playlist

        Song song =
                new Song("Jan Kowalski","Test",183);

        Song sameSong =
                new Song("Jan Kowalski","Test",183); // Інший об'єкт але з такими ж даними

        playlist.add(song);                         // Додає першу пісню

        assertTrue(playlist.contains(sameSong));    // contains() використовує equals()

        assertEquals(playlist.get(0),sameSong);     // Порівнює через equals()

    }

    @Test
    public void testAtSecond(){

        Playlist playlist = new Playlist();         // Новий Playlist

        Song song1 =
                new Song("John Doe","Test1",100);

        Song song2 =
                new Song("Mary Sue","Test2",150);

        Song song3 =
                new Song("Marty Sue","Test3",200);

        playlist.add(song1);                        // 0-99

        playlist.add(song2);                        // 100-249

        playlist.add(song3);                        // 250-449

        assertEquals(song1,playlist.atSecond(0));   // Початок першої

        assertEquals(song1,playlist.atSecond(50));  // Середина першої

        assertEquals(song2,playlist.atSecond(200)); // Друга

        assertEquals(song3,playlist.atSecond(300)); // Третя

    }

    private IndexOutOfBoundsException doesThrowExceptionCommon(int seconds){

        Playlist playlist = new Playlist();         // Створює Playlist

        Song song1 =
                new Song("John Doe","Test1",100);

        Song song2 =
                new Song("Mary Sue","Test2",150);

        Song song3 =
                new Song("Marty Sue","Test3",200);

        playlist.add(song1);

        playlist.add(song2);

        playlist.add(song3);

        return assertThrows(                        // Очікує IndexOutOfBoundsException
                IndexOutOfBoundsException.class,
                () -> playlist.atSecond(seconds)    // Викликає метод
        );

    }

    @Test
    public void TestDoesThrowException(){

        assertEquals(
                "Zbyt duzy czas",                   // Очікуване повідомлення
                doesThrowExceptionCommon(1500).getMessage() // Текст Exception
        );

    }

    @Test
    public void TestDoesThrowNegativeException(){

        assertEquals(
                "Ujemny czas",
                doesThrowExceptionCommon(-1000).getMessage()
        );

    }

}