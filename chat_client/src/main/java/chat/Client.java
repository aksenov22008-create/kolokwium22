package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class Client implements Runnable{
    private final Socket socket; // сокет для підключення до сервера
    private final BufferedReader reader; // потік для отримання повідомлень
    private final PrintWriter writer; // потік для відправки повідомлень
    private Consumer<String> onMessageReceived; // обробник отриманих повідомлень

    // Встановлює функцію, яка буде викликатися при отриманні нового повідомлення
    public void setOnMessageReceived(Consumer<String> callback){
        this.onMessageReceived = callback;
    }

    // Створює підключення до сервера
    public Client(String address,int port) throws IOException{
        this.socket = new Socket(address,port); // підключаємося до сервера
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // потік читання
        this.writer = new PrintWriter(socket.getOutputStream(),true); // потік запису
    }

    // Надсилає повідомлення серверу
    public void send(String message){
        writer.println(message);
    }

    // Постійно отримує повідомлення від сервера
    @Override
    public void run(){
        try{
            String message; // змінна для повідомлень

            while((message = reader.readLine()) != null){ // поки сервер надсилає повідомлення
                if(onMessageReceived != null){ // якщо встановлений обробник
                    onMessageReceived.accept(message); // передаємо повідомлення у GUI
                }
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}