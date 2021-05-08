package com.bonnalenzo.tntim.util;

/**
 * Represents a point coordinate, a speed vector or an acceleration/force vector
 */
public class Vector implements Cloneable {
    private float x;
    private float y;

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Vector clone() {
        return new Vector(this.x, this.y);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void addVector(Vector other) {
        this.x += other.getX();
        this.y += other.getY();
    }
}
