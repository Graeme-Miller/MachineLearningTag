package com.mayorgraeme;

public class Vector {


    double magnitude;
    Direction direction;

    public Vector(double magnitude, Direction direction) {
        this.magnitude = magnitude;
        this.direction = direction;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "Vector{" +
                "magnitude=" + magnitude +
                ", direction=" + direction +
                '}';
    }


}
