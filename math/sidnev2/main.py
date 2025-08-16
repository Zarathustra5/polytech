import numpy as np

# Определяем состояния
states = ['S0', 'S1', 'S2', 'S3']
state_indices = {state: idx for idx, state in enumerate(states)}

# Определяем действия
actions = ['V', 'G', 'D']

# Таблица вероятностей согласия
prob_table = {
    'S0': {'V': 0.5, 'G': 0.5, 'D': 0.75},
    'S1': {'V': 0.5, 'G': 0.33, 'D': 0.25},
    'S2': {'V': 0.25, 'G': 0.66, 'D': 0.33},
    'S3': {'V': 0.6, 'G': 0.4, 'D': 0.2},
}

# Параметры задачи
N = 4  # Количество недель

# Инициализируем таблицу для динамического программирования
# V[t][s] - максимальное ожидаемое вознаграждение начиная с шага t в состоянии s
V = np.zeros((N+1, len(states)))
# Определим стратегию
policy = np.empty((N, len(states)), dtype=str)

# Заполняем таблицу снизу вверх
for t in range(N-1, -1, -1):  # от N-1 до 0
    for s in states:
        best_action = None
        best_value = -1
        for a in actions:
            p = prob_table[s][a]
            # Если действие a успешно, переходим в состояние, соответствующее a
            if a == 'V':
                next_state_success = 'S1'
            elif a == 'G':
                next_state_success = 'S2'
            elif a == 'D':
                next_state_success = 'S3'
            # Ожидаемое вознаграждение
            value = p  # Награда за успешное сопровождение
            # Добавляем ожидаемое вознаграждение от следующих шагов
            value += p * V[t+1][state_indices[next_state_success]] + (1 - p) * V[t+1][state_indices['S0']]
            if value > best_value:
                best_value = value
                best_action = a
        V[t][state_indices[s]] = best_value
        policy[t][state_indices[s]] = best_action

# Вывод оптимальной стратегии и ожидаемого вознаграждения
print("Оптимальная стратегия:")
for t in range(N):
    print(f"Неделя {t+1}:")
    for s in states:
        print(f"  Если состояние {s}, выбрать {policy[t][state_indices[s]]}")
print("\nМаксимальное ожидаемое число успешных сопровождений:", V[0][state_indices['S0']])

# Для лучшего понимания можно также вывести V таблицу
print("\nТаблица ожидаемых вознаграждений (V):")
for t in range(N+1):
    print(f"Шаг {t}: ", V[t])
