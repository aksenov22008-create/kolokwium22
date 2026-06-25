package i;

import javafx.geometry.Point2D; // Зберігає двовимірну точку або напрямок
import javafx.scene.canvas.GraphicsContext; // Надає інструменти для малювання на полотні
import javafx.scene.paint.Color; // Надає готові кольори JavaFX

public class Ball extends GraphicsItem { // Представляє м'яч, який рухається по екрану
    private Point2D moveVector; // Зберігає нормалізований напрямок руху
    private double velocity; // Зберігає швидкість руху м'яча

    public Ball() { // Створює м'яч із початковим напрямком угору і вправо
        width = 0.025; // Встановлює діаметр відносно ширини полотна
        height = width * canvasWidth / canvasHeight; // Зберігає круглу форму при прямокутному полотні
        velocity = 0.55; // Встановлює початкову швидкість м'яча
        moveVector = new Point2D(1, -1).normalize(); // Створює напрямок під кутом 45 градусів
    }

    public void setPosition(Point2D position) { // Встановлює центр м'яча у вказаній точці
        x = position.getX() - width / 2.0; // Обчислює координату лівого краю
        y = position.getY() - height / 2.0; // Обчислює координату верхнього краю
    }

    public void updatePosition(double elapsedSeconds) { // Пересуває м'яч відповідно до часу між кадрами
        x += moveVector.getX() * velocity * elapsedSeconds; // Оновлює горизонтальну позицію
        y += moveVector.getY() * velocity * elapsedSeconds * canvasWidth / canvasHeight; // Оновлює вертикальну позицію
    }

    public void bounceHorizontally() { // Відбиває м'яч від вертикальної поверхні
        moveVector = new Point2D(-moveVector.getX(), moveVector.getY()); // Змінює горизонтальний напрямок
    }

    public void bounceVertically() { // Відбиває м'яч від горизонтальної поверхні
        moveVector = new Point2D(moveVector.getX(), -moveVector.getY()); // Змінює вертикальний напрямок
    }

    public void bounceFromPaddle(double hitOffset) { // Враховує місце удару по платформі
        hitOffset = Math.max(-1.0, hitOffset); // Обмежує значення лівим краєм платформи
        hitOffset = Math.min(1.0, hitOffset); // Обмежує значення правим краєм платформи
        double maximumAngle = Math.toRadians(70); // Визначає найбільший кут відскоку
        double angle = hitOffset * maximumAngle; // Обчислює кут за місцем удару
        moveVector = new Point2D(Math.sin(angle), -Math.cos(angle)).normalize(); // Спрямовує м'яч угору під новим кутом
    }

    public Point2D getTopPoint() { // Повертає найвищу точку м'яча
        return new Point2D(x + width / 2.0, y); // Створює точку посередині верхнього краю
    }

    public Point2D getBottomPoint() { // Повертає найнижчу точку м'яча
        return new Point2D(x + width / 2.0, y + height); // Створює точку посередині нижнього краю
    }

    public Point2D getLeftPoint() { // Повертає крайню ліву точку м'яча
        return new Point2D(x, y + height / 2.0); // Створює точку посередині лівого краю
    }

    public Point2D getRightPoint() { // Повертає крайню праву точку м'яча
        return new Point2D(x + width, y + height / 2.0); // Створює точку посередині правого краю
    }

    public boolean isMovingDown() { // Перевіряє, чи рухається м'яч униз
        return moveVector.getY() > 0; // Повертає результат перевірки вертикального напрямку
    }

    @Override
    public void draw(GraphicsContext graphicsContext) { // Малює м'яч на полотні
        graphicsContext.setFill(Color.WHITE); // Вибирає білий колір м'яча
        graphicsContext.fillOval(x * canvasWidth, y * canvasHeight,
                width * canvasWidth, height * canvasHeight); // Малює заповнене біле коло
    }
}
