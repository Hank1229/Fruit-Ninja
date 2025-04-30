import java.awt.Color;
import java.awt.Graphics;

public class Bomb extends GameObject {
    private int frameCounter = 0;

    public Bomb(int x, int y, double vx, double vy) {
        super(x, y, vx, vy, 20);
    }

    public int getFrameCounter() {
        return frameCounter;
    }

    public void incrementFrameCounter() {
        frameCounter++;
    }

    @Override
    public void draw(Graphics g) {
        // placeholder circle if sprite missing
        g.setColor(Color.BLACK);
        g.fillOval((int)x - radius, (int)y - radius, radius*2, radius*2);
        g.setColor(Color.WHITE);
        g.drawString("B", (int)x - 4, (int)y + 4);
    }
}
