package music;

import java.util.ArrayList;                  // ArrayList - список, від якого наслідується Playlist

public class Playlist extends ArrayList<Song> { // Playlist - це ArrayList<Song>, тому можна add(), get(), size()...

    public Song atSecond(int second){        // Повертає пісню, яка грає на заданій секунді

        if(second < 0){                      // Якщо секунда від'ємна
            throw new IndexOutOfBoundsException("Ujemny czas"); // Кидає помилку "від'ємний час"
        }

        int offset = 0;                      // Початок першої пісні (0 секунда)

        for(Song song : this){               // Проходить по всіх піснях Playlist

            int end = offset + song.getLength(); // Обчислює кінець поточної пісні

            if(offset <= second && second < end){ // Якщо second знаходиться між offset і end
                return song;                 // Повертає цю пісню
            }else{
                offset = end;                // Інакше початок наступної пісні = кінець поточної
            }

        }

        throw new IndexOutOfBoundsException("Zbyt duzy czas"); // Якщо second більша за довжину Playlist

    }

}