import java.awt.Color;
import java.awt.Graphics;

public class Fruit extends GameObject {
    public enum FruitType { APPLE, ORANGE, BANANA }
    private static final int[] POINTS = {5, 10, 15};
    private FruitType type;
    private int typeIndex;

    public Fruit(int x, int y, double vx, double vy, int typeIndex) {
        super(x, y, vx, vy, 20);
        this.typeIndex = typeIndex;
        this.type = FruitType.values()[typeIndex];
    }

    public int getPointValue() {
        return POINTS[typeIndex];
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    @Override
    public void draw(Graphics g) {
        // Draw placeholder circle if sprite missing
        g.setColor(type == FruitType.APPLE ? Color.RED :
                   type == FruitType.ORANGE ? Color.ORANGE :
                                              Color.YELLOW);
        g.fillOval((int)x - radius, (int)y - radius, radius*2, radius*2);
        g.setColor(Color.BLACK);
        g.drawOval((int)x - radius, (int)y - radius, radius*2, radius*2);
    }
}
