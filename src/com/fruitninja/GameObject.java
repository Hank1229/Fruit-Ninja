package com.fruitninja;
//GameObject.java: abstract base class for all moving game objects

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform; // For rotation if needed later

import com.fruitninja.BonusItem.BonusType;

public abstract class GameObject {
    protected double x, y, vx, vy;
    // Rotation angle in radians, and angular velocity if needed
    // protected double angle = 0.0;
    // protected double angularVelocity = 0.0;
    public static double globalSpeed = Constants.GLOBAL_SPEED_NORMAL;


    public GameObject(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

   
    public void update() {
        x += vx * globalSpeed;
        y += vy * globalSpeed;
        vy += Constants.GRAVITY_ACCELERATION; // Use constant
        // angle += angularVelocity * globalSpeed; // Add if implementing rotation

 
        if (y - getRadiusScaled() < 0) {
            y = getRadiusScaled();
            if (vy < 0) {
                vy = 0;
            }
        }
    }
    /**
     * Draws the object, applying scaling.
     * Subclasses provide the image and base radius.
     * Subclasses override getScaleFactor().
     */
    public void draw(Graphics2D g2d) {
        Image img = getImage();
        // Allow drawing even if image is null (e.g., BonusItem custom draw)
        // but scaling/positioning depends on radius.

        double scale = getScaleFactor();
        int radius = getRadius(); // Base radius
        int size = (int)(radius * 2 * scale); // Scaled size for drawing

        if (size <= 0) return; // Cannot draw if size is zero or negative

        int drawX = (int)(x - size / 2.0);
        int drawY = (int)(y - size / 2.0);

        if (img != null) {
        
            // Simple draw without rotation:
             g2d.drawImage(img, drawX, drawY, size, size, null);
        } else {
    
             drawPlaceholder(g2d, drawX, drawY, size); // Optional: Draw something if image is null but draw isn't overridden
        }
    }

    protected void drawPlaceholder(Graphics2D g2d, int drawX, int drawY, int size) {
         // g2d.setColor(Color.MAGENTA);
         // g2d.drawOval(drawX, drawY, size, size);
         // g2d.drawString("?", (int)x, (int)y);
    }


    /** Subclasses must provide the base image */
    protected abstract Image getImage();

    /** Subclasses must provide the base radius (before scaling) */
    public abstract int getRadius();

    /** Subclasses override to provide their specific scaling factor */
    public double getScaleFactor() {
        return 1.0; // Default scale
    }

    /** Calculate scaled radius */
    public int getRadiusScaled() {
        return (int)(getRadius() * getScaleFactor());
    }


    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
    public double getVx() { return vx; }
    public double getVy() { return vy; }



}