import numpy as np
import matplotlib.pyplot as plt

# Методы оценки точки
def midpoint(x): return 0.5 * (np.min(x) + np.max(x))
def mean(x): return np.mean(x)
def trimmed_mean(x, k):
    x_sorted = np.sort(x)
    trim = int(k * len(x))
    if len(x_sorted[trim:-trim]) == 0: return np.nan
    return np.mean(x_sorted[trim:-trim])
def median(x): return np.median(x)

def monte_carlo(x, relative_errors, estimator, N):
    deltas = np.abs(x) * relative_errors
    noise = np.random.uniform(-deltas, deltas, size=(N, len(x)))
    x_noisy = x + noise
    estimates = np.apply_along_axis(estimator, 1, x_noisy)
    return np.max(np.abs(estimates - estimator(x)))

def run_experiment(x_base, k, N_mc, delta_n_range):
    results = {"середина размаха": [], "среднее арифметическое": [], "усеченное среднее": [], "медиана": []}
    for delta_n in delta_n_range:
        relative_errors = np.array([0.01] * (len(x_base) - 1) + [delta_n])
        results["середина размаха"].append(monte_carlo(x_base, relative_errors, midpoint, N_mc))
        results["среднее арифметическое"].append(monte_carlo(x_base, relative_errors, mean, N_mc))
        results["усеченное среднее"].append(monte_carlo(x_base, relative_errors, lambda x: trimmed_mean(x, k), N_mc))
        results["медиана"].append(monte_carlo(x_base, relative_errors, median, N_mc))
    return results

def plot_results(results_dict, delta_n_range, title, image_name):
    plt.figure(figsize=(10, 6))
    for key, values in results_dict.items():
        plt.plot(delta_n_range, values, label=key)
    plt.xlabel('Δn (отн. погрешность выброса)')
    plt.ylabel('Макс. отклонение Δy')
    plt.title(title)
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.savefig(image_name)
    plt.show()

# --- Настройки ---
np.random.seed(0)
N_mc = 1000#00
delta_n_range = np.linspace(5.0010, 10.0011, 20)
k_values = [0.2, 0.1]

# --- Базовая выборка ---
base_x = np.random.uniform(-1, 1, size=99).tolist() + [1.589]

# --- Модификации выборки ---
modifications = {
    "без выбросов": base_x,
    "1 выброс": base_x + [10],
    "2 выброса": base_x + [10, -8],
    "3 выброса": base_x + [10, -8, 7],
}

# --- Запуск для k = 0.2 и 0.1 ---
for k in k_values:
	print(f"▶ Запуск расчётов для k = {k}...")
	results = {}
	for label, x_mod in modifications.items():
		x_mod = np.array(x_mod)
		results[label] = run_experiment(x_mod, k=k, N_mc=N_mc, delta_n_range=delta_n_range)
		plot_results(results[label], delta_n_range, f"Погрешности (k={k}), {label}", f"{k}_{label}.png")
