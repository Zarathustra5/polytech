public class Grass {
    private int energy;
    private int x;
    private int y;
    private int regenerationTime;

    public Grass(int energy, int x, int y) {
        this.energy = energy;
        this.x = x;
        this.y = y;
        this.regenerationTime = 0;
    }
    public void regenerate() {
        if (regenerationTime > 0) {
            regenerationTime--;
        }
    }

    public boolean isAvailable() {
        return regenerationTime == 0;
    }

    public void eaten() {
        regenerationTime = 5; // Восстановление после съедения

    }

    public int getEnergy() {
        return energy; // Возвращаем текущее количество энергии
    }
}
