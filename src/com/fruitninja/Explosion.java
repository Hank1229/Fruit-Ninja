package com.fruitninja;
//Explosion.java: renders explosion effect when objects are sliced

import java.awt.*;
import javax.swing.ImageIcon;

public class Explosion extends GameObject {
    private final Image image;
    private final double scaleFactor;
    private int age = 0;
    private final int baseRadius;

    public Explosion(double x, double y, String type, int initialRadius) {
        super(x, y, 0, 0);
        this.baseRadius = initialRadius;
        //  scale
        if (type.equals("fatalBomb")) scaleFactor = 2;
        else if (type.equals("timeBomb")) scaleFactor = 0.2;
        else scaleFactor = 0.2;
        
        String ext = type.equals("fatalBomb") ? "gif" : "png";
        String path = Constants.IMG_EXPLOSION_PATH + type + "Explosion." + ext;
        Image tmp;
        try {
            tmp = new ImageIcon(path).getImage();
            if (tmp.getWidth(null) <= 0) tmp = null;
        } catch (Exception e) {
            tmp = null;
        }
        image = tmp;
    }

    public boolean isFinished() {
        return age >= Constants.EXPLOSION_MAX_AGE_TICKS;
    }

    @Override
    public void update() {
        age++;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (isFinished()) return;
        if (image != null) {
            float alpha = 1f - (float) age / Constants.EXPLOSION_MAX_AGE_TICKS;
            alpha = Math.max(0, Math.min(alpha, 1));
            Composite old = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int w = (int) (image.getWidth(null) * scaleFactor);
            int h = (int) (image.getHeight(null) * scaleFactor);
            g2d.drawImage(image,
                (int) (x - w/2.0), (int) (y - h/2.0),
                w, h,
                null);
            g2d.setComposite(old);
        } else {
            float alpha = 1f - (float) age / Constants.EXPLOSION_MAX_AGE_TICKS;
            alpha = Math.max(0, Math.min(alpha, 1));
            Color col = new Color(1f,0.5f,0f,alpha);
            g2d.setColor(col);
            int r = (int) (baseRadius * scaleFactor + age*2);
            g2d.fillOval((int)(x - r/2.0), (int)(y - r/2.0), r, r);
        }
    }

    @Override
    public int getRadius() {
        return (int) (baseRadius * scaleFactor);
    }

    @Override
    protected Image getImage() {
        return image;
    }
}
