package com.fruitninja;

import java.awt.Color;
import java.awt.Point;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Image;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
//GamePanel.java: core game loop and rendering panel (spawning, updating, drawing)

public class GamePanel extends JPanel implements MouseListener, MouseMotionListener {
    private final List<GameObject> objects = new ArrayList<>();
    private final Deque<Point> swipe = new ArrayDeque<>();

    private Timer gameTimer, countdownTimer;
    private Image bg;
    private BufferedImage bgBuffer;
    private Image comboImg;

    private Clip sliceSnd, bombSndFatal, bombSndTime, swordSnd, starSnd, clockSnd, gameOverSnd;

    private int score, lives, timeLeft, slowTicks;
    private boolean gameOver;
    private Runnable onGameOver;
    private int tick;
    private final Random random = new Random();

    private static final int SLICE_POOL_SIZE = 5;
    private final List<Clip> slicePool = new ArrayList<>(SLICE_POOL_SIZE);
    private int sliceIndex = 0;

    private List<Effect> activeEffects = new ArrayList<>();
    private int comboCount = 0;
    private long lastSliceTime = 0L;
    private static final long COMBO_WINDOW = 800;

    public GamePanel() {
        setPreferredSize(new Dimension(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        // bg
        try { bg = new ImageIcon(Constants.IMG_BACKGROUND_PATH).getImage(); } catch (Exception e) { bg = null; }
        
        if (bg != null) {
            bgBuffer = new BufferedImage(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gBuf = bgBuffer.createGraphics();
            gBuf.drawImage(bg, 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, null);
            gBuf.dispose();
        }
        // Combo 
        try { comboImg = new ImageIcon(Constants.IMG_COMBO_PATH).getImage(); } catch (Exception e) { comboImg = null; }
        loadSounds();
        initTimers();
        initComboTimer();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void loadSounds() {
        try {
            for (int i = 0; i < SLICE_POOL_SIZE; i++) {
                Clip c = AudioSystem.getClip();
                c.open(AudioSystem.getAudioInputStream(new File(Constants.SND_SLICE_PATH)));
                slicePool.add(c);
            }
            swordSnd = AudioSystem.getClip();
            swordSnd.open(AudioSystem.getAudioInputStream(new File(Constants.SND_SWING_PATH)));
            bombSndFatal = AudioSystem.getClip();
            bombSndFatal.open(AudioSystem.getAudioInputStream(new File(Constants.SND_BOMB_FATAL_PATH)));
            bombSndTime = AudioSystem.getClip();
            bombSndTime.open(AudioSystem.getAudioInputStream(new File(Constants.SND_BOMB_TIME_PATH)));
            starSnd = AudioSystem.getClip();
            starSnd.open(AudioSystem.getAudioInputStream(new File(Constants.SND_EXTRA_LIFE_PATH)));
            clockSnd = AudioSystem.getClip();
            clockSnd.open(AudioSystem.getAudioInputStream(new File(Constants.SND_SLOW_MOTION_PATH)));
            gameOverSnd = AudioSystem.getClip();
            gameOverSnd.open(AudioSystem.getAudioInputStream(new File(Constants.SND_GAME_OVER_PATH)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void playSlice() {
        Clip c = slicePool.get(sliceIndex);
        if (c.isRunning()) c.stop();
        c.setFramePosition(0);
        c.start();
        sliceIndex = (sliceIndex + 1) % slicePool.size();
    }

    private void initTimers() {
        gameTimer = new Timer(Constants.GAME_TIMER_DELAY_MS, e -> updateFrame());
        countdownTimer = new Timer(Constants.COUNTDOWN_TIMER_DELAY_MS, e -> {
            if (!gameOver) {
                timeLeft--;
                if (timeLeft <= 0) { timeLeft = 0; endGame(); }
            }
            repaint();
        });
    }

    private void initComboTimer() {
        Timer comboTimer = new Timer(16, e -> {
            Iterator<Effect> it = activeEffects.iterator();
            while (it.hasNext()) {
                Effect ef = it.next();
                ef.update();
                if (ef.isFinished()) it.remove();
            }
            repaint();
        });
        comboTimer.start();
    }

    public void setOnGameOver(Runnable cb) { onGameOver = cb; }
    public int getScore() { return score; }

    public void startGame() {
        score = 0; lives = Constants.INITIAL_LIVES; timeLeft = Constants.INITIAL_TIME_SECONDS;
        slowTicks = 0; gameOver = false; tick = 0;
        objects.clear(); swipe.clear();
        GameObject.globalSpeed = Constants.GLOBAL_SPEED_NORMAL;
        gameTimer.start(); countdownTimer.start();
    }

    public void pauseGame() { gameTimer.stop(); countdownTimer.stop(); }
    public void resumeGame() { if (!gameOver) { gameTimer.start(); countdownTimer.start(); } }
    public void restartGame() { stopTimers(); startGame(); }
    private void stopTimers() { gameTimer.stop(); countdownTimer.stop(); }

    private void endGame() {
        if (gameOver) return;
        gameOver = true;
        stopTimers();
        if (gameOverSnd != null) { gameOverSnd.setFramePosition(0); gameOverSnd.start(); }
        if (onGameOver != null) onGameOver.run();
    }

    private void updateFrame() {
        if (gameOver) return;
        tick++;
        double vyFactor = (slowTicks > 0) ? 2.0 : 1.0;
        objects.removeIf(o -> o.getY() > getHeight() + Constants.OBJECT_START_Y_OFFSET ||
                              (o instanceof Explosion && ((Explosion)o).isFinished()));
        objects.forEach(GameObject::update);

        if (tick % Constants.FRUIT_SPAWN_INTERVAL == 0) {
            int spawnCount = random.nextInt(5) + 1;
            for (int i = 1; i <= spawnCount; i++) {
                double baseX = Constants.SCREEN_WIDTH * i / (spawnCount + 1.0);
                double region = Constants.SCREEN_WIDTH / (spawnCount + 1.0);
                double jitter = (random.nextDouble() - 0.5) * region * 0.8;
                double x = baseX + jitter;
                objects.add(new Fruit(x, getHeight() + Constants.OBJECT_START_Y_OFFSET,
                    randVx(), -randVy() * vyFactor));
            }
        }
        // Bomb uses same trajectory as fruits
        if (tick % Constants.BOMB_SPAWN_INTERVAL == 0) {
            double bVx = randVx();
            double bVy = randVy() * vyFactor;
            objects.add(new Bomb(
                randX(),
                getHeight() + Constants.OBJECT_START_Y_OFFSET,
                bVx,
                -bVy,
                random.nextBoolean()
            ));
        }
        // BonusItem uses same trajectory as fruits
        if (tick % Constants.BONUS_ITEM_SPAWN_INTERVAL == 0) {
            double biVx = randVx();
            double biVy = randVy() * vyFactor;
            BonusItem.BonusType t = random.nextBoolean()
                ? BonusItem.BonusType.EXTRA_LIFE
                : BonusItem.BonusType.SLOW_MOTION;
            objects.add(new BonusItem(
                randX(),
                getHeight() + Constants.OBJECT_START_Y_OFFSET,
                biVx,
                -biVy,
                t
            ));
        }
handleCollisions();
        GameObject.globalSpeed = (slowTicks > 0)
            ? Constants.GLOBAL_SPEED_SLOW_MOTION : Constants.GLOBAL_SPEED_NORMAL;
        if (slowTicks > 0) slowTicks--;
        repaint();
    }

    private int randX() { return random.nextInt(getWidth()); }
    private double randVx() { return (random.nextDouble()*2 - 1) * Constants.MAX_ABS_VX * 0.7; }
    private int randVy() {
        int base = Constants.MIN_INITIAL_VY + random.nextInt(Constants.MAX_INITIAL_VY_RANGE + 1);
        return base;
    }

    private void handleCollisions() {
        try {
            List<GameObject> toAdd = new ArrayList<>();
            Iterator<GameObject> itObj = objects.iterator();
            while (itObj.hasNext()) {
                GameObject o = itObj.next();
                if (o instanceof FruitHalf || o instanceof Explosion) continue;
                boolean hit = false;
                for (Point p : swipe) {
                    double dx = p.x - o.getX();
                    double dy = p.y - o.getY();
                    double r = o.getRadius() * o.getScaleFactor();
                    if (dx*dx + dy*dy <= r*r) { hit = true; break; }
                }
                if (!hit) continue;
                itObj.remove();

                if (o instanceof Fruit) {
                    Fruit f = (Fruit) o;
                    score += Constants.POINTS_PER_FRUIT;
                    List<Image> halves = Fruit.getHalfImages(Fruit.getName(f.getImage()));
                    if (halves.size() >= 2) {
                    	double hv = Constants.FRUIT_HALF_VELOCITY_DIVERGENCE;  

                    	toAdd.add(new FruitHalf(
                    	    f.getX() - 5,
                    	    f.getY(),
                    	    f.getX() - hv,         
                    	    f.getY() - hv * 0.5,   
                    	    f.getRadius(),
                    	    halves.get(0)
                    	));
                    	toAdd.add(new FruitHalf(
                    	    f.getX() + 5,
                    	    f.getY(),
                    	    f.getX() + hv,
                    	    f.getY() - hv * 0.5,
                    	    f.getRadius(),
                    	    halves.get(1)
                    	));

                    }
                    toAdd.add(new Explosion(f.getX(), f.getY(), Fruit.getName(f.getImage()), f.getRadius()));
                 // slice Snd
                    playSlice();

                    // Sword swipe
                    activeEffects.add(new SwordEffect(new ArrayList<>(swipe)));

                    
                    if (swordSnd != null) {
                        swordSnd.setFramePosition(0);
                        swordSnd.start();
                    }

                    long now = System.currentTimeMillis();
                    if (now - lastSliceTime <= COMBO_WINDOW) comboCount++;
                    else comboCount = 1;
                    lastSliceTime = now;
                    if (comboCount == 3) {  // 3 fruits
                        int offsetX = (int)f.getRadius() + 20;
                        int offsetY = -(int)f.getRadius() - 20;
                        activeEffects.add(new ComboEffect(comboCount, (int)f.getX()+offsetX, (int)f.getY()+offsetY));
                    }
                } else if (o instanceof Bomb) {
                    Bomb b = (Bomb) o;
                    if (b.isFatal() && bombSndFatal != null) { bombSndFatal.setFramePosition(0); bombSndFatal.start(); }
                    else if (bombSndTime != null) { bombSndTime.setFramePosition(0); bombSndTime.start(); }
                    toAdd.add(new Explosion(b.getX(), b.getY(), b.isFatal()?"fatalBomb":"timeBomb", b.getRadius()*3));
                    if (b.isFatal()) {
                        if (--lives <= 0) {
                            lives = 0;
                            new Timer(Constants.FATAL_BOMB_GAMEOVER_DELAY_MS, ev -> { ((Timer)ev.getSource()).stop(); endGame(); }).start();
                        }
                    } else {
                        timeLeft = Math.max(0, timeLeft - Constants.TIME_BOMB_PENALTY_SECONDS);
                        if (timeLeft <= 0) endGame();
                    }
                } else if (o instanceof BonusItem) {
                    BonusItem bi = (BonusItem) o;
                    score += bi.getPointValue();
                    if (bi.getBonusType() == BonusItem.BonusType.EXTRA_LIFE) { lives++; if (starSnd != null) { starSnd.setFramePosition(0); starSnd.start(); } }
                    else { slowTicks = Constants.SLOW_MOTION_DURATION_TICKS; if (clockSnd != null) { clockSnd.setFramePosition(0); clockSnd.start(); } }
                }
            }
            objects.addAll(toAdd);
            while (swipe.size() > Constants.MAX_SWIPE_POINTS) swipe.pollFirst();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        if (bgBuffer != null) {
            g2.drawImage(bgBuffer, 0, 0, null);
        } else if (bg != null) {
            g2.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        for (GameObject o : objects) o.draw(g2);

        if (swipe.size() >= 2) {
            GeneralPath blade = new GeneralPath();
            Iterator<Point> it = swipe.iterator();
            Point first = it.next(); blade.moveTo(first.x, first.y); while (it.hasNext()) { Point p = it.next(); blade.lineTo(p.x, p.y); }
            for (int layer = 3; layer >= 1; layer--) {
                float width = layer * 4f + 12f; int alphaLayer = 30 + layer * 30;
                g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                g2.setColor(new Color(180, 230, 255, alphaLayer));
                g2.draw(blade);
            }
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(255, 255, 255, 180)); g2.draw(blade);
        }

        for (Effect ef : activeEffects) ef.render(g2);

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Constants.SCORE_TEXT_COLOR);
        g2.drawString("Score: " + score, Constants.SCORE_DISPLAY_X, Constants.SCORE_DISPLAY_Y);
        g2.setColor(Color.WHITE);
        g2.drawString("Lives: " + lives, Constants.SCORE_DISPLAY_X, Constants.LIVES_DISPLAY_Y);
        g2.drawString("Time: " + timeLeft, Constants.SCORE_DISPLAY_X, Constants.TIME_DISPLAY_Y);
    }

    @Override public void mousePressed(MouseEvent e) { swipe.clear(); swipe.add(e.getPoint()); }
    @Override public void mouseReleased(MouseEvent e) { swipe.clear(); handleCollisions(); repaint(); }
    @Override public void mouseDragged(MouseEvent e) { swipe.add(e.getPoint()); }
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    private interface Effect { void update(); void render(Graphics2D g2); boolean isFinished(); }

    private class ComboEffect implements Effect {
        private final int combo; private final long startTime; private final long DURATION = 1200;
        private float radius=0f, alpha=0f, scale=1f; private boolean expanding=true;
        private final int originX, originY;
        public ComboEffect(int comboCount, int x, int y) { this.combo = comboCount; this.startTime = System.currentTimeMillis(); this.originX = x; this.originY = y; }
        @Override public void update() { long elapsed = System.currentTimeMillis() - startTime; if (elapsed > DURATION) return;
            if (expanding) { radius += 0.5f; alpha += 0.015f; scale += 0.0008f; if (radius >= 20f) expanding = false; }
            else { radius -= 0.5f; alpha -= 0.015f; scale -= 0.0008f; if (radius <= 0f) expanding = true; }
            alpha = Math.max(0f, Math.min(0.6f, alpha));
            scale = Math.max(1f, Math.min(1.2f, scale)); }
        @Override public void render(Graphics2D g2) { long elapsed = System.currentTimeMillis() - startTime; if (elapsed > DURATION || comboImg == null) return;
            int imgSize = 64;
            // 放大 2.5 倍
            int drawSize = (int)(imgSize * scale * 2.5f);
            int x = originX - drawSize/2, y = originY - drawSize/2;
            g2.drawImage(comboImg, x, y, drawSize, drawSize, null);
            String txt = combo + "×"; g2.setFont(new Font("Impact", Font.BOLD, 24)); g2.setColor(new Color(255,215,0,200));
            FontMetrics fm = g2.getFontMetrics(); int tw = fm.stringWidth(txt), th = fm.getAscent();
            int tx = originX - tw/2, ty = originY - drawSize/2 - 5; g2.drawString(txt, tx, ty); }
        @Override public boolean isFinished() { return System.currentTimeMillis() - startTime > DURATION; }
    }
        
        private class SwordEffect implements Effect {
            private final List<Point> path;
            private final long startTime;
            private final long DURATION = 200; // 200ms 

            public SwordEffect(List<Point> path) {
                this.path = path;
                this.startTime = System.currentTimeMillis();
            }

            @Override
            public void update() {
                
            }

            @Override
            public void render(Graphics2D g2) {
                long elapsed = System.currentTimeMillis() - startTime;
                float alpha = 1f - (float) elapsed / DURATION;
                if (alpha <= 0f || path.isEmpty()) return;

                GeneralPath gp = new GeneralPath();
                Point first = path.get(0);
                gp.moveTo(first.x, first.y);
                for (Point p : path) gp.lineTo(p.x, p.y);

                // multilayer
                for (int layer = 2; layer >= 0; layer--) {
                    float strokeWidth = layer * 4f + 8f;
                    int a = (int) (alpha * (180 - layer * 60));
                    g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(new Color(255, 255, 255, a));
                    g2.draw(gp);
                }
            }

            @Override
            public boolean isFinished() {
                return System.currentTimeMillis() - startTime > DURATION;
            }
        }

    }




