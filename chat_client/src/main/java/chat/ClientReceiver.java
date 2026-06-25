package chat;

import javax.swing.DefaultListModel;

public class ClientReceiver {
    private final MainWindow window; // головне вікно
    private final DefaultListModel<String> model; // модель списку користувачів

    // =====================================================
    // Конструктор ClientReceiver
    // Зберігає посилання на головне вікно
    // та модель списку користувачів.
    // =====================================================
    public ClientReceiver(MainWindow window, DefaultListModel<String> model){
        this.window = window; // зберігаємо вікно
        this.model = model; // зберігаємо модель списку
    }

    // =====================================================
    // Метод broadcast()
    // Відображає звичайне повідомлення у чаті.
    // =====================================================
    public void broadcast(String message){
        window.appendMessage(message); // додаємо повідомлення у чат
    }

    // =====================================================
    // Метод whisper()
    // Відображає приватне повідомлення.
    // =====================================================
    public void whisper(String message){
        window.appendMessage("[PRIVATE] " + message); // додаємо приватне повідомлення
    }

    // =====================================================
    // Метод loginBroadcast()
    // Додає нового користувача до списку
    // та повідомляє про його вхід.
    // =====================================================
    public void loginBroadcast(String login){
        if(!model.contains(login)){ // якщо користувача ще немає
            model.addElement(login); // додаємо його до списку
        }

        window.appendMessage(login + " joined the chat."); // повідомляємо про вхід
    }

    // =====================================================
    // Метод logoutBroadcast()
    // Видаляє користувача зі списку
    // та повідомляє про його вихід.
    // =====================================================
    public void logoutBroadcast(String login){
        model.removeElement(login); // видаляємо користувача

        window.appendMessage(login + " left the chat."); // повідомляємо про вихід
    }

    // =====================================================
    // Метод online()
    // Оновлює список користувачів онлайн.
    // =====================================================
    public void online(String users){
        model.clear(); // очищаємо список

        if(users.isEmpty()){
            return; // якщо список порожній
        }

        String[] list = users.split(";"); // розбиваємо рядок на логіни

        for(String user : list){ // проходимо по всіх логінах
            model.addElement(user); // додаємо користувача
        }
    }
}