package i;

import javafx.scene.canvas.GraphicsContext; // Надає інструменти для малювання на полотні
import javafx.scene.paint.Color; // Надає готові кольори JavaFX

public class Paddle extends GraphicsItem { // Представляє платформу, якою керує гравець
    public Paddle() { // Створює платформу в нижній частині екрана
        width = 0.20; // Встановлює ширину на двадцять відсотків полотна
        height = 0.025; // Встановлює невелику висоту платформи
        x = (1.0 - width) / 2.0; // Розміщує платформу по центру
        y = 0.93; // Розміщує платформу біля нижнього краю
    }

    public void updatePosition(double mouseX) { // Пересуває центр платформи до курсора
        double normalizedMouseX = mouseX / canvasWidth; // Перетворює позицію миші на діапазон від 0 до 1
        x = normalizedMouseX - width / 2.0; // Розміщує центр платформи під курсором
        x = Math.max(0.0, x); // Не дозволяє платформі вийти за лівий край
        x = Math.min(1.0 - width, x); // Не дозволяє платформі вийти за правий край
    }

    public double getCenterX() { // Повертає координату центра платформи
        return x + width / 2.0; // Обчислює центр за позицією та шириною
    }

    @Override
    public void draw(GraphicsContext graphicsContext) { // Малює платформу на полотні
        double pixelX = x * canvasWidth; // Перетворює координату x на пікселі
        double pixelY = y * canvasHeight; // Перетворює координату y на пікселі
        double pixelWidth = width * canvasWidth; // Перетворює ширину на пікселі
        double pixelHeight = height * canvasHeight; // Перетворює висоту на пікселі
        graphicsContext.setFill(Color.CRIMSON); // Вибирає червоний колір платформи
        graphicsContext.fillRect(pixelX, pixelY, pixelWidth, pixelHeight); // Малює прямокутну платформу
    }
}
