package chat;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame{
    private JPanel rootPanel;
    private JTextField inputField;
    private JButton sendButton;
    private JList<String> userList;
    private JTextArea chatArea;
    private final Client client; // клієнт чату
    private final DefaultListModel<String> model = new DefaultListModel<>(); // модель списку користувачів

    // =====================================================
    // Конструктор MainWindow
    // Створює головне вікно,
    // налаштовує список користувачів
    // та обробники кнопки і Enter.
    // =====================================================
    public MainWindow(String login,Client client){
        this.client = client; // зберігаємо клієнта

        setTitle(login); // встановлюємо назву вікна
        setContentPane(rootPanel); // встановлюємо головну панель
        setMinimumSize(new Dimension(800,600)); // мінімальний розмір
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // закриття програми

        userList.setModel(model); // встановлюємо модель списку

        sendButton.addActionListener(e -> send()); // натискання кнопки Send
        inputField.addActionListener(e -> send()); // натискання Enter

        pack(); // підганяємо розмір вікна
    }

    // =====================================================
    // Метод send()
    // Відправляє повідомлення серверу
    // та очищає поле вводу.
    // =====================================================
    private void send(){
        String message = inputField.getText(); // отримуємо текст

        if(message.isBlank()){ // якщо повідомлення порожнє
            return;
        }

        client.send(message); // надсилаємо повідомлення

        inputField.setText(""); // очищаємо поле
    }

    // =====================================================
    // Метод appendMessage()
    // Додає повідомлення у вікно чату.
    // =====================================================
    public void appendMessage(String message){
        chatArea.append(message + "\n"); // додаємо повідомлення
        chatArea.setCaretPosition(chatArea.getDocument().getLength()); // прокручуємо вниз
    }

    // =====================================================
    // Метод getModel()
    // Повертає модель списку користувачів.
    // =====================================================
    public DefaultListModel<String> getModel(){
        return model; // повертаємо модель
    }
}