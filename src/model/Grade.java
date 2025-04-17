package model;

/**
 * Represents a grade received on an assignment
 */
public class Grade {
    private double pointsEarned;
    private String feedback;

    public Grade(double pointsEarned, String feedback) {
    	setPointsEarned(pointsEarned);
        this.feedback = feedback;
    }

    public double getPointsEarned() {
        return pointsEarned;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setPointsEarned(double pointsEarned) {
        if (pointsEarned < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
        this.pointsEarned = pointsEarned;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return "Grade [pointsEarned=" + pointsEarned + ", feedback=" + feedback + "]";
    }
}
