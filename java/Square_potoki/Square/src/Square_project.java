import javax.swing.*;
import java.awt.*;

public class Square_project extends Frame {
    public Square[] sq;
    public Thread[] thr;

    public Square_project(String str) {
        super(str);
        this.setSize(600, 600);
        this.setLayout(new GridLayout(10, 1));
        sq = new Square[10];
        thr = new Thread[10];
        for (int i = 0; i < 10; i++) {
            sq[i] = new Square(1, 1, (int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255), 50);
            thr[i] = new Thread(sq[i]);
            this.add(sq[i]);
            thr[i].start();
        }
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setDefaultCloseOperation(int exitOnClose) {
    }

    public static void main(String[] args) {
        Square_project sq_prj = new Square_project("MyFavProject");
    }
}