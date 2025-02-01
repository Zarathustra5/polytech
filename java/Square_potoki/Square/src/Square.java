import java.awt.*;

public class Square extends Canvas implements Runnable {
    private int cord_x;
    private int cord_y;
    private int color_red;
    private int color_green;
    private int color_blue;
    private int square_size;
    private boolean movingRight = true;

    public Square(int cord_x, int cord_y, int color_red, int color_green, int color_blue, int square_size)
    {
        this.cord_x = cord_x;
        this.cord_y = cord_y;
        this.color_red = color_red;
        this.color_green = color_green;
        this.color_blue = color_blue;
        this.square_size = square_size;
    }
    public void move() {
        if (movingRight == true) {
            cord_x += 10;
            if (600 <= cord_x) {
                movingRight = false;
            }
        } else {
            cord_x -= 10;
            if (0 >= cord_x) {
                movingRight = true;
            }
        }
    }

    public void paint(Graphics g) {
        g.setColor(new Color(color_red, color_green, color_blue));
        g.fillRect(cord_x, cord_y, square_size, square_size);
    }
    @Override
    public void run() {
        while (true) {
            repaint();
            try{
                Thread.currentThread().sleep(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            move();
        }
    }
}
