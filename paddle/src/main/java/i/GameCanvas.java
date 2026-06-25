package i;

import javafx.animation.AnimationTimer; // Викликає оновлення гри перед кожним кадром
import javafx.geometry.Point2D; // Зберігає позицію центра м'яча
import javafx.scene.canvas.Canvas; // Створює полотно для гри
import javafx.scene.canvas.GraphicsContext; // Надає інструменти для малювання
import javafx.scene.paint.Color; // Надає кольори для фону та цеглин
import java.util.ArrayList; // Надає змінний список елементів
import java.util.List; // Описує список цеглин

public class GameCanvas extends Canvas { // Містить логіку та відображення всієї гри
    private final GraphicsContext graphicsContext; // Зберігає контекст малювання полотна
    private final Paddle paddle; // Зберігає платформу гравця
    private final Ball ball; // Зберігає ігровий м'яч
    private final List<Brick> bricks; // Зберігає цеглини поточного рівня
    private boolean gameRunning = false; // Вказує, чи почався рух м'яча
    private long previousFrameTime = 0; // Зберігає час попереднього кадру

    public GameCanvas(double width, double height) { // Створює та налаштовує гру
        super(width, height); // Створює полотно заданого розміру
        graphicsContext = getGraphicsContext2D(); // Отримує інструмент для малювання
        GraphicsItem.setCanvasSize(width, height); // Передає розмір полотна всім елементам
        paddle = new Paddle(); // Створює платформу
        ball = new Ball(); // Створює м'яч
        bricks = new ArrayList<>(); // Створює порожній список цеглин
        loadLevel(); // Заповнює рівень кольоровими цеглинами

        setOnMouseMoved(event -> { // Реагує на переміщення курсора над полотном
            paddle.updatePosition(event.getX()); // Пересуває платформу за курсором
        });
        setOnMouseDragged(event -> { // Реагує на рух миші із затиснутою кнопкою
            paddle.updatePosition(event.getX()); // Також пересуває платформу за курсором
        });
        setOnMousePressed(event -> { // Реагує на натискання кнопки миші
            gameRunning = true; // Запускає рух м'яча
        });

        AnimationTimer timer = new AnimationTimer() { // Створює головний цикл гри
            @Override
            public void handle(long currentTime) { // Виконується перед відображенням кожного кадру
                double elapsedSeconds = calculateElapsedSeconds(currentTime); // Обчислює час між кадрами
                update(elapsedSeconds); // Оновлює стан гри
                draw(); // Малює оновлений кадр
            }
        };
        timer.start(); // Запускає постійне оновлення гри
    }

    private double calculateElapsedSeconds(long currentTime) { // Обчислює тривалість попереднього кадру
        if (previousFrameTime == 0) { // Перевіряє, чи це перший кадр
            previousFrameTime = currentTime; // Запам'ятовує час першого кадру
            return 0; // Не пересуває м'яч під час першого кадру
        }
        double elapsedSeconds = (currentTime - previousFrameTime) / 1_000_000_000.0; // Перетворює наносекунди на секунди
        previousFrameTime = currentTime; // Запам'ятовує час поточного кадру
        return Math.min(elapsedSeconds, 0.05); // Обмежує великий стрибок після затримки програми
    }

    public void draw() { // Малює повний кадр гри
        graphicsContext.setFill(Color.BLACK); // Вибирає чорний колір фону
        graphicsContext.fillRect(0, 0, getWidth(), getHeight()); // Очищає все полотно чорним кольором
        for (Brick brick : bricks) { // Проходить по всіх цеглинах рівня
            brick.draw(graphicsContext); // Малює поточну цеглину
        }
        paddle.draw(graphicsContext); // Малює платформу
        ball.draw(graphicsContext); // Малює м'яч
    }

