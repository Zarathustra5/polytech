import java.util.Random;

public class SarsaAgent {
    private final int positionBins;
    private final int velocityBins;
    private final double[][] weights; // Веса для линейной аппроксимации Q-функции
    private final double alpha;
    private final double gamma;
    private final double epsilon;
    private final Random random;

    public SarsaAgent(int positionBins, int velocityBins, double alpha, double gamma, double epsilon) {
        this.positionBins = positionBins; // Количество дискретных интервалов для позиции
        this.velocityBins = velocityBins; // Количество дискретных интервалов для скорости
        this.alpha = alpha; // Скорость обучения
        this.gamma = gamma; // Коэффициент дисконтирования
        this.epsilon = epsilon; // Параметр ε-жадной политики
        this.random = new Random();

        // Инициализируем веса для каждого действия
        this.weights = new double[MountainCar.NUM_ACTIONS][positionBins * velocityBins];

        // Инициализируем веса небольшими случайными значениями
        for (int a = 0; a < MountainCar.NUM_ACTIONS; a++) {
            for (int i = 0; i < weights[a].length; i++) {
                weights[a][i] = random.nextDouble() * 0.01;
            }
        }
    }

    // Преобразование дискретного состояния в индекс признака
    private int stateToFeature(int[] state) {
        return state[0] * velocityBins + state[1];
    }

    // Оценка Q-значения для состояния и действия
    private double qValue(int[] state, int action) {
        return weights[action][stateToFeature(state)];
    }

    // Выбор действия согласно ε-жадной политике
    public int selectAction(MountainCar env) {
        int[] state = env.getDiscreteState(positionBins, velocityBins);

        // С вероятностью epsilon выбираем случайное действие
        if (random.nextDouble() < epsilon) {
            return random.nextInt(MountainCar.NUM_ACTIONS);
        }

        // Иначе выбираем действие с максимальным Q-значением
        return getBestAction(state);
    }

    // Получение лучшего действия для состояния
    private int getBestAction(int[] state) {
        int bestAction = 0;
        double bestValue = qValue(state, 0);

        for (int a = 1; a < MountainCar.NUM_ACTIONS; a++) {
            double value = qValue(state, a);
            if (value > bestValue) {
                bestValue = value;
                bestAction = a;
            }
        }

        return bestAction;
    }

    // Обновление весов по правилу SARSA
    public void update(int[] state, int action, double reward, int[] nextState, int nextAction) {
        double currentQ = qValue(state, action);
        double nextQ = qValue(nextState, nextAction);

        double delta = reward + gamma * nextQ - currentQ;

        // Обновляем вес для текущего состояния-действия
        int featureIndex = stateToFeature(state);
        weights[action][featureIndex] += alpha * delta;
    }
}
