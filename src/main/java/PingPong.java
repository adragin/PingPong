import model.DiagonalShift;
import model.Point;

import java.util.*;

public class PingPong {
    int rows;
    int cols;
    char[][] matrix;
    List<Point> historyPosition = new ArrayList<>();
    List<String> way = new ArrayList<>();
    boolean isLoop = false;

    public PingPong() {
        int[] initSize = init();
        rows = initSize[0];
        cols = initSize[1];
        matrix = new char[rows][cols];
        for (char[] row : matrix) {
            Arrays.fill(row, ' ');
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PingPong instance = new PingPong();

        Point startPoint = instance.getRandomElementInMatrix();
        instance.matrix[startPoint.getX()][startPoint.getY()] = 'O';

        Point currentPoint = startPoint;
        DiagonalShift currentShift = getStartRandomDiagonalShift(instance.matrix, startPoint);

        instance.historyPosition.add(startPoint);
        instance.way.add(currentShift.getArrow());

        // Скрываем курсор
        System.out.print("\033[?25l");

        do {
            printMatrixWithFrame(instance.matrix, currentPoint, currentShift);

            Point previewPoint = currentPoint.clone();
            currentPoint = makeShift(instance.matrix, currentPoint, currentShift);

            instance.matrix[currentPoint.getX()][currentPoint.getY()] = 'O';
            if (!currentPoint.equals(previewPoint)) {
                instance.matrix[previewPoint.getX()][previewPoint.getY()] = '\u2e31';
            }

            currentShift = getNextDirection(instance.matrix, currentPoint, currentShift);
            if (currentPoint.equals(startPoint) && currentShift.getArrow().equals(instance.way.get(0))) {
                instance.isLoop = true;
            }
            instance.historyPosition.add(currentPoint);
            instance.way.add(currentShift.getArrow());
            int delay = 500;
            Thread.sleep(delay);
        } while (isNotCorner(instance.matrix, currentPoint) && !instance.isLoop);

        printMatrixWithFrame(instance.matrix, currentPoint, currentShift);

        // Показываем курсор
        System.out.print("\033[?25h");
        // Восстанавливаем позицию курсора на две строки ниже
        System.out.print("\033[" + (instance.rows + 1) + "B");

        instance.way.remove(instance.way.size() - 1); // переходов должно быть на 1 меньше точек перехода
        System.out.println();
        System.out.println(instance.isLoop ? "The loop is closed!" : "The corner has been reached!");

        System.out.println("History of points: " + Arrays.toString(instance.historyPosition.toArray()));
        System.out.println("   Path of moving: " + Arrays.toString(instance.way.toArray()));
        System.out.println();
        System.out.println("\n\u00A9 2024 Alex Drahin. All rights reserved.");
        System.out.println("August 2024 \uD83D\uDE0A");
    }

    private int[] init() {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println("Set the size of matrix:");
        System.out.print("    columns >> ");
        int y = sc.nextInt();
        System.out.print("       rows >> ");
        int x = sc.nextInt();

        return new int[]{x, y};
    }

    public static DiagonalShift getStartRandomDiagonalShift(char[][] matrix, Point startPoint) {
        Random random = new Random();
        DiagonalShift result;

        do {
            DiagonalShift[] values = DiagonalShift.values();
            int index = random.nextInt(values.length);
            result = values[index];
        } while (!isValidStartShift(matrix, startPoint, result));

        return result;
    }

    private static boolean isValidStartShift(char[][] matrix, Point startPoint, DiagonalShift shift) {
        int newX = startPoint.getX() + shift.getRowChange();
        int newY = startPoint.getY() + shift.getColChange();

        if (newX < 0 || newX == matrix.length) return false;
        if (newY < 0 || newY == matrix[0].length) return false;

        return true;
    }

    public static DiagonalShift getNextDirection(char[][] matrix, Point currentPoint, DiagonalShift currentShift) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        int x = currentPoint.getX();
        int y = currentPoint.getY();

        switch (currentShift) {
            case RIGHT_UP:
                if (x == 0) return DiagonalShift.RIGHT_DOWN;
                if (y == numCols - 1) return DiagonalShift.LEFT_UP;
                break;
            case RIGHT_DOWN:
                if (x == numRows - 1) return DiagonalShift.RIGHT_UP;
                if (y == numCols - 1) return DiagonalShift.LEFT_DOWN;
                break;
            case LEFT_UP:
                if (x == 0) return DiagonalShift.LEFT_DOWN;
                if (y == 0) return DiagonalShift.RIGHT_UP;
                break;
            case LEFT_DOWN:
                if (x == numRows - 1) return DiagonalShift.LEFT_UP;
                if (y == 0) return DiagonalShift.RIGHT_DOWN;
                break;
        }
        return currentShift;
    }

