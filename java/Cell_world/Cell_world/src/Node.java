import java.awt.*;

public class Node {
    final int xIndex;
    final int yIndex;
    final int x;
    final int y;

    public Node(int xIndex, int yIndex, int x, int y) {
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        this.x = x;
        this.y = y;
    }

    public void drawNode(Graphics g, boolean isHome, boolean isFood) {
        g.setColor(Color.blue);
        if (isHome) {
            g.setColor(Color.red);
        } else if (isFood) {
            g.setColor(Color.orange);
        }
        g.fillOval(x, y, 20, 20);
    }
}
