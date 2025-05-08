package com.fruitninja;
//BonusItem.java: implements power‑up items (extra life, slow motion)

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class BonusItem extends GameObject {
    public enum BonusType { EXTRA_LIFE, SLOW_MOTION }

    private final BonusType bonusType;
    private final Image image;   

   
    public BonusItem(double x, double y, double vx, double vy, BonusType t) {
        super(x, y, vx, vy);
        this.bonusType = t;
        String imgPath = (t == BonusType.EXTRA_LIFE)
            ? Constants.IMG_BONUS_STAR_PATH
            : Constants.IMG_BONUS_CLOCK_PATH;
        this.image = new ImageIcon(imgPath).getImage();
    }

    @Override
    protected Image getImage() {
       
        return image;
    }

    @Override
    public int getRadius() {
        return Constants.BONUS_ITEM_RADIUS;
    }

    @Override
    public double getScaleFactor() {
        return Constants.BOMB_SCALE_FACTOR * 2;
    }
    
    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);

        Graphics2D g = (Graphics2D) g2d.create();
        int cx = getX(); 
        int cy = getY();
        int rad = (int)(getRadius() * getScaleFactor());

        float phase = (System.currentTimeMillis() % 1000) / 1000f; 
        float glow = 0.2f + 0.4f * (0.5f + 0.5f * (float)Math.sin(phase * 2 * Math.PI));

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glow));
        g.setColor(new Color(255, 255, 255, (int)(glow * 255)));
        g.fillOval(cx - rad*2, cy - rad*2, rad*4, rad*4);
        g.dispose();
    }

	public int getPointValue() {
	    switch (bonusType) {
	        case EXTRA_LIFE:
	            return Constants.BONUS_POINTS_EXTRA_LIFE;
	        case SLOW_MOTION:
	            return Constants.BONUS_POINTS_SLOW_MOTION;
	        default:
	            return 0;
	    }
	}
	
	public BonusType getBonusType() {
	    return bonusType;
	}

}
