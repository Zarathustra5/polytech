import java.util.HashSet;
import java.util.Set;

public class Field {
    private int m;
    private int n;
    private int x;
    private int y;
    private int n_wolf;
    private int n_rabbits;
    private int n_grass;
    private Object[][] s;

    public Field(int m, int n, int n_wolf, int n_rabbits, int n_grass) {
        this.m = m;
        this.n = n;
        this.n_wolf = n_wolf;
        this.n_rabbits = n_rabbits;
        this.n_grass = n_grass;
        this.s = new Object[n][m];


        for(int i = 0; i < n; i++) {
            for(int j = 0; j < m; j++) {
                s[i][j] = null;
            }
        }

    }
    public Object[][] getFieldArray() {
        return this.s;
    }
    private void create_random_objects(Class<?> clazz, int count) {
        Set<String> occupiedPositions = new HashSet<>(); // Множество для отслеживания занятых позиций

        for (int i = 0; i < count; ) {
            int x = (int) (Math.random() * this.n);
            int y = (int) (Math.random() * this.m);
            String position = x + "," + y; // Создаем строку для представления позиции

            // Проверяем, занята ли позиция
            if (!occupiedPositions.contains(position)) {
                try {
                    if (clazz == Wolf.class) {
                        s[x][y] = new Wolf(100, 50, x, y, 0);
                    } else if (clazz == Rabbit.class) {
                        s[x][y] = new Rabbit(20, 16, x, y, 0);
                    } else if (clazz == Grass.class) {
                        s[x][y] = new Grass(5, x, y); // Пример энергии для травы
                    }
                    occupiedPositions.add(position); // Добавляем позицию в множество
                    i++; // Увеличиваем счетчик только если добавили объект
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void create_wolf() {
        create_random_objects(Wolf.class, this.n_wolf);
    }

    public void create_rabbit() {
        create_random_objects(Rabbit.class, this.n_rabbits);
    }

    public void create_grass() {
        create_random_objects(Grass.class, this.n_grass);
    }

    public void print() {
        for (int i = 0; i < this.n; ++i) {
            for (int j = 0; j < this.m; ++j) {
                if (s[i][j] == null) {
                    System.out.print("."); // Выводим точку для пустой ячейки
                } else if (s[i][j] instanceof Wolf) {
                    System.out.print("w"); // Выводим 'w' для волка
                } else if (s[i][j] instanceof Rabbit) {
                    System.out.print("r"); // Выводим 'r' для кролика
                } else if (s[i][j] instanceof Grass) {
                    System.out.print("g"); // Выводим 'g' для травы
                }
            }
            System.out.println(); // Переход на новую строку после каждой строки
        }
    }
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < n && y >= 0 && y < m;
    }

    public void update() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (s[i][j] instanceof Rabbit) {
                    Rabbit rabbit = (Rabbit) s[i][j];
                    rabbit.move(this);
                    rabbit.reproduce(this);
                    rabbit.updateEnergy(this);
                } else if (s[i][j] instanceof Wolf) {
                    Wolf wolf = (Wolf) s[i][j];
                    wolf.move(this);
                    wolf.reproduce(this);
                    wolf.updateEnergy(this);
                } else if (s[i][j] instanceof Grass) {
                    Grass grass = (Grass) s[i][j];
                    grass.regenerate();
                }
            }
        }
    }

}