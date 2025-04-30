import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;
import java.util.*;

public class GamePanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    public static final int WIDTH = 800, HEIGHT = 600;
    public static final double GRAVITY = 0.5;

    private Timer timer;
    private List<GameObject> objects = new ArrayList<>();
    private Random rand = new Random();

    private int score = 0, lives = 3;
    private boolean gameOver = false;

    private boolean swipeActive = false;
    private List<Point> swipePoints = new ArrayList<>();
    private int comboCount = 0;
    private String comboMsg = "";
    private int comboTimer = 0;

    private boolean slowMo = false;
    private int slowMoTimer = 0;

    // 新增資源欄位
    private BufferedImage bgImage;
    private BufferedImage[] fruitSprites;
    private BufferedImage[][] halfFruitSprites;
    private BufferedImage bombSprite;
    private BufferedImage[] explosionFrames;
    private Clip sliceSfx, bombSfx, explosionSfx;

    private JButton restartBtn, exitBtn;
    private int spawnInterval = 100, spawnCounter = 0, nextThreshold = 50;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        addMouseListener(this);
        addMouseMotionListener(this);

        // Preload assets
        try {
            bgImage = ImageIO.read(getClass().getResource("/assets/background/woodBackground.png"));
            String[] types = {"apple","orange","banana"};
            fruitSprites      = new BufferedImage[types.length];
            halfFruitSprites  = new BufferedImage[types.length][2];
            for(int i=0; i<types.length; i++){
                fruitSprites[i]      = ImageIO.read(getClass().getResource("/assets/fruit/"       + types[i] + ".png"));
                halfFruitSprites[i][0] = ImageIO.read(getClass().getResource("/assets/half-fruit/" + types[i] + "_left.png"));
                halfFruitSprites[i][1] = ImageIO.read(getClass().getResource("/assets/half-fruit/" + types[i] + "_right.png"));
            }
            bombSprite = ImageIO.read(getClass().getResource("/assets/bomb/bomb.png"));
            URL expDirUrl = getClass().getResource("/assets/explosion");
            File expDir = new File(expDirUrl.toURI());
            File[] frames = expDir.listFiles();
            explosionFrames = new BufferedImage[frames.length];
            for(int i=0; i<frames.length; i++){
                explosionFrames[i] = ImageIO.read(frames[i]);
            }
            sliceSfx     = AudioSystem.getClip();
            sliceSfx.open(AudioSystem.getAudioInputStream(getClass().getResource("/assets/sound/slice.wav")));
            bombSfx      = AudioSystem.getClip();
            bombSfx.open(AudioSystem.getAudioInputStream(getClass().getResource("/assets/sound/bomb.wav")));
            explosionSfx = AudioSystem.getClip();
            explosionSfx.open(AudioSystem.getAudioInputStream(getClass().getResource("/assets/sound/explosion.wav")));
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Game Over buttons
        restartBtn = new JButton("Restart");
        exitBtn    = new JButton("Exit");
        restartBtn.setBounds(350, 260, 100, 30);
        exitBtn.setBounds(350, 300, 100, 30);
        restartBtn.setVisible(false);
        exitBtn.setVisible(false);
        add(restartBtn); add(exitBtn);
        restartBtn.addActionListener(e -> restart());
        exitBtn.addActionListener(e -> System.exit(0));

        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!gameOver) {
            spawnCounter++;
            if(spawnCounter >= spawnInterval) {
                spawn();
                spawnCounter = 0;
            }
            double speed = slowMo ? 0.5 : 1.0;
            Iterator<GameObject> it = objects.iterator();
            while(it.hasNext()) {
                GameObject obj = it.next();
                obj.update(speed);
                if(obj.isOffScreen(WIDTH, HEIGHT)) {
                    if(obj instanceof Fruit && !obj.isSliced()) loseLife();
                    it.remove();
                }
            }
            if(slowMo && --slowMoTimer <= 0) slowMo = false;
            if(comboTimer-- <= 0) comboMsg = "";
            if(score >= nextThreshold) {
                spawnInterval = Math.max(20, spawnInterval - 10);
                nextThreshold += 50;
            }
        }
        repaint();
    }

    private void spawn() {
        int x = rand.nextInt(WIDTH - 100) + 50;
        int y = HEIGHT + 10;
        double vy = -(rand.nextDouble()*5 + 15);
        double vx = rand.nextDouble()*6 - 3;
        double r = rand.nextDouble();
        if(r < 0.70) {
            int idx = rand.nextInt(fruitSprites.length);
            objects.add(new Fruit(x,y,vx,vy, idx));
        } else if(r < 0.85) {
            objects.add(new Bomb(x,y,vx,vy));
        } else {
            objects.add(new BonusItem(x,y,vx,vy));
        }
    }

    private void loseLife() {
        if(--lives <= 0) endGame();
    }

    private void endGame() {
        gameOver = true;
        timer.stop();
        restartBtn.setVisible(true);
        exitBtn.setVisible(true);
    }

    private void restart() {
        score = 0; lives = 3; objects.clear();
        gameOver = false; spawnInterval = 100; nextThreshold = 50;
        restartBtn.setVisible(false); exitBtn.setVisible(false);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_QUALITY);

        // draw background
        g2.drawImage(bgImage, 0, 0, WIDTH, HEIGHT, null);

        // draw objects
        for(GameObject obj : objects) {
            int cx = (int)obj.x, cy = (int)obj.y, r = obj.radius;
            // shadow
            g2.setColor(new Color(0,0,0,50));
            g2.fillOval(cx-r/2, cy-r/2+5, r*2, r);

            if(!obj.isSliced()) {
                if(obj instanceof Fruit) {
                    int idx = ((Fruit)obj).getTypeIndex();
                    g2.drawImage(fruitSprites[idx], cx-r, cy-r, r*2, r*2, null);
                } else if(obj instanceof Bomb) {
                    g2.drawImage(bombSprite, cx-r, cy-r, r*2, r*2, null);
                }
            } else {
                if(obj instanceof Fruit) {
                    int idx = ((Fruit)obj).getTypeIndex();
                    g2.drawImage(halfFruitSprites[idx][0], cx-r, cy-r, r, r*2, null);
                    g2.drawImage(halfFruitSprites[idx][1], cx,   cy-r, r, r*2, null);
                    score += ((Fruit)obj).getPointValue(); // ensure scoring
                } else if(obj instanceof Bomb) {
                    Bomb b = (Bomb)obj;
                    int f = b.getFrameCounter() % explosionFrames.length;
                    g2.drawImage(explosionFrames[f], cx-r, cy-r, r*2, r*2, null);
                    b.incrementFrameCounter();
                } else if(obj instanceof BonusItem) {
                    BonusItem bi = (BonusItem)obj;
                    // draw nothing here; effects handled in mouseDragged
                }
            }
        }

        // play combo/bonus text
        if(!comboMsg.isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 24));
            int w = g2.getFontMetrics().stringWidth(comboMsg);
            g2.setColor(Color.WHITE);
            g2.drawString(comboMsg, (WIDTH - w)/2, 50);
        }

        // UI overlay: score & lives
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score, 10, 20);
        g2.drawString("Lives:", 10, 40);
        for(int i=0; i<lives; i++){
            g2.setColor(Color.RED);
            g2.drawString("\u2665", 60 + i*15, 40);
        }

        // game over overlay
        if(gameOver) {
            g2.setColor(new Color(0,0,0,150));
            g2.fillRect(0, 0, WIDTH, HEIGHT);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 36));
            g2.drawString("Game Over", WIDTH/2 - 100, HEIGHT/2 - 60);
            restartBtn.setVisible(true);
            exitBtn.setVisible(true);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        swipeActive = true;
        swipePoints.clear();
        comboCount = 0;
        swipePoints.add(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        swipeActive = false;
        swipePoints.clear();
        if(comboCount >= 3) {
            int bonus = comboCount;
            score += bonus;
            comboMsg = comboCount + " Combo! +" + bonus;
            comboTimer = 60;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(!swipeActive) return;
        Point prev = swipePoints.get(swipePoints.size()-1);
        Point cur  = e.getPoint();
        swipePoints.add(cur);

        Iterator<GameObject> it = objects.iterator();
        while(it.hasNext()) {
            GameObject obj = it.next();
            if(!obj.isSliced() && obj.intersectsLine(prev.x, prev.y, cur.x, cur.y)) {
                obj.setSliced(true);
                if(obj instanceof Fruit) {
                    sliceSfx.stop(); sliceSfx.setFramePosition(0); sliceSfx.start();
                    comboCount++;
                } else if(obj instanceof Bomb) {
                    bombSfx.stop(); bombSfx.setFramePosition(0); bombSfx.start();
                    explosionSfx.stop(); explosionSfx.setFramePosition(0); explosionSfx.start();
                    loseLife();
                } else if(obj instanceof BonusItem) {
                    BonusItem bi = (BonusItem)obj;
                    if(bi.getBonusType() == BonusItem.BonusType.EXTRA_LIFE && lives<5){
                        lives++;
                        comboMsg = "+1 Life!";
                    } else if(bi.getBonusType() == BonusItem.BonusType.SLOW_MOTION){
                        slowMo = true; slowMoTimer = 150;
                        comboMsg = "Slow Motion!";
                    }
                    comboTimer = 60;
                }
                it.remove();
                break; // only slice one per segment
            }
        }
    }

    // unused MouseListener stubs
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
