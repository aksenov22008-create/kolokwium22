package i;

import javafx.scene.canvas.GraphicsContext; // Надає інструменти для малювання на полотні

public abstract class GraphicsItem { // Є основою для всіх графічних елементів гри
    protected static double canvasWidth; // Зберігає ширину полотна в пікселях
    protected static double canvasHeight; // Зберігає висоту полотна в пікселях
    protected double x; // Зберігає координату лівого краю в діапазоні від 0 до 1
    protected double y; // Зберігає координату верхнього краю в діапазоні від 0 до 1
    protected double width; // Зберігає ширину елемента в діапазоні від 0 до 1
    protected double height; // Зберігає висоту елемента в діапазоні від 0 до 1

    public static void setCanvasSize(double width, double height) { // Запам'ятовує справжній розмір полотна
        canvasWidth = width; // Зберігає ширину полотна
        canvasHeight = height; // Зберігає висоту полотна
    }

    public double getX() { // Повертає горизонтальну позицію елемента
        return x; // Передає значення координати x
    }

    public void setX(double x) { // Змінює горизонтальну позицію елемента
        this.x = x; // Записує нову координату x
    }

    public double getY() { // Повертає вертикальну позицію елемента
        return y; // Передає значення координати y
    }

    public void setY(double y) { // Змінює вертикальну позицію елемента
        this.y = y; // Записує нову координату y
    }

    public double getWidth() { // Повертає ширину елемента
        return width; // Передає значення ширини
    }

    public void setWidth(double width) { // Змінює ширину елемента
        this.width = width; // Записує нову ширину
    }

    public double getHeight() { // Повертає висоту елемента
        return height; // Передає значення висоти
    }

    public void setHeight(double height) { // Змінює висоту елемента
        this.height = height; // Записує нову висоту
    }

    public abstract void draw(GraphicsContext graphicsContext); // Вимагає реалізувати малювання кожного елемента
}
