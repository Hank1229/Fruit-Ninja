package com.fruitninja;
//FruitNinjaGame.java: main application launcher, manages UI panels and controls

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;

public class FruitNinjaGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fruit Ninja Java");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
            frame.setResizable(false);
            
            Clip gameStartClip = loadClip(Constants.SOUND_BASE_PATH + "gameRoundSounds/gameStart.wav");
            Clip gameOverClip  = loadClip(Constants.SOUND_BASE_PATH + "gameRoundSounds/gameOver.wav");
            Clip swordClip     = loadClip(Constants.SOUND_BASE_PATH + "gameRoundSounds/swordSwipe1.wav");
            if (gameStartClip != null) gameStartClip.loop(Clip.LOOP_CONTINUOUSLY);

            // gamePanel
            GamePanel gp = new GamePanel();
            gp.setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
            gp.setVisible(false); 
            frame.getLayeredPane().add(gp, JLayeredPane.DEFAULT_LAYER);
            //startPanel
            JPanel startPanel = new JPanel() {
                private Image bg = new ImageIcon("resource/background/startPanel.png").getImage();
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // full screen
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
                }
            };
            startPanel.setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
            startPanel.setLayout(null);

            // START 
            JButton startBtn = new JButton("START");
            startBtn.setFont(new Font("Arial", Font.BOLD, 36));
            startBtn.setBackground(Color.WHITE);
            startBtn.setOpaque(true);
            startBtn.setContentAreaFilled(true);
            startBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            int btnW = 200, btnH = 80;
            // Under title
            startBtn.setBounds((Constants.SCREEN_WIDTH - btnW) / 2,
                               Constants.SCREEN_HEIGHT - btnH - 60,
                               btnW, btnH);
            // Hover
            startBtn.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { startBtn.setBackground(Color.GREEN); }
                @Override public void mouseExited(MouseEvent e)  { startBtn.setBackground(Color.WHITE); }
            });
            startPanel.add(startBtn);
            frame.getLayeredPane().add(startPanel, JLayeredPane.PALETTE_LAYER);

            // PAUSE 
            JButton pause = new JButton("⏸");
            pause.setFont(new Font("Arial", Font.BOLD, 18));
            pause.setOpaque(true);
            pause.setContentAreaFilled(true);
            pause.setBackground(Color.WHITE);
            pause.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            int ps = 50;
            pause.setBounds(Constants.SCREEN_WIDTH - ps - 20, 10, ps, ps);
            pause.setVisible(false);
            frame.getLayeredPane().add(pause, JLayeredPane.PALETTE_LAYER);

            // Overlay 
            JPanel overlay = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(0,0,0,150));
                    g2.fillRect(0,0,getWidth(),getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            overlay.setOpaque(false);
            overlay.setBounds(0,0,Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
            overlay.setLayout(null);
            overlay.setVisible(false);
            frame.getLayeredPane().add(overlay, JLayeredPane.MODAL_LAYER);

            // Pause menu
            JPanel pauseMenu = new JPanel();
            pauseMenu.setOpaque(false);
            pauseMenu.setLayout(new BoxLayout(pauseMenu, BoxLayout.Y_AXIS));
            pauseMenu.setBounds((Constants.SCREEN_WIDTH-200)/2, (Constants.SCREEN_HEIGHT-200)/2 - 50, 200, 200);
            pauseMenu.setVisible(false);
            JButton resume = new JButton("RESUME");
            JButton restart = new JButton("RESTART");
            JButton exitP = new JButton("EXIT");
            for (JButton b : new JButton[]{resume, restart, exitP}) {
                b.setFont(new Font("Arial", Font.PLAIN, 18));
                b.setOpaque(true);
                b.setContentAreaFilled(true);
                b.setBackground(Color.WHITE);
                b.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                b.setAlignmentX(Component.CENTER_ALIGNMENT);
                b.setMaximumSize(new Dimension(180, 40));
                b.addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { b.setBackground(Color.GREEN); }
                    @Override public void mouseExited(MouseEvent e)  { b.setBackground(Color.WHITE); }
                });
                pauseMenu.add(Box.createVerticalStrut(20));
                pauseMenu.add(b);
            }
            overlay.add(pauseMenu);

            // Game Over 
            JPanel gameOverMenu = new JPanel();
            gameOverMenu.setOpaque(false);
            gameOverMenu.setLayout(new BoxLayout(gameOverMenu, BoxLayout.Y_AXIS));
            gameOverMenu.setBounds((Constants.SCREEN_WIDTH-400)/2, (Constants.SCREEN_HEIGHT-300)/2 - 30, 400, 300);
            gameOverMenu.setVisible(false);
            JLabel goLabel = new JLabel("GAME OVER");
            goLabel.setFont(new Font("Arial", Font.BOLD, 48));
            goLabel.setForeground(Color.RED);
            goLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel scoreLabel = new JLabel("Your Score: 0");
            scoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
            scoreLabel.setForeground(Color.WHITE);
            scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JButton playAgain = new JButton("PLAY AGAIN");
            JButton gameExit = new JButton("EXIT");
            for (JButton b : new JButton[]{playAgain, gameExit}) {
                b.setFont(new Font("Arial", Font.PLAIN, 18));
                b.setOpaque(true);
                b.setContentAreaFilled(true);
                b.setBackground(Color.WHITE);
                b.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                b.setAlignmentX(Component.CENTER_ALIGNMENT);
                b.setMaximumSize(new Dimension(180, 40));
                b.addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { b.setForeground(Color.RED); }
                    @Override public void mouseExited(MouseEvent e)  { b.setForeground(Color.BLACK); }
                });
            }
            gameOverMenu.add(Box.createVerticalStrut(20));
            gameOverMenu.add(goLabel);
            gameOverMenu.add(Box.createVerticalStrut(10));
            gameOverMenu.add(scoreLabel);
            gameOverMenu.add(Box.createVerticalStrut(20));
            gameOverMenu.add(playAgain);
            gameOverMenu.add(Box.createVerticalStrut(10));
            gameOverMenu.add(gameExit);
            overlay.add(gameOverMenu);

    

         // START Btn
            startBtn.addActionListener(e -> {
                if (gameStartClip != null) gameStartClip.stop();
                // Sword Snd
                if (swordClip != null) {
                    swordClip.setFramePosition(0);
                    swordClip.start();
                }
                // Switch to game window
                startPanel.setVisible(false);
                gp.setVisible(true);
                pause.setVisible(true);
                overlay.setVisible(false);
                gp.startGame();

                // 延遲 500ms 播放背景音樂，並設定為無限迴圈
                new javax.swing.Timer(10000, ev -> {
                    if (gameStartClip != null) {
                        gameStartClip.setFramePosition(0);
                    }
                }) {{
                    setRepeats(false);
                }}.start();
            });


         // PAUSE Btn
         pause.addActionListener(e -> {
             gp.pauseGame();
             pause.setVisible(false);
             pauseMenu.setVisible(true);
             overlay.setVisible(true);
         });

         // RESUME Btn
         resume.addActionListener(e -> {
             gp.resumeGame();
             overlay.setVisible(false);
             pause.setVisible(true);
         });

         // RESTART Btn
         restart.addActionListener(e -> {
             gp.restartGame();
             overlay.setVisible(false);
             pauseMenu.setVisible(false);
             pause.setVisible(true);
         });

         // EXIT Btn
         exitP.addActionListener(e -> {
             System.exit(0);
         });

         // PLAY AGAIN Btn
         playAgain.addActionListener(e -> {
             gp.startGame();
             overlay.setVisible(false);
             gameOverMenu.setVisible(false);
             pauseMenu.setVisible(false);
             pause.setVisible(true);
         });

         // EXIT Btn
         gameExit.addActionListener(e -> {
             System.exit(0);
         });

         gp.setOnGameOver(() -> {
        	 if (gameOverClip != null) { gameOverClip.setFramePosition(0); gameOverClip.start(); }

             overlay.setVisible(true);
             pauseMenu.setVisible(false);
             gameOverMenu.setVisible(true);
             pause.setVisible(false);
             // startPanel 
             scoreLabel.setText("Your Score: " + gp.getScore());
         });

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static Clip loadClip(String path) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
