public class MountainCar {
    // Константы задачи
    private static final double MIN_POSITION = -1.2;
    private static final double MAX_POSITION = 0.6;
    private static final double MAX_SPEED = 0.07;
    private static final double GOAL_POSITION = 0.5;

    // Состояние среды
    private double position;
    private double velocity;
    private boolean done;

    // Возможные действия: -1 (назад), 0 (нейтрально), 1 (вперед)
    public static final int BACKWARD = 0;
    public static final int NEUTRAL = 1;
    public static final int FORWARD = 2;
    public static final int NUM_ACTIONS = 3;

    public MountainCar() {
        reset();
    }

    public void reset() {
        // Начальное положение случайно в диапазоне [-0.6, -0.4]
        position = -0.5 + Math.random() * 0.2 - 0.1;
        velocity = 0.0;
        done = false;
    }

    public double[] getState() {
        return new double[] {position, velocity};
    }

    public boolean isDone() {
        return done;
    }

    public double step(int action) {
        // Преобразуем индекс действия в фактическое действие
        double force = action - 1; // Преобразуем [0,1,2] в [-1,0,1]

        // Обновляем скорость
        velocity += 0.001 * force - 0.0025 * Math.cos(3 * position);

        // Ограничиваем скорость
        if (velocity > MAX_SPEED) velocity = MAX_SPEED;
        if (velocity < -MAX_SPEED) velocity = -MAX_SPEED;

        // Обновляем позицию
        position += velocity;

        // Ограничиваем позицию
        if (position < MIN_POSITION) {
            position = MIN_POSITION;
            velocity = 0; // Отскок от стены
        }

        // Проверяем, достигнута ли цель
        if (position >= GOAL_POSITION) {
            done = true;
            return 0; // Вознаграждение за достижение цели
        }

        return -1; // Штраф за каждый шаг
    }

    // Метод для получения индекса состояния для табличного представления
    public int[] getDiscreteState(int positionBins, int velocityBins) {
        int positionIdx = (int) ((position - MIN_POSITION) / (MAX_POSITION - MIN_POSITION) * positionBins);
        positionIdx = Math.min(positionIdx, positionBins - 1);
        positionIdx = Math.max(positionIdx, 0);

        int velocityIdx = (int) ((velocity + MAX_SPEED) / (2 * MAX_SPEED) * velocityBins);
        velocityIdx = Math.min(velocityIdx, velocityBins - 1);
        velocityIdx = Math.max(velocityIdx, 0);

        return new int[] {positionIdx, velocityIdx};
    }
}
