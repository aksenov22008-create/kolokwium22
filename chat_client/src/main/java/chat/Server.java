package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Server {
    private final ServerSocket serverSocket; // серверний сокет
    private final HashMap<String, ClientHandler> handlers = new HashMap<>(); // список підключених користувачів

    // =====================================================
    // Конструктор Server
    // Створює серверний сокет на вказаному порту.
    // =====================================================
    public Server(int port) throws IOException{
        this.serverSocket = new ServerSocket(port); // відкриваємо сервер
    }

    // =====================================================
    // Метод listen()
    // Постійно очікує нових клієнтів,
    // створює для кожного ClientHandler
    // та запускає його в окремому потоці.
    // =====================================================
    public void listen() throws IOException{
        System.out.println("SERVER STARTED"); // повідомлення про запуск сервера

        while(true){ // нескінченне очікування клієнтів

            Socket socket = serverSocket.accept(); // очікуємо нового клієнта

            ClientHandler handler = new ClientHandler(socket,this); // створюємо обробник клієнта

            handlers.put(handler.getLogin(),handler); // додаємо користувача до списку онлайн

            online(handler); // надсилаємо новому користувачу список онлайн

            Thread thread = new Thread(handler); // створюємо потік

            thread.start(); // запускаємо потік


        }
    }

    // =====================================================
    // Метод broadcast()
    // Надсилає повідомлення всім користувачам.
    // =====================================================
    public void broadcast(String message,ClientHandler sender){
        handlers.values().forEach(handler ->
                handler.send("BROADCAST:" + message)); // розсилаємо повідомлення
    }

    // =====================================================
    // Метод whisper()
    // Надсилає приватне повідомлення
    // лише одному користувачу.
    // =====================================================
    public void whisper(String recipient,String message,ClientHandler sender){

        ClientHandler receiver = handlers.get(recipient); // шукаємо отримувача

        if(receiver == null){ // якщо користувача немає

            sender.send("BROADCAST:User " + recipient + " is not online."); // повідомляємо відправника

            return;
        }

        receiver.send("WHISPER:" + sender.getLogin() + ": " + message); // надсилаємо повідомлення
    }

    // =====================================================
    // Метод online()
    // Надсилає клієнту список користувачів онлайн.
    // =====================================================
    public void online(ClientHandler sender){

        String users = handlers.values()
                .stream()
                .map(ClientHandler::getLogin)
                .collect(Collectors.joining(";")); // формуємо список користувачів

        sender.send("ONLINE:" + users); // надсилаємо список
    }

    // =====================================================
    // Метод loginBroadcast()
    // Повідомляє всіх про вхід користувача.
    // =====================================================
    public void loginBroadcast(String login){

        handlers.values().forEach(handler ->
                handler.send("LOGIN:" + login)); // розсилаємо повідомлення
    }

    // =====================================================
    // Метод logoutBroadcast()
    // Повідомляє всіх про вихід користувача
    // та видаляє його зі списку онлайн.
    // =====================================================
    public void logoutBroadcast(String login){

        handlers.remove(login); // видаляємо користувача

        handlers.values().forEach(handler ->
                handler.send("LOGOUT:" + login)); // повідомляємо інших користувачів
    }
}