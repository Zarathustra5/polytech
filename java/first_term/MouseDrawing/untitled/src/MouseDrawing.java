import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class MouseDrawing extends JFrame {
    private int width = 800;
    private int height = 800;
    private int circleX;
    private int circleY;

    public MouseDrawing() {
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
        width = getWidth();
        height = getHeight();
        g.fillRect(0, 0, width, height);
        g.setColor(Color.PINK);
        g.drawOval(circleX - 25, circleY - 25, 50, 50);
        for (int i = 0; i <= width; i += 50) {
            for (int j = 0; j <= height; j += 50) {
                if (i == 0 || j == 0) g.drawLine(i, j, width - i, height - j);
            }
        }
    }
    public static void main(String[] args) {
        System.out.println("Hello world!");
        MouseDrawing mouseDrawing = new MouseDrawing();
    }
}