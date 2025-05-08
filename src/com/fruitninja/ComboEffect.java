package com.fruitninja;
// ComboEffect.java: handles displaying and animating the combo pop‑up effect when multiple fruits are sliced in quick succession

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class ComboEffect extends JComponent {
    private Image comboImg;
    private float glowRadius = 0f;
    private float glowAlpha  = 0f;
    private float scale      = 1f;
    private boolean expanding = true;

    public ComboEffect (Image comboImg) {
        this.comboImg = comboImg;
        Timer timer = new Timer(16, e -> {
            // 0～20 
            if (expanding) {
                glowRadius += 0.5f;
                glowAlpha  += 0.015f;
                scale      += 0.0008f;
                if (glowRadius >= 20f) expanding = false;
            } else {
                glowRadius -= 0.5f;
                glowAlpha  -= 0.015f;
                scale      -= 0.0008f;
                if (glowRadius <= 0f) expanding = true;
            }
            // clamp alpha & scale
            glowAlpha = Math.max(0f, Math.min(0.6f, glowAlpha));
            scale     = Math.max(1f, Math.min(1.05f, scale));
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int imgW = comboImg.getWidth(this);
        int imgH = comboImg.getHeight(this);
        int cx = getWidth()/2  - (int)(imgW*scale)/2;
        int cy = getHeight()/2 - (int)(imgH*scale)/2;

        // 1. light
        Point2D center = new Point2D.Float(getWidth()/2f, getHeight()/2f);
        float radius = glowRadius;
        float[] fractions = { 0f, 1f };
        Color[] colors = {
            new Color(255, 215, 0, (int)(glowAlpha*255)),  // gold
            new Color(255, 215, 0, 0)                      // fully transparent
        };
        RadialGradientPaint paint = new RadialGradientPaint(center, radius, fractions, colors);
        g2.setPaint(paint);
        g2.fill(new Ellipse2D.Float(
            getWidth()/2f - radius, getHeight()/2f - radius, 
            radius * 2, radius * 2
        ));

        g2.translate(cx, cy);
        g2.scale(scale, scale);
        g2.drawImage(comboImg, 0, 0, this);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }

    // test Frame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Image img = new ImageIcon("combo.png").getImage();
            JFrame frame = new JFrame("Combo Glow Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ComboEffect (img));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
