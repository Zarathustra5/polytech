import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MountainCarMain {
    private static MountainCar env;
    private static SarsaAgent agent;
    private static MountainCarVisualizer visualizer;
    private static Timer timer;
    private static int steps;
    private static double totalReward;
    private static JLabel stepsLabel;
    private static JLabel rewardLabel;

    public static void main(String[] args) {
        // Параметры
        int positionBins = 20;
        int velocityBins = 20;
        double alpha = 0.1;
        double gamma = 0.99;
        double epsilon = 0.01; // Низкое значение для тестирования

        env = new MountainCar();
        agent = new SarsaAgent(positionBins, velocityBins, alpha, gamma, epsilon);

        // Обучаем агента (можно закомментировать, если используем предварительно обученного агента)
        trainAgent(1000);

        // Создаем интерфейс
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void trainAgent(int episodes) {
        System.out.println("Начало обучения...");
        for (int episode = 0; episode < episodes; episode++) {
            env.reset();
            int[] state = env.getDiscreteState(20, 20);
            int action = agent.selectAction(env);

            int episodeSteps = 0;
            double episodeReward = 0;

            while (!env.isDone() && episodeSteps < 1000) {
                double reward = env.step(action);
                episodeReward += reward;

                int[] nextState = env.getDiscreteState(20, 20);
                int nextAction = agent.selectAction(env);

                agent.update(state, action, reward, nextState, nextAction);

                state = nextState;
                action = nextAction;
                episodeSteps++;
            }

            if ((episode + 1) % 100 == 0) {
                System.out.println("Эпизод " + (episode + 1) + ": шагов = " + episodeSteps +
                        ", суммарное вознаграждение = " + episodeReward);
            }
        }
        System.out.println("Обучение завершено.");
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Демонстрация: Машина на горе");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создаем визуализатор
        visualizer = new MountainCarVisualizer();
        frame.add(visualizer, BorderLayout.CENTER);

        // Панель управления
        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Старт");
        JButton resetButton = new JButton("Сброс");
        stepsLabel = new JLabel("Шаги: 0");
        rewardLabel = new JLabel("Вознаграждение: 0.0");

        controlPanel.add(startButton);
        controlPanel.add(resetButton);
        controlPanel.add(stepsLabel);
        controlPanel.add(rewardLabel);

        frame.add(controlPanel, BorderLayout.SOUTH);

        // Обработчики событий
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                    startButton.setText("Старт");
                } else {
                    startSimulation();
                    startButton.setText("Пауза");
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer != null && timer.isRunning()) {
                    timer.stop();
                    startButton.setText("Старт");
                }
                resetSimulation();
            }
        });

        // Настройка окна
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Инициализация
        resetSimulation();
    }

    private static void resetSimulation() {
        env.reset();
        steps = 0;
        totalReward = 0;

        double[] state = env.getState();
        visualizer.updateState(state[0], state[1], 1); // Нейтральное действие

        stepsLabel.setText("Шаги: 0");
        rewardLabel.setText("Вознаграждение: 0.0");
    }

    private static void startSimulation() {
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (env.isDone() || steps >= 1000) {
                    timer.stop();
                    JOptionPane.showMessageDialog(null,
                            "Симуляция завершена!\nШаги: " + steps +
                                    "\nСуммарное вознаграждение: " + totalReward);
                    return;
                }

                // Выбираем действие и выполняем шаг
                int[] state = env.getDiscreteState(20, 20);
                int action = agent.selectAction(env);
                double reward = env.step(action);

                totalReward += reward;
                steps++;

                // Обновляем визуализацию
                double[] rawState = env.getState();
                visualizer.updateState(rawState[0], rawState[1], action);

                // Обновляем метки
                stepsLabel.setText("Шаги: " + steps);
                rewardLabel.setText("Вознаграждение: " + String.format("%.1f", totalReward));
            }
        });

        timer.start();
    }
}
