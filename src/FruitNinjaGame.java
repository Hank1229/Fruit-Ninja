import javax.swing.JFrame;

public class FruitNinjaGame {
    public static int highScore = 0;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fruit Ninja");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
