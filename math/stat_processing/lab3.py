import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import fsolve

# Целевая функция
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

# Метод комплексного приращения
def complex_step_derivative(f, x_vals, i, h=1e-20):
    x_complex = np.array(x_vals, dtype=complex)
    x_complex[i] += 1j * h
    return np.imag(f(*x_complex)) / h

# Линеаризация через комплексное приращение
def linearization_complex_step(x_vals, rel_deltas):
    abs_deltas = [x * delta for x, delta in zip(x_vals, rel_deltas)]
    dy_total = 0
    for i in range(len(x_vals)):
        df_dxi = complex_step_derivative(f, x_vals, i)
        dy_total += abs(df_dxi) * abs_deltas[i]
    return dy_total

# Исходные значения
x_vals = [1.2, 1.8, 2.2, 1.4]
base_rel_deltas = [0.001, 0.001, 0.0005, 0.001]
k = 0.01
N = 10000

# Множество значений δx2
#delta2_values = np.linspace(0.0001, 0.0003, 30)  # от 0.01% до 0.03%
delta2_values = np.linspace(0.0001, 0.02, 30)  # от 0.01% до 2%

# Списки для хранения результатов
results_mc = []
results_kreinovich = []
results_lin = []

# Вычисления
for delta2 in delta2_values:
    rel_deltas = base_rel_deltas.copy()
    rel_deltas[1] = delta2  # меняем только δx2

    dy_mc = monte_carlo_method(x_vals, rel_deltas, N)
    dy_kreinovich = kreinovich_method(x_vals, rel_deltas, k, N)
    dy_lin = linearization_complex_step(x_vals, rel_deltas)

    results_mc.append(dy_mc)
    results_kreinovich.append(dy_kreinovich)
    results_lin.append(dy_lin)

# Построение графика
plt.figure(figsize=(10, 6))
plt.plot(delta2_values * 100, results_mc, label='Монте-Карло', marker='o')
plt.plot(delta2_values * 100, results_kreinovich, label='Крейнович', marker='s')
plt.plot(delta2_values * 100, results_lin, label='Линеаризация (комплексное приращение)', marker='^')

plt.xlabel("δx₂, %")
plt.ylabel("Оценка Δy")
plt.title("Зависимость оценки Δy от δx₂")
plt.grid(True)
plt.legend()
plt.tight_layout()
plt.savefig("methods_comparing_prir.png")
plt.show()
