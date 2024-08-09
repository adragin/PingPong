package model;

public enum DiagonalShift {
    RIGHT_UP(1, -1, "\u2197"),    // Сдвиг вправо-вверх
    RIGHT_DOWN(1, 1, "\u2198"), // Сдвиг вправо-вниз
    LEFT_UP(-1, -1, "\u2196"),    // Сдвиг влево-вверх
    LEFT_DOWN(-1, 1, "\u2199"); // Сдвиг влево-вниз

    private final int rowChange;
    private final int colChange;
    private final String arrow;

    DiagonalShift(int colChange, int rowChange, String arrow) {
        this.rowChange = rowChange;
        this.colChange = colChange;
        this.arrow = arrow;
    }

    public int getRowChange() {
        return rowChange;
    }

    public int getColChange() {
        return colChange;
    }

    public String getArrow() {
        return arrow;
    }
}
