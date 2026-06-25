package i;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static java.lang.Math.clamp;

public class ImageProcessor {

    BufferedImage image; // Поле, в якому зберігається зображення

    public void load(String path) throws IOException {
        File file = new File(path); // Створюємо об'єкт файлу
        this.image = ImageIO.read(file); // Зчитуємо зображення у BufferedImage
    }

    public void save(String path) throws IOException {
        File file = new File(path); // Створюємо файл для збереження
        ImageIO.write(this.image, "png", file); // Записуємо зображення у форматі PNG
    }

    public void addBrightness(int amount) {

        for (int y = 0; y < image.getHeight(); y++) { // Проходимо всі рядки

            for (int x = 0; x < image.getWidth(); x++) { // Проходимо всі стовпці

                int color = image.getRGB(x, y); // Отримуємо колір пікселя

                int blue = color & 0x0000FF; // Виділяємо синій канал
                int green = (color & 0x00FF00) >> 8; // Виділяємо зелений канал
                int red = (color & 0xFF0000) >> 16; // Виділяємо червоний канал

                blue = clamp(blue + amount, 0, 255); // Збільшуємо синій
                green = clamp(green + amount, 0, 255); // Збільшуємо зелений
                red = clamp(red + amount, 0, 255); // Збільшуємо червоний

                int newColor = blue | (green << 8) | (red << 16); // Складаємо новий RGB

                image.setRGB(x, y, newColor); // Записуємо новий колір
            }
        }
    }

    public void addBrightnessThread(int amount) throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors(); // Кількість доступних ядер процесора
        Thread[] threads = new Thread[cores]; // Масив потоків
        for (int i = 0; i < cores; i++) {
            int startRow = (image.getHeight() / cores) * i; // Початковий рядок потоку
            int endRow = (i == cores - 1)
                    ? image.getHeight()
                    : (image.getHeight() / cores) * (i + 1); // Останній рядок потоку
            AddBrightnessWorker worker =
                    new AddBrightnessWorker(image, amount, startRow, endRow); // Створюємо Runnable
            threads[i] = new Thread(worker); // Створюємо потік
            threads[i].start(); // Запускаємо потік
        }
        for (int j = 0; j < cores; j++) {
            threads[j].join(); // Чекаємо завершення всіх потоків
        }
    }

    public void addBrightnessPool(int amount) throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors(); // Кількість ядер
        ExecutorService executor =
                Executors.newFixedThreadPool(cores); // Створюємо пул потоків
        for (int y = 0; y < image.getHeight(); y++) {
            int row = y; // Робимо копію, щоб можна було використати в lambda
            executor.submit(() -> { // Додаємо задачу в пул
                for (int x = 0; x < image.getWidth(); x++) {

                    int color = image.getRGB(x, row); // Отримуємо колір

                    int blue = color & 0xFF; // Синій
                    int green = (color >> 8) & 0xFF; // Зелений
                    int red = (color >> 16) & 0xFF; // Червоний

                    blue = Math.clamp(blue + amount, 0, 255); // Змінюємо синій
                    green = Math.clamp(green + amount, 0, 255); // Змінюємо зелений
                    red = Math.clamp(red + amount, 0, 255); // Змінюємо червоний

                    int newColor = blue | (green << 8) | (red << 16); // Новий RGB

                    image.setRGB(x, row, newColor); // Записуємо піксель
                }
            });
        }
        executor.shutdown(); // Забороняємо додавати нові задачі
        executor.awaitTermination(1, TimeUnit.MINUTES); // Чекаємо завершення всіх задач
    }

    public int[] histogramBlue() throws InterruptedException {
        AtomicIntegerArray histogram = new AtomicIntegerArray(256); // Потокобезпечний масив гістограми
        int cores = Runtime.getRuntime().availableProcessors(); // Кількість ядер
        ExecutorService executor =
                Executors.newFixedThreadPool(cores); // Пул потоків
        for (int y = 0; y < image.getHeight(); y++) {
            int row = y; // Копія номера рядка
            executor.submit(() -> { // Один task = один рядок
                for (int x = 0; x < image.getWidth(); x++) {
                    int color = image.getRGB(x, row); // Беремо піксель
                    int blue = color & 0xFF; // Отримуємо синій канал
                    histogram.incrementAndGet(blue); // Збільшуємо лічильник цього значення
                }
            });
        }
        executor.shutdown(); // Завершуємо прийом задач
        executor.awaitTermination(1, TimeUnit.MINUTES); // Чекаємо виконання
        int[] result = new int[256]; // Звичайний масив результату
        for (int i = 0; i < 256; i++) {
            result[i] = histogram.get(i); // Копіюємо значення

        }
        return result; // Повертаємо гістограму
    }

    public BufferedImage histogramImage(int[] histogram) {
        int width = 256; // По одному стовпчику на кожне значення кольору
        int height = 300; // Висота графіка
        BufferedImage hist =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // Створюємо нове зображення
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                hist.setRGB(x, y, Color.WHITE.getRGB()); // Заповнюємо фон білим
            }
        }
        int max = 0; // Максимальне значення гістограми
        for (int value : histogram) {
            if (value > max)
                max = value; // Знаходимо найбільший стовпчик
        }
        for (int x = 0; x < 256; x++) {
            int barHeight = histogram[x] * height / max; // Масштабуємо висоту
            for (int y = height - 1; y >= height - barHeight; y--) {
                hist.setRGB(x, y, Color.BLACK.getRGB()); // Малюємо чорний стовпчик
            }
        }
        return hist; // Повертаємо готову гістограму
    }

}