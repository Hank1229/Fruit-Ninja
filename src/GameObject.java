import java.awt.Graphics;

public abstract class GameObject {
    protected double x, y;
    protected double vx, vy;
    protected int radius;
    protected boolean sliced = false;

    public GameObject(int x, int y, double vx, double vy, int radius) {
        this.x = x; this.y = y;
        this.vx = vx; this.vy = vy;
        this.radius = radius;
    }

    public void update(double speedFactor) {
        x += vx * speedFactor;
        y += vy * speedFactor;
        vy += GamePanel.GRAVITY * speedFactor;
    }

    public boolean isOffScreen(int width, int height) {
        return (y - radius > height) || (x + radius < 0) || (x - radius > width);
    }

    public boolean isSliced() {
        return sliced;
    }

    public void setSliced(boolean sliced) {
        this.sliced = sliced;
    }

    public boolean intersectsLine(int x1, int y1, int x2, int y2) {
        double cx = this.x, cy = this.y;
        double vx = x2 - x1, vy = y2 - y1;
        double wx = cx - x1, wy = cy - y1;
        double c1 = wx * vx + wy * vy;
        double c2 = vx * vx + vy * vy;
        double t = c1 / c2;
        t = Math.max(0, Math.min(1, t));
        double px = x1 + t * vx, py = y1 + t * vy;
        double dist = Math.hypot(cx - px, cy - py);
        return dist <= radius;
    }

    public abstract void draw(Graphics g);
}
