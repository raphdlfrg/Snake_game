package tsp.snake_game.model;

import Math.sqrt

public class Coordinates {

    private float X;
    private float Y;

    public Coordinates(float x, float y) {
        x=X;
        y=Y;

    }
    public boolean equals(Coordinates point) {
        if (X != point.X) {
            return false;
        }
        if (Y != point.Y) {
            return false;
        }
        return true;
    }
    public float getX() {
        return X;
    }
    public void setX(float x) {
        X = x;
    }
    public float getY() {
        return Y;
    }
    public void setY(float y) {
        Y = y;
    }

    public void moveTo(float x, float y) {
        X = x;
        Y = y;
    }
    public void moveTo(Coordinates point) {
        X = point.X;
        Y = point.Y;
    }
    public void translate(float dx, float dy) {
        X += dx;
        Y += dy;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "X=" + X +
                ", Y=" + Y +
                '}';
    }
    public Coordinates getLocation(Coordinates point) {
        return point;
    }
    public float distanceTo(Coordinates point) {
        return Math.sqrt(Math.pow((point.X-X),2) + Math.pow((point.Y-Y),2));
    }
}
