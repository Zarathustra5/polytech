import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class Ants extends JFrame {
    private int width = 800;
    private int height = 800;
    private int circleX;
    private int circleY;

    public Ants() {
        super("Окно игры");
        setSize(width, height);
        getContentPane().setBackground(Color.PINK);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                circleX = e.getX();
                circleY = e.getY();
                repaint();
            }
        });
    }

    public void paint(Graphics g) {
        g.fillRect(0, 0, width, height);
        g.setColor(Color.PINK);
        g.drawOval(circleX - 25, circleY - 25, 50, 50);
        for (int i = 0; i <= width; i += 80) {
            g.drawLine(0, i, width, height - i);
            g.drawLine(i, 0, width - i, height);
            System.out.println(i);
        }
    }
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Ants ants = new Ants();
    }
}