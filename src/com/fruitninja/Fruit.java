package com.fruitninja;
//Fruit.java: represents a whole fruit, handles image and split into halves

import java.awt.Image;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.Objects;
import java.util.HashMap;
import java.io.File;
import javax.swing.ImageIcon;

public class Fruit extends GameObject {
    private static final List<Image> FRUITS = new ArrayList<>();
    private static final Map<String, List<Image>> HALVES = new HashMap<>();
    private static final Map<Image, String> NAMES = new HashMap<>();
    private static final Random RAND = new Random();

    static {
        File dir = new File(Constants.IMG_FRUIT_PATH);
        File halfDir = new File(Constants.IMG_HALF_FRUIT_PATH);
        for (File f : Objects.requireNonNull(dir.listFiles((d, name) -> name.endsWith(".png")))) {
            Image img = new ImageIcon(f.getPath()).getImage();
            String name = f.getName().replace(".png", "");
            FRUITS.add(img);
            NAMES.put(img, name);
         
            List<Image> halfList = new ArrayList<>();
            for (File hf : Objects.requireNonNull(halfDir.listFiles((d, fname) ->
                    fname.startsWith(name) && fname.endsWith(".png")))) {
                halfList.add(new ImageIcon(hf.getPath()).getImage());
            }
            HALVES.put(name, halfList);
        }
    }

    private final Image image;
    private final int baseRadius;

    public Fruit(double x, double y, double vx, double vy) {
        super(x, y, vx, vy);
        this.image = FRUITS.get(RAND.nextInt(FRUITS.size()));
        this.baseRadius = image.getWidth(null) / 2;
    }

    public static String getName(Image img) {
        return NAMES.get(img);
    }

    public static List<Image> getHalfImages(String fruitName) {
        return HALVES.getOrDefault(fruitName, List.of());
    }

    @Override
    public int getRadius() {
        return baseRadius;
    }

    @Override
    protected Image getImage() {
        return image;
    }

    @Override
    public double getScaleFactor() {
        return Constants.FRUIT_SCALE_FACTOR;
    }
}
