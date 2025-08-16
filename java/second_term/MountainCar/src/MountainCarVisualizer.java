import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

public class MountainCarVisualizer extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final double MIN_POSITION = -1.2;
    private static final double MAX_POSITION = 0.6;
    private static final double GOAL_POSITION = 0.5;

    private double carPosition;
    private double carVelocity;
    private int action;

    public MountainCarVisualizer() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        carPosition = -0.5;
        carVelocity = 0.0;
        action = 1; // Нейтральное действие
    }

    public void updateState(double position, double velocity, int action) {
        this.carPosition = position;
        this.carVelocity = velocity;
        this.action = action;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рисуем горный ландшафт
        drawMountain(g2d);

        // Рисуем машину
        drawCar(g2d);

        // Рисуем информацию о состоянии
        drawStateInfo(g2d);
    }

    private void drawMountain(Graphics2D g2d) {
        // Преобразование координат
        int baseY = HEIGHT - 100;

        // Создаем путь для горы
        GeneralPath mountainPath = new GeneralPath();
        mountainPath.moveTo(0, baseY);

        // Рисуем горный ландшафт с помощью синусоидальной функции
        for (int x = 0; x <= WIDTH; x++) {
            double position = MIN_POSITION + (MAX_POSITION - MIN_POSITION) * x / WIDTH;
            double height = Math.sin(3 * position);
            int y = baseY - (int)(height * 100);
            mountainPath.lineTo(x, y);
        }

        // Замыкаем путь
        mountainPath.lineTo(WIDTH, HEIGHT);
        mountainPath.lineTo(0, HEIGHT);
        mountainPath.closePath();

        // Заливка горы
        g2d.setColor(new Color(139, 69, 19)); // Коричневый цвет для горы
        g2d.fill(mountainPath);

        // Контур горы
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(mountainPath);

        // Рисуем цель
        int goalX = positionToScreenX(GOAL_POSITION);
        double height = Math.sin(3 * GOAL_POSITION);
        int goalY = baseY - (int)(height * 100);

        g2d.setColor(Color.GREEN);
        g2d.fillOval(goalX - 10, goalY - 10, 20, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(goalX - 10, goalY - 10, 20, 20);
        g2d.drawString("Цель", goalX - 15, goalY - 15);
    }

    private void drawCar(Graphics2D g2d) {
        int x = positionToScreenX(carPosition);
        double height = Math.sin(3 * carPosition);
        int y = (HEIGHT - 100) - (int)(height * 100);

        // Рисуем корпус машины
        g2d.setColor(Color.RED);
        g2d.fillRect(x - 20, y - 10, 40, 20);

        // Рисуем колеса
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x - 15, y + 5, 15, 15);
        g2d.fillOval(x + 5, y + 5, 15, 15);

        // Рисуем индикатор действия (направление движения)
        g2d.setColor(Color.BLUE);
        int arrowLength = 30;
        if (action == 0) { // Назад
            g2d.drawLine(x, y - 5, x - arrowLength, y - 5);
            g2d.drawLine(x - arrowLength, y - 5, x - arrowLength + 10, y - 10);
            g2d.drawLine(x - arrowLength, y - 5, x - arrowLength + 10, y);
        } else if (action == 2) { // Вперед
            g2d.drawLine(x, y - 5, x + arrowLength, y - 5);
            g2d.drawLine(x + arrowLength, y - 5, x + arrowLength - 10, y - 10);
            g2d.drawLine(x + arrowLength, y - 5, x + arrowLength - 10, y);
        }
    }

    private void drawStateInfo(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));

        String actionName;
        switch (action) {
            case 0: actionName = "Назад (-1)"; break;
            case 2: actionName = "Вперед (+1)"; break;
            default: actionName = "Нейтрально (0)"; break;
        }

        g2d.drawString("Позиция: " + String.format("%.4f", carPosition), 20, 30);
        g2d.drawString("Скорость: " + String.format("%.4f", carVelocity), 20, 50);
        g2d.drawString("Действие: " + actionName, 20, 70);
    }

    private int positionToScreenX(double position) {
        return (int)((position - MIN_POSITION) / (MAX_POSITION - MIN_POSITION) * WIDTH);
    }

    public static JFrame createFrame() {
        JFrame frame = new JFrame("Задача о машине на горе");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MountainCarVisualizer visualizer = new MountainCarVisualizer();
        frame.add(visualizer);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

    public static MountainCarVisualizer createVisualizer() {
        JFrame frame = new JFrame("Задача о машине на горе");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MountainCarVisualizer visualizer = new MountainCarVisualizer();
        frame.add(visualizer);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return visualizer;
    }
}
