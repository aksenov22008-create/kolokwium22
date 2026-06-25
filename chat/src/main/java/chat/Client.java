package chat;

import java.io.*;
import java.net.Socket;

public class Client {

    // =====================================================
    // Метод main()
    // Підключається до сервера, відправляє логін,
    // запускає потік для отримання повідомлень та
    // дозволяє користувачу надсилати свої повідомлення.
    // =====================================================
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 3001); // підключаємося до сервера

        BufferedReader keyboard =
                new BufferedReader(new InputStreamReader(System.in)); // потік для читання повідомлень з клавіатури

        BufferedReader serverReader =
                new BufferedReader(new InputStreamReader(socket.getInputStream())); // потік для отримання повідомлень від сервера

        PrintWriter writer =
                new PrintWriter(socket.getOutputStream(), true); // потік для надсилання повідомлень серверу

        System.out.print("Login: "); // просимо користувача ввести логін

        String login = keyboard.readLine(); // зчитуємо логін

        writer.println(login); // відправляємо логін серверу

        Thread receiver = new Thread(() -> { // окремий потік для отримання повідомлень від сервера
            try {
                String message; // змінна для повідомлень

                while ((message = serverReader.readLine()) != null) { // поки сервер надсилає повідомлення
                    System.out.println(message); // виводимо повідомлення на екран
                }

            } catch (IOException e) {
                System.out.println("Disconnected from server."); // повідомлення про втрату з'єднання
            }
        });

        receiver.setDaemon(true); // робимо потік фоновим

        receiver.start(); // запускаємо потік отримання повідомлень

        String message; // змінна для повідомлень користувача

        while ((message = keyboard.readLine()) != null) { // поки користувач вводить повідомлення
            writer.println(message); // відправляємо повідомлення серверу
        }

        socket.close(); // закриваємо з'єднання із сервером

    }

}