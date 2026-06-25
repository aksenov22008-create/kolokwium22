package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Server {
    private ServerSocket serverSocket; // серверний сокет, який очікує нові підключення
    private HashMap<String, ClientHandler> handlers = new HashMap(); // список усіх підключених користувачів (login -> ClientHandler)

    // =====================================================
    // Конструктор Server
    // Створює серверний сокет на вказаному порту.
    // =====================================================
    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port); // відкриваємо сервер на заданому порту

    }

    // =====================================================
    // Метод listen()
    // Постійно очікує нових клієнтів, створює для кожного
    // ClientHandler і запускає його в окремому потоці.
    // =====================================================
    public void listen() throws IOException {
        System.out.println("SERVER STARTED"); // повідомлення про запуск сервера
        while(true){ // нескінченне очікування нових клієнтів
            Socket socket = serverSocket.accept(); // очікуємо підключення нового клієнта
            ClientHandler handler = new ClientHandler(socket,this); // створюємо об'єкт для роботи з клієнтом
            System.out.println("Client connect"); // виводимо повідомлення про підключення
            Thread thread = new Thread(handler); // створюємо окремий потік для клієнта
            thread.start(); // запускаємо потік

            handlers.put(handler.getLogin(),handler); // додаємо клієнта до списку онлайн
        }
    }

    // =====================================================
    // Метод online()
    // Формує список усіх користувачів онлайн та надсилає
    // його клієнту, який викликав команду /online.
    // =====================================================
    public void online(ClientHandler sender){
        String userList = handlers.values().stream().map(ClientHandler::getLogin).collect(Collectors.joining("\n")); // отримуємо логіни всіх користувачів
        sender.send("Users online: \n"+userList); // надсилаємо список користувачу
    }

    // =====================================================
    // Метод broadcast()
    // Надсилає повідомлення всім підключеним користувачам.
    // =====================================================
    public  void broadcast(String message, ClientHandler sender){
        handlers.values().stream().forEach(handler -> handler.send(message)); // відправляємо повідомлення кожному клієнту
    }

    // =====================================================
    // Метод whisper()
    // Надсилає приватне повідомлення лише одному
    // користувачу. Якщо його немає онлайн — повідомляє
    // про це відправника.
    // =====================================================
    public void whisper(String recipient, String message, ClientHandler sender) {
        ClientHandler receiver = handlers.get(recipient); // шукаємо отримувача за логіном

        if (receiver == null) { // якщо користувача не знайдено
            sender.send("User "+ recipient+ " is not online."); // повідомляємо відправника
            return; // завершуємо метод
        }

        receiver.send("[private] "+ sender.getLogin()+ ": "+ message); // надсилаємо приватне повідомлення отримувачу
    }
}