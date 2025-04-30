import java.awt.Color;
import java.awt.Graphics;

public class BonusItem extends GameObject {
    public enum BonusType { EXTRA_LIFE, SLOW_MOTION }
    private BonusType bonusType;
    private int pointValue;

    public BonusItem(int x, int y, double vx, double vy) {
        super(x, y, vx, vy, 20);
        if (Math.random() < 0.5) {
            bonusType = BonusType.EXTRA_LIFE;
            pointValue = 0;
        } else {
            bonusType = BonusType.SLOW_MOTION;
            pointValue = 5;
        }
    }

    public BonusType getBonusType() {
        return bonusType;
    }

    public int getPointValue() {
        return pointValue;
    }

    @Override
    public void draw(Graphics g) {
        // placeholder circle if sprite missing
        if (bonusType == BonusType.EXTRA_LIFE) {
            g.setColor(Color.PINK);
            g.fillOval((int)x - radius, (int)y - radius, radius*2, radius*2);
            g.setColor(Color.WHITE);
            g.drawString("+1", (int)x - 6, (int)y + 4);
        } else {
            g.setColor(Color.CYAN);
            g.fillOval((int)x - radius, (int)y - radius, radius*2, radius*2);
            g.setColor(Color.BLUE.darker());
            g.drawString("S", (int)x - 4, (int)y + 5);
        }
    }
}
