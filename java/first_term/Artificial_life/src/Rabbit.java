public class Rabbit {
    private int maxEnergy;
    private static final int REPRODUCTION_ENERGY = 15; // Энергия для размножения
    private int energy;
    private int x;
    private int y;
    private int minEnergy;

    public Rabbit(int maxEnergy, int energy, int x, int y, int minEnergy) {
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
        // Проверяем соседние клетки на наличие волков
        boolean isWolfNearby = false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (Math.abs(dx) + Math.abs(dy) == 1) { // Только соседние клетки
                    int newX = x + dx;
                    int newY = y + dy;
                    if (field.isValidPosition(newX, newY)) {
                        if (field.getFieldArray()[newX][newY] instanceof Wolf) {
                            isWolfNearby = true;
                            break;
                        }
                        if (field.getFieldArray()[newX][newY] instanceof Grass) {
                            // Если кролик нашел траву, поедаем её
                            eat((Grass) field.getFieldArray()[newX][newY]);
                        }
                    }
                }
            }
            if (isWolfNearby) break;
        }

        // Если волк рядом, движемся в противоположном направлении
        int newX, newY;
        if (isWolfNearby) {
            do {
                newX = x +(int) (Math.random() * 3) - 1;
                newY = y + (int) (Math.random() * 3) - 1;
            } while (!field.isValidPosition(newX, newY) || (newX == x && newY == y));
        } else {
            // Обычное движение
            do {
                newX = x + (int) (Math.random() * 3) - 1;
                newY = y + (int) (Math.random() * 3) - 1;
            } while (!field.isValidPosition(newX, newY) || (newX == x && newY == y));
        }

        // Обновляем координаты
        if (field.isValidPosition(newX, newY)) {
            this.x = newX;
            this.y = newY;
        }

        // Проверяем, есть ли трава на новом месте, и поедаем её
        if (field.getFieldArray()[newX][newY] instanceof Grass) {
            eat((Grass) field.getFieldArray()[newX][newY]);
        }
    }


    public void eat(Grass grass) {
        if (grass.isAvailable()) {
            this.energy += grass.getEnergy();
            grass.eaten();
        }
    }

    public void updateEnergy(Field field) {
        energy--;
        if (energy < minEnergy) {
            field.getFieldArray()[x][y] = null; // Удаляем кролика с поля
        }
    }
    public int getEnergy() {
        return energy; // Возвращаем текущее количество энергии
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
                            field.getFieldArray()[newX][newY] = new Rabbit(10, 10, newX, newY, 0);
                            energy -= REPRODUCTION_ENERGY; // Уменьшаем энергию после размножения
                            return;
                        }
                    }
                }
            }
        }
    }
}