    private void update(double elapsedSeconds) { // Оновлює позиції та перевіряє зіткнення
        if (!gameRunning) { // Перевіряє, чи гра очікує натискання миші
            attachBallToPaddle(); // Тримає м'яч над платформою
            return; // Завершує оновлення без руху м'яча
        }

        ball.updatePosition(elapsedSeconds); // Пересуває м'яч відповідно до швидкості

        if (shouldBallBounceHorizontally()) { // Перевіряє удар у бічну стіну
            ball.bounceHorizontally(); // Змінює горизонтальний напрямок м'яча
        }
        if (shouldBallBounceVertically()) { // Перевіряє удар у верхню стіну
            ball.bounceVertically(); // Змінює вертикальний напрямок м'яча
        }
        if (shouldBallBounceFromPaddle()) { // Перевіряє удар у платформу
            double ballCenter = ball.getX() + ball.getWidth() / 2.0; // Обчислює центр м'яча
            double distanceFromCenter = ballCenter - paddle.getCenterX(); // Обчислює відстань від центра платформи
            double hitOffset = distanceFromCenter / (paddle.getWidth() / 2.0); // Перетворює відстань на діапазон від -1 до 1
            ball.bounceFromPaddle(hitOffset); // Відбиває м'яч під кутом залежно від місця удару
        }

        checkBrickCollisions(); // Перевіряє зіткнення з цеглинами

        if (ball.getY() > 1.0) { // Перевіряє, чи м'яч упав нижче екрана
            gameRunning = false; // Зупиняє поточну спробу
            attachBallToPaddle(); // Повертає м'яч на платформу
        }
        if (bricks.isEmpty()) { // Перевіряє, чи гравець знищив усі цеглини
            loadLevel(); // Завантажує рівень знову
            gameRunning = false; // Чекає нового натискання для старту
        }
    }

    private void attachBallToPaddle() { // Розміщує м'яч безпосередньо над платформою
        double ballX = paddle.getCenterX(); // Бере горизонтальний центр платформи
        double ballY = paddle.getY() - ball.getHeight() / 2.0; // Обчислює позицію над платформою
        ball.setPosition(new Point2D(ballX, ballY)); // Встановлює центр м'яча у розрахованій точці
    }

    private boolean shouldBallBounceHorizontally() { // Перевіряє зіткнення з лівою або правою стіною
        boolean touchesLeftWall = ball.getX() <= 0.0; // Перевіряє ліву межу
        boolean touchesRightWall = ball.getX() + ball.getWidth() >= 1.0; // Перевіряє праву межу
        return touchesLeftWall || touchesRightWall; // Повертає результат перевірки бічних стін
    }

    private boolean shouldBallBounceVertically() { // Перевіряє зіткнення з верхньою стіною
        return ball.getY() <= 0.0; // Повертає правду, коли м'яч торкається верхнього краю
    }

    private boolean shouldBallBounceFromPaddle() { // Перевіряє зіткнення м'яча з платформою
        boolean movingDown = ball.isMovingDown(); // Не дозволяє повторний відскок м'яча, який уже летить угору
        boolean insidePaddleWidth = ball.getX() + ball.getWidth() >= paddle.getX()
                && ball.getX() <= paddle.getX() + paddle.getWidth(); // Перевіряє горизонтальне перекриття
        boolean insidePaddleHeight = ball.getY() + ball.getHeight() >= paddle.getY()
                && ball.getY() <= paddle.getY() + paddle.getHeight(); // Перевіряє вертикальне перекриття
        return movingDown && insidePaddleWidth && insidePaddleHeight; // Повертає повний результат зіткнення
    }

    private void checkBrickCollisions() { // Шукає першу цеглину, якої торкнувся м'яч
        for (int index = 0; index < bricks.size(); index++) { // Перебирає цеглини за їхніми індексами
            Brick brick = bricks.get(index); // Отримує поточну цеглину
            Brick.CrushType crushType = brick.checkCrush(ball.getTopPoint(), ball.getBottomPoint(),
                    ball.getLeftPoint(), ball.getRightPoint()); // Визначає тип зіткнення

            if (crushType == Brick.CrushType.HorizontalCrush) { // Перевіряє удар у бічну сторону цеглини
                ball.bounceHorizontally(); // Змінює горизонтальний напрямок м'яча
                bricks.remove(index); // Видаляє розбиту цеглину
                return; // Завершує перевірку після одного зіткнення
            }
            if (crushType == Brick.CrushType.VerticalCrush) { // Перевіряє удар зверху або знизу
                ball.bounceVertically(); // Змінює вертикальний напрямок м'яча
                bricks.remove(index); // Видаляє розбиту цеглину
                return; // Завершує перевірку після одного зіткнення
            }
        }
    }

    private void loadLevel() { // Створює початковий набір цеглин
        bricks.clear(); // Видаляє цеглини попереднього рівня
        Brick.setGridSize(20, 10); // Ділить полотно на двадцять рядків і десять стовпців
        Color[] colors = {Color.CRIMSON, Color.DARKORANGE, Color.GOLD,
                Color.LIMEGREEN, Color.DEEPSKYBLUE, Color.MEDIUMPURPLE}; // Готує окремий колір для кожного рядка

        for (int row = 2; row <= 7; row++) { // Створює цеглини в рядках від другого до сьомого
            for (int column = 0; column < 10; column++) { // Заповнює всі десять стовпців
                bricks.add(new Brick(column, row, colors[row - 2])); // Додає кольорову цеглину до рівня
            }
        }
    }
}
