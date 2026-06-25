package chat;

import java.io.IOException;

public class Main {

    // =====================================================
    // Метод main()
    // Створює сервер та запускає
    // очікування підключення клієнтів.
    // =====================================================
    public static void main(String[] args) throws IOException{
        Server server = new Server(3001); // створюємо сервер на порту 3001
        server.listen(); // запускаємо сервер
    }
}