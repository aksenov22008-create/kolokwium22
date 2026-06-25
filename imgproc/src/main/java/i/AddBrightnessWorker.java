package i;

import java.awt.image.BufferedImage;

import static java.lang.Math.clamp;

public class AddBrightnessWorker implements Runnable { // Клас, який виконується в окремому потоці
    private BufferedImage image; // Зображення для обробки
    private int amount; // На скільки збільшити яскравість
    private int begin; // Перший рядок, який обробляє потік
    private int end; // Рядок, до якого обробляє потік (не включно)

    public AddBrightnessWorker(BufferedImage image, int amount, int begin, int end) {
        this.image = image; // Запам'ятовуємо зображення
        this.amount = amount; // Запам'ятовуємо величину зміни яскравості
        this.begin = begin; // Початковий рядок
        this.end = end; // Кінцевий рядок
    }

    @Override
    public void run() { // Код, який виконається після start()
        for (int y = this.begin; y < this.end; y++) { // Обробляємо тільки свою частину зображення
            for (int x = 0; x < image.getWidth(); x++) { // Проходимо по всіх стовпцях

                int color = image.getRGB(x, y); // Отримуємо колір пікселя

                int blue = color & 0x0000FF; // Виділяємо синій канал
                int green = (color & 0x00FF00) >> 8; // Виділяємо зелений канал
                int red = (color & 0xFF0000) >> 16; // Виділяємо червоний канал

                blue = clamp(blue + amount, 0, 255); // Збільшуємо синій і обмежуємо 0..255
                green = clamp(green + amount, 0, 255); // Збільшуємо зелений
                red = clamp(red + amount, 0, 255); // Збільшуємо червоний

                int newColor = blue | (green << 8) | (red << 16); // Складаємо новий RGB

                image.setRGB(x, y, newColor); // Записуємо новий колір
            }
        }
    }
}