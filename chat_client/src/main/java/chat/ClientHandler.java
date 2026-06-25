package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private String login; // логін користувача
    private final Socket socket; // сокет клієнта
    private final Server server; // посилання на сервер
    private final BufferedReader reader; // потік для отримання повідомлень
    private final PrintWriter writer; // потік для відправлення повідомлень

    // =====================================================
    // Конструктор ClientHandler
    // Створює всі потоки для роботи з клієнтом та
    // отримує його логін.
    // =====================================================
    public ClientHandler(Socket socket, Server server) throws IOException{
        this.socket = socket; // зберігаємо сокет
        this.server = server; // зберігаємо сервер
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // створюємо потік читання
        this.writer = new PrintWriter(socket.getOutputStream(), true); // створюємо потік запису
        this.login = reader.readLine(); // отримуємо логін користувача
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
        writer.println(message); // надсилаємо повідомлення
    }

    // =====================================================
    // Метод run()
    // Основний цикл роботи клієнта.
    // Отримує повідомлення, обробляє команди
    // та розсилає повідомлення іншим користувачам.
    // =====================================================
    @Override
    public void run(){
        try{
            System.out.println("Login: " + login); // виводимо логін у консоль

            server.loginBroadcast(login); // повідомляємо всіх про вхід користувача

            String message; // змінна для повідомлень

            while((message = reader.readLine()) != null){ // поки клієнт надсилає повідомлення

                if(message.equals("/online")){ // якщо користувач просить список онлайн
                    server.online(this); // надсилаємо список користувачів
                    continue; // переходимо до наступної ітерації
                }

                if(message.startsWith("/w ")){ // якщо це приватне повідомлення
                    String[] parts = message.split(" ",3); // розділяємо команду

                    if(parts.length == 3){ // якщо команда введена правильно
                        server.whisper(parts[1], parts[2], this); // надсилаємо приватне повідомлення
                    }else{
                        send("Usage: /w recipient message"); // повідомляємо правильний формат
                    }

                    continue; // не відправляємо повідомлення всім
                }

                server.broadcast(login + ": " + message, this); // надсилаємо повідомлення всім користувачам
            }

            server.logoutBroadcast(login); // повідомляємо всіх про вихід користувача
            socket.close(); // закриваємо сокет

        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}