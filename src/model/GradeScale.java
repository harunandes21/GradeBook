package model;

public enum GradeScale {
    A(90, 4.0, "A"),
    B(80, 3.0, "B"),
    C(70, 2.0, "C"),
    D(60, 1.0, "D"),
    E(0, 0.0, "E");

    private final int minPercentage;
    private final double gpaValue;
    private final String letter;

    GradeScale(int minPercentage, double gpaValue, String letter) {
        this.minPercentage = minPercentage;
        this.gpaValue = gpaValue;
        this.letter = letter;
    }

    public double getGpaValue() {
        return gpaValue;
    }

    public String getLetter() {
        return letter;
    }

    public static GradeScale fromPercentage(double percentage) {
        for (GradeScale scale : GradeScale.values()) {
            if (percentage >= scale.minPercentage) {
                return scale;
            }
        }
        return E;
    }
}
