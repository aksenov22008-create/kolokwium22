package i;

import javafx.geometry.Point2D; // Зберігає точки, потрібні для перевірки зіткнення
import javafx.scene.canvas.GraphicsContext; // Надає інструменти для малювання на полотні
import javafx.scene.paint.Color; // Зберігає колір цеглини

public class Brick extends GraphicsItem { // Представляє одну цеглину рівня
    public enum CrushType { // Описує результат зіткнення м'яча з цеглиною
        NoCrush, // Означає відсутність зіткнення
        HorizontalCrush, // Означає удар у лівий або правий бік
        VerticalCrush // Означає удар у верхній або нижній бік
    }

    private static int gridRows; // Зберігає кількість рядків у сітці
    private static int gridCols; // Зберігає кількість стовпців у сітці
    private final Color color; // Зберігає основний колір цеглини

    public static void setGridSize(int rows, int cols) { // Встановлює розмір сітки для всіх цеглин
        gridRows = rows; // Запам'ятовує кількість рядків
        gridCols = cols; // Запам'ятовує кількість стовпців
    }

    public Brick(int gridX, int gridY, Color color) { // Створює цеглину у вибраній клітинці сітки
        x = (double) gridX / gridCols; // Перетворює номер стовпця на координату від 0 до 1
        y = (double) gridY / gridRows; // Перетворює номер рядка на координату від 0 до 1
        width = 1.0 / gridCols; // Встановлює ширину однієї клітинки
        height = 1.0 / gridRows; // Встановлює висоту однієї клітинки
        this.color = color; // Запам'ятовує колір цеглини
    }

    public CrushType checkCrush(Point2D topPoint, Point2D bottomPoint,
                                Point2D leftPoint, Point2D rightPoint) { // Визначає напрямок зіткнення з м'ячем
        boolean verticalHit = contains(topPoint) || contains(bottomPoint); // Перевіряє верхню та нижню точки м'яча
        boolean horizontalHit = contains(leftPoint) || contains(rightPoint); // Перевіряє ліву та праву точки м'яча

        if (!verticalHit && !horizontalHit) { // Перевіряє, чи жодна крайня точка не торкнулася цеглини
            return CrushType.NoCrush; // Повідомляє про відсутність зіткнення
        }
        if (horizontalHit && !verticalHit) { // Перевіряє удар збоку цеглини
            return CrushType.HorizontalCrush; // Вимагає змінити горизонтальний напрямок
        }
        if (verticalHit && !horizontalHit) { // Перевіряє удар зверху або знизу
            return CrushType.VerticalCrush; // Вимагає змінити вертикальний напрямок
        }

        double horizontalDistance = Math.min(Math.abs(rightPoint.getX() - x),
                Math.abs(x + width - leftPoint.getX())); // Обчислює глибину горизонтального входження
        double verticalDistance = Math.min(Math.abs(bottomPoint.getY() - y),
                Math.abs(y + height - topPoint.getY())); // Обчислює глибину вертикального входження

        if (horizontalDistance < verticalDistance) { // Вибирає ближчу сторону при ударі в кут
            return CrushType.HorizontalCrush; // Визначає зіткнення як горизонтальне
        }
        return CrushType.VerticalCrush; // Визначає зіткнення як вертикальне
    }

    private boolean contains(Point2D point) { // Перевіряє належність точки прямокутнику цеглини
        boolean insideX = point.getX() >= x && point.getX() <= x + width; // Перевіряє горизонтальні межі
        boolean insideY = point.getY() >= y && point.getY() <= y + height; // Перевіряє вертикальні межі
        return insideX && insideY; // Повертає правду лише для точки всередині цеглини
    }

    @Override
    public void draw(GraphicsContext graphicsContext) { // Малює цеглину з простим об'ємним ефектом
        double gap = 2.0; // Залишає невеликий проміжок між цеглинами
        double pixelX = x * canvasWidth + gap; // Перетворює координату x на пікселі
        double pixelY = y * canvasHeight + gap; // Перетворює координату y на пікселі
        double pixelWidth = width * canvasWidth - gap * 2; // Обчислює ширину з урахуванням проміжку
        double pixelHeight = height * canvasHeight - gap * 2; // Обчислює висоту з урахуванням проміжку

        graphicsContext.setFill(color); // Вибирає основний колір цеглини
        graphicsContext.fillRect(pixelX, pixelY, pixelWidth, pixelHeight); // Малює основу цеглини
        graphicsContext.setStroke(color.brighter()); // Вибирає світлий колір верхнього та лівого краю
        graphicsContext.setLineWidth(3); // Встановлює товщину об'ємної рамки
        graphicsContext.strokeLine(pixelX, pixelY, pixelX + pixelWidth, pixelY); // Малює світлий верхній край
        graphicsContext.strokeLine(pixelX, pixelY, pixelX, pixelY + pixelHeight); // Малює світлий лівий край
        graphicsContext.setStroke(color.darker()); // Вибирає темний колір нижнього та правого краю
        graphicsContext.strokeLine(pixelX, pixelY + pixelHeight,
                pixelX + pixelWidth, pixelY + pixelHeight); // Малює темний нижній край
        graphicsContext.strokeLine(pixelX + pixelWidth, pixelY,
                pixelX + pixelWidth, pixelY + pixelHeight); // Малює темний правий край
    }
}
