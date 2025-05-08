package com.fruitninja;
//FruitHalf.java: models a half‑fruit piece with its own trajectory

import java.awt.Image;

public class FruitHalf extends GameObject {
    private final Image image;
    private final int baseRadius;


    public FruitHalf(double x, double y, double vx, double vy, int originalFruitRadius, Image image) {
        super(x, y, vx, vy);
        this.baseRadius = originalFruitRadius;
        this.image = image;
        // this.angularVelocity = (new Random().nextDouble() - 0.5) * 0.2;
    }

    @Override
    public int getRadius() {
        return baseRadius;
    }

    @Override
    protected Image getImage() {
        return image;
    }

    @Override
    public double getScaleFactor() {
        return Constants.FRUIT_HALF_SCALE_FACTOR;
    }
}