    public static Point makeShift(char[][] matrix, Point currPoint, DiagonalShift shift) {
        int newX = currPoint.getX() + shift.getRowChange();
        int newY = currPoint.getY() + shift.getColChange();

        if (!isInBounds(matrix, newX, newY)) {
            newX = currPoint.getX();
            newY = currPoint.getY();
        }

        return new Point(newX, newY);
    }

    public static boolean isInBounds(char[][] matrix, int row, int col) {
        return row >= 0 && row < matrix.length && col >= 0 && col < matrix[0].length;
    }

    public Point getRandomElementInMatrix() {
        int x = (int) (Math.random() * rows);
        int y = (int) (Math.random() * cols);
        return new Point(x, y);
    }

    public static boolean isNotCorner(char[][] matrix, Point currentPoint) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;
        int x = currentPoint.getX();
        int y = currentPoint.getY();

        boolean left_top = (x == 0 && y == 0);               // Левый верхний угол
        boolean right_top = (x == 0 && y == numCols - 1);    // Правый верхний угол
        boolean left_bottom = (x == numRows - 1 && y == 0);  // Левый нижний угол
        boolean right_bottom = (x == numRows - 1 && y == numCols - 1);  // Правый нижний угол

        return !(left_top || right_top || left_bottom || right_bottom);
    }

    public static void printMatrixWithFrame(char[][] matrix, Point currentPoint, DiagonalShift shift) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;

        // Сохраняем текущую позицию курсора
        System.out.print("\033[s");

        // верхняя граница
        System.out.print("\u250c");
        for (int j = 0; j < numCols; j++) {
            System.out.print("\u2500\u2500");
        }
        System.out.print("\u2500\u2510");
        System.out.println();

        // матрица
        for (int row = 0; row < numRows; row++) {
            System.out.print("\u2502");
            for (int col = 0; col < matrix[row].length; col++) {
                if (row == currentPoint.getX() && col == currentPoint.getY()) {
                    System.out.print("\033[34m" + " " + matrix[row][col] + "\033[0m");
                } else {
                    System.out.print(" " + matrix[row][col]);
                }
            }
            System.out.print(" \u2502");
            System.out.println();

        }

        // нижняя граница
        System.out.print("\u2514");
        for (int j = 0; j < numCols; j++) {
            System.out.print("\u2500\u2500");
        }
        System.out.print("\u2500\u2518");
        System.out.println();

        // Восстанавливаем позицию курсора
        System.out.print("\033[u");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // верхняя граница
        sb.append("\u250c");
        sb.append("\u2500\u2500".repeat(cols));
        sb.append("\u2500\u2510\n");
        // матрица
        for (int i = 0; i < rows; i++) {
            sb.append("\u2502");
            for (int j = 0; j < cols; j++) {
                String element = String.format("%2c", matrix[i][j]);
                sb.append(element);
            }
            sb.append(" \u2502\n");
        }
        // нижняя граница
        sb.append("\u2514");
        sb.append("\u2500\u2500".repeat(cols));
        sb.append("\u2500\u2518\n");
        return sb.toString();
    }
}

