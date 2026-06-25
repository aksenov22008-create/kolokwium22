package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private String login; // логін користувача
    private final Socket socket; // сокет, через який спілкується клієнт із сервером
    private final Server server; // посилання на сервер
    private final BufferedReader reader; // потік для отримання повідомлень від клієнта
    private final PrintWriter writer; // потік для надсилання повідомлень клієнту

    // =====================================================
    // Конструктор ClientHandler
    // Створює всі потоки для роботи з клієнтом та
    // зчитує його логін.
    // =====================================================
    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket; // зберігаємо сокет клієнта
        this.server = server; // зберігаємо посилання на сервер
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // створюємо потік читання
        this.writer = new PrintWriter(socket.getOutputStream(),true); // створюємо потік запису
        this.login = reader.readLine(); // отримуємо логін клієнта
    }

    // =====================================================
    // Метод getLogin()
    // Повертає логін користувача.
    // =====================================================
    public String getLogin(){
        return login; // повертаємо логін
    }

    // =====================================================
    // Метод send()
    // Надсилає повідомлення клієнту.
    // =====================================================
    public void send(String message){
        writer.println(message); // відправляємо повідомлення
    }

    // =====================================================
    // Метод run()
    // Головний цикл роботи клієнта. Отримує повідомлення,
    // обробляє команди та розсилає звичайні повідомлення.
    // =====================================================
    @Override
    public void run() {
        String message; // змінна для збереження повідомлень
        try {
            System.out.println("Login: "+this.login); // виводимо логін нового користувача
            server.broadcast(login + " joined the chat.", this); // повідомляємо всіх про вхід користувача

            while ((message = reader.readLine()) != null) { // поки клієнт надсилає повідомлення
                if (message.startsWith("/")) { // перевіряємо, чи це команда
                    String[] parts = message.split(" ", 3); // розбиваємо команду максимум на 3 частини
                    String command = parts[0]; // отримуємо саму команду

                    switch (command) {
                        case "/online" -> {
                            server.online(this); // показуємо список користувачів онлайн
                        }

                        case "/w" -> {
                            if (parts.length < 3) { // якщо команда введена неправильно
                                send("Usage: /w recipient message"); // показуємо правильний формат
                                continue; // переходимо до наступної ітерації циклу
                            }

                            String recipient = parts[1]; // логін отримувача
                            String privateMessage = parts[2]; // текст приватного повідомлення

                            server.whisper(recipient, privateMessage, this); // надсилаємо приватне повідомлення
                        }
                    }

                    continue; // не обробляємо команду як звичайне повідомлення
                }

                server.broadcast(login+" :"+message,this); // надсилаємо повідомлення всім користувачам
            }

            server.broadcast(login + " left the chat.", this); // повідомляємо всіх про вихід користувача

            socket.close(); // закриваємо сокет

        }catch (IOException e){
            throw new RuntimeException();
        }
    }

}