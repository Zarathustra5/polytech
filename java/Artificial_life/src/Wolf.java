public class Wolf {
    private int energy;
    private static final int REPRODUCTION_ENERGY = 50; // Энергия для размножения
    private int x;
    private int y;
    private int maxEnergy;
    private int minEnergy;

    public Wolf(int maxEnergy, int energy, int x, int y, int minEnergy) {
        this.maxEnergy = maxEnergy;
        this.energy = energy;
        this.x = x;
        this.y = y;
        this.minEnergy = minEnergy;
    }
    public static int randomVaule() {
        double randomValue;
        do {
            randomValue = (Math.random() * 2) - 1; // Генерируем значение от -1 до 1
        } while (randomValue == 0); // Исключаем 0
        return (int) randomValue; // Приводим к int
    }


    public void move(Field field) {
        // Проверяем соседние клетки на наличие кроликов
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (Math.abs(dx) + Math.abs(dy) == 1) { // Только соседние клетки
                    int newX = x + dx;
                    int newY = y + dy;
                    if (field.isValidPosition(newX, newY) && field.getFieldArray()[newX][newY] instanceof Rabbit) {
                        eat((Rabbit) field.getFieldArray()[newX][newY]); // Съедаем кролика
                        field.getFieldArray()[newX][newY] = null; // Удаляем кролика с поля
                        return; // Завершаем движение, так как мы съели
                    }
                }
            }
        }

        // Если кроликов нет, двигаемся случайным образом
        int newX = x + (int) (Math.random() * 3) - 1;
        int newY = y + (int) (Math.random() * 3) - 1;

        if (field.isValidPosition(newX, newY)) {
            this.x = newX;
            this.y = newY;
        }
    }


    public void eat(Rabbit rabbit) {
        this.energy += rabbit.getEnergy();
    }

    public void updateEnergy(Field field) {
        energy-=5;
        if (energy < minEnergy) {
            field.getFieldArray()[x][y] = null; // Удаляем волка с поля
        }
    }


    public void reproduce(Field field) {
        if (energy >= REPRODUCTION_ENERGY) {
            // Найти пустую клетку рядом для размножения
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (Math.abs(dx) + Math.abs(dy) == 1) { // Только соседние клетки
                        int newX = x + dx;
                        int newY = y + dy;
                        if (field.isValidPosition(newX, newY) && field.getFieldArray()[newX][newY] == null) {
                            field.getFieldArray()[newX][newY] = new Wolf(100, 50, newX, newY, 0);
                            energy -= REPRODUCTION_ENERGY; // Уменьшаем энергию после размножения
                            return;
                        }
                    }
                }
            }
        }
    }
}