package com.fruitninja;
//Bomb.java: defines bomb entities and fatal/time‑bomb explosion logic

import java.awt.Image;
import javax.swing.ImageIcon;

public class Bomb extends GameObject {
    private final boolean fatal;
    private final Image image;

    public Bomb(double x, double y, double vx, double vy, boolean isFatal) {
        super(x, y, vx, vy);
        this.fatal = isFatal;
        String file = isFatal ? "fatalBomb.png" : "timeBomb.png";
        image = new ImageIcon(Constants.IMG_BOMB_PATH + file).getImage();
    }


    @Override protected Image getImage() {
        return image;
    }

    @Override public int getRadius() {
        return 50;
    }

    public boolean isFatal() { return fatal; }
    public boolean isTime()  { return !fatal; }
}