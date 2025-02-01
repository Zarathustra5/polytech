import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FieldVisualizer extends JPanel {
    private Object[][] field;
    private int cellSize = 80; // Размер ячейки в пикселях

    // Хранение изображений
    private Map<Class<?>, Image> images = new HashMap<>();

    public FieldVisualizer(Object[][] field) {
        this.field = field;
        loadImages();
    }

    private void loadImages() {
        // Загрузка изображений
        String imagePath = "/home/user/coding/java/Artificial_life/src/resources/";
        images.put(Wolf.class, new ImageIcon(imagePath + "Wolf.png").getImage());
        images.put(Rabbit.class, new ImageIcon(imagePath + "Rabbit.png").getImage());
        images.put(Grass.class, new ImageIcon(imagePath + "Grass.png").getImage());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] != null) {
                    Image img = images.get(field[i][j].getClass());
                    if (img != null) {
                        g.drawImage(img, j * cellSize, i * cellSize, cellSize, cellSize, null);
                    }
                }
            }
        }
    }
}