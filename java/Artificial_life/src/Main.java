import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Field field = new Field(10, 10, 4, 5, 6);
        field.create_wolf();
        field.create_grass();
        field.create_rabbit();

        JFrame frame = new JFrame("Field Visualization");
        FieldVisualizer visualizer = new FieldVisualizer(field.getFieldArray());
        frame.add(visualizer);
        frame.setSize(900, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);



        while (true) {
            field.update(); // Обновляем состояние поля
            visualizer.repaint(); // Перерисовываем визуализацию
            double randomValue = (Math.random() * 2) + (Math.random() < 0.5 ? -1 : 1);
            System.out.println((int) randomValue);
            try {
                Thread.sleep(1000); // Задержка для управления скоростью обновления
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
