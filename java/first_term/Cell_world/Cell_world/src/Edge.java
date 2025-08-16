import java.awt.*;
import java.util.Random;

public class Edge {
    final Node nodeA;
    final Node nodeB;
    final int distance;
    public int feromon = 0;
    final String edgeType;

    public Edge(Node nodeA, Node nodeB, String edgeType) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        Random rand = new Random();
        this.distance = rand.nextInt(6);
        this.edgeType = edgeType;
    }

    public void drawEdge(Graphics g) {
        g.setColor(Color.red);
        g.drawLine(nodeA.x + 10, nodeA.y + 10, nodeB.x + 10, nodeB.y + 10);
    }
}