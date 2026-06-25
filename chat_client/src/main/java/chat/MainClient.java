package chat;

import javax.swing.*;

public class MainClient {

    // =====================================================
    // Метод main()
    // Запитує логін користувача,
    // підключається до сервера,
    // створює головне вікно,
    // запускає потік отримання повідомлень.
    // =====================================================
    public static void main(String[] args) throws Exception{

        String login = JOptionPane.showInputDialog("Login:"); // запитуємо логін

        if(login == null || login.isBlank()){ // якщо користувач нічого не ввів
            return; // завершуємо програму
        }

        Client client = new Client("localhost",3001); // створюємо клієнта

        client.send(login); // надсилаємо логін серверу

        client.send("/online");

        MainWindow window = new MainWindow(login,client); // створюємо головне вікно

        ClientReceiver receiver = new ClientReceiver(window,window.getModel()); // створюємо обробник повідомлень

        client.setOnMessageReceived(message -> { // обробляємо повідомлення від сервера

            if(message.startsWith("BROADCAST:")){ // якщо це звичайне повідомлення
                receiver.broadcast(message.substring(10));
            }
            else if(message.startsWith("WHISPER:")){ // якщо це приватне повідомлення
                receiver.whisper(message.substring(8));
            }
            else if(message.startsWith("LOGIN:")){ // якщо користувач увійшов
                receiver.loginBroadcast(message.substring(6));
            }
            else if(message.startsWith("LOGOUT:")){ // якщо користувач вийшов
                receiver.logoutBroadcast(message.substring(7));
            }
            else if(message.startsWith("ONLINE:")){ // якщо прийшов список користувачів
                receiver.online(message.substring(7));
            }

        });

        new Thread(client).start(); // запускаємо потік отримання повідомлень

        window.setVisible(true); // показуємо головне вікно
    }
}