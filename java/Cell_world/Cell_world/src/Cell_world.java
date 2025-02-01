import javax.swing.*;
import java.awt.*;

public class Cell_world extends JFrame {
    private int windowWidth = 400;
    private int windowHeight = 400;
    public Thread thr;

    public Cell_world() {
        super("Муравьи");
        setSize(windowWidth, windowHeight);
        setLayout(new CardLayout());
        Graph graph = new Graph(windowWidth, windowHeight);
        graph.createGraph();
        graph.createAnts();

        thr = new Thread(graph);
        this.add(graph);
        thr.start();

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
        Cell_world cell_world = new Cell_world();
    }
}
