import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import fsolve

# Функция, заданная в задании
def f(x1, x2, x3, x4):
    return (x1**2 * x2**2 - x3**2 * x4**2) / (x1 + x2 * x3 * x4)

# Генерация случайных значений по Коши
def generate_cauchy_samples(x, dx, k, N):
    scale = k * dx
    return x + scale * np.tan(np.pi * (np.random.rand(N) - 0.5))

# Метод максимального правдоподобия
def mle_cauchy_scale(dy_sample):
    def equation(d):
        return np.sum(1 / (1 + (dy_sample / d)**2)) - len(dy_sample)/2
    d0 = np.std(dy_sample)
    d_solution = fsolve(equation, d0)
    return np.abs(d_solution[0])

# Метод Крейновича
def kreinovich_method(x_vals, rel_deltas, k, N):
    abs_deltas = [x * delta for x, delta in zip(x_vals, rel_deltas)]
    samples = [generate_cauchy_samples(x, dx, k, N) for x, dx in zip(x_vals, abs_deltas)]
    y_samples = f(*samples)
    y0 = f(*x_vals)
    dy = y_samples - y0
    d_dy = mle_cauchy_scale(dy)
    return np.abs(d_dy) / k

# Генерация по равномерному закону (Метод Монте-Карло)
def generate_uniform_samples(x, dx, N):
    return np.random.uniform(x - dx, x + dx, N)

# Метод Монте-Карло
def monte_carlo_method(x_vals, rel_deltas, N):
    abs_deltas = [x * delta for x, delta in zip(x_vals, rel_deltas)]
    samples = [generate_uniform_samples(x, dx, N) for x, dx in zip(x_vals, abs_deltas)]
    y_samples = f(*samples)
    y0 = f(*x_vals)
    return np.max(np.abs(y_samples - y0))

## Параметры задания
# 3 различных набора данных
x_sets = [
    [1.0, 2.0, 1.5, 3.0],
    [2.0, 1.0, 2.5, 1.5],
    [1.2, 1.8, 2.2, 1.4]
]
rel_deltas = [0.001, 0.001, 0.0005, 0.001]
k = 0.01
N_values = sorted(list({10**1, 10**2, 200, 300, 500, 10**3, 10**4, 10**5}))

# Построение графиков
for i, x_vals in enumerate(x_sets):
    kreinovich_deltas = []
    montecarlo_deltas = []
    for N in N_values:
        kreinovich_deltas.append(kreinovich_method(x_vals, rel_deltas, k, N))
        montecarlo_deltas.append(monte_carlo_method(x_vals, rel_deltas, N))

    plt.plot(np.log10(N_values), kreinovich_deltas, marker='o', label=f'Крейнович, Набор {i+1}')
    plt.plot(np.log10(N_values), montecarlo_deltas, marker='s', linestyle='--', label=f'Монте-Карло, Набор {i+1}')

plt.title("Оценка Δy методами Крейновича и Монте-Карло")
plt.xlabel("lg(N)")
plt.ylabel("Оценка Δy")
plt.grid(True)
plt.legend()
plt.savefig('methods_comparing.png')
plt.show()

# Повторение 3 раза эксперимента для набора №3 методом Крейновича
x_vals = x_sets[2]
plt.figure(figsize=(10, 6))
for rep in range(3):
    kreinovich_deltas = []
    for N in N_values:
        dy = kreinovich_method(x_vals, rel_deltas, k, N)
        kreinovich_deltas.append(dy)
    plt.plot(np.log10(N_values), kreinovich_deltas, marker='o', label=f'Повтор {rep+1}')

plt.title("Метод Крейновича: три повтора (набор 3)")
plt.xlabel("lg(N)")
plt.ylabel("Оценка Δy")
plt.grid(True)
plt.legend()
plt.savefig('kreinovich_last_3_times.png')
plt.show()

# Повторение 3 раза эксперимента для набора №3 методом Монте-Карло
plt.figure(figsize=(10, 6))
for rep in range(3):
    montecarlo_deltas = []
    for N in N_values:
        dy = monte_carlo_method(x_vals, rel_deltas, N)
        montecarlo_deltas.append(dy)
    plt.plot(np.log10(N_values), montecarlo_deltas, marker='s', linestyle='--', label=f'Повтор {rep+1}')

plt.title("Метод Монте-Карло: три повтора (набор 3)")
plt.xlabel("lg(N)")
plt.ylabel("Оценка Δy")
plt.grid(True)
plt.legend()
plt.savefig('montecarlo_last_3_times.png')
plt.show()
