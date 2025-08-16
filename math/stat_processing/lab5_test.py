import numpy as np
import matplotlib.pyplot as plt

# --- Методы оценки ---
def midpoint(x): return 0.5 * (np.min(x) + np.max(x))
def mean(x): return np.mean(x)
def trimmed_mean(x, k):
    x_sorted = np.sort(x)
    trim = int(k * len(x))
    core = x_sorted[trim:-trim]
    return np.mean(core) if len(core) > 0 else np.nan
def median(x): return np.median(x)

# --- Monte Carlo ---
def monte_carlo_error_vec(x, relative_errors, estimator, N):
    deltas = np.abs(x) * relative_errors
    noise = np.random.uniform(-deltas, deltas, size=(N, len(x)))
    x_noisy = x + noise
    estimates = np.apply_along_axis(estimator, 1, x_noisy)
    return np.max(np.abs(estimates - estimator(x)))

# --- Analytical Errors ---
def analytical_midpoint_error(x, deltas):
    idx_min = np.argmin(x)
    idx_max = np.argmax(x)
    return 0.5 * (deltas[idx_min] + deltas[idx_max])

def analytical_mean_error(deltas):
    return np.mean(deltas)

def analytical_trimmed_mean_error(x, deltas, k):
    n = len(x)
    trim = int(k * n)
    sorted_indices = np.argsort(x)
    core_indices = sorted_indices[trim: n - trim]
    return np.mean(deltas[core_indices]) if len(core_indices) > 0 else np.nan

def analytical_median_error(x, deltas):
    n = len(x)
    sorted_indices = np.argsort(x)
    if n % 2 == 1:
        idx = sorted_indices[n // 2]
        return deltas[idx]
    else:
        idx1 = sorted_indices[n // 2 - 1]
        idx2 = sorted_indices[n // 2]
        return 0.5 * (deltas[idx1] + deltas[idx2])

# --- Основной блок ---
def run_all_estimations(x, k, N_mc, delta_n_range):
    mc_res = {"midpoint": [], "mean": [], "trimmed": [], "median": []}
    an_res = {"midpoint": [], "mean": [], "trimmed": [], "median": []}

    for delta_n in delta_n_range:
        rel_err = np.array([0.01] * (len(x) - 1) + [delta_n])
        deltas = np.abs(x) * rel_err

        # --- Аналитика ---
        an_res["midpoint"].append(analytical_midpoint_error(x, deltas))
        an_res["mean"].append(analytical_mean_error(deltas))
        an_res["trimmed"].append(analytical_trimmed_mean_error(x, deltas, k))
        an_res["median"].append(analytical_median_error(x, deltas))

        # --- Monte Carlo ---
        mc_res["midpoint"].append(monte_carlo_error_vec(x, rel_err, midpoint, N_mc))
        mc_res["mean"].append(monte_carlo_error_vec(x, rel_err, mean, N_mc))
        mc_res["trimmed"].append(monte_carlo_error_vec(x, rel_err, lambda x: trimmed_mean(x, k), N_mc))
        mc_res["median"].append(monte_carlo_error_vec(x, rel_err, median, N_mc))

    return an_res, mc_res

# --- Визуализация ---
def plot_dual(results1, results2, delta_n_range, method, title_prefix):
    plt.figure(figsize=(8, 5))
    plt.plot(delta_n_range, results1[method], label="Аналитика", linestyle='--', marker='o')
    plt.plot(delta_n_range, results2[method], label="Монте-Карло", linestyle='-', marker='x')
    plt.title(f"{title_prefix} — {method}")
    plt.xlabel("Δn (отн. погрешность выброса)")
    plt.ylabel("Δy (макс. отклонение оценки)")
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.show()

# --- Конфигурация ---
np.random.seed(0)
base_x = np.random.uniform(-1, 1, size=99).tolist() + [1.589]
delta_n_range = np.linspace(0.01, 0.2, 20)
N_mc = 1000

# --- k = 0.2 ---
print("▶ Расчёты для k = 0.2...")
an_k02, mc_k02 = run_all_estimations(np.array(base_x), k=0.2, N_mc=N_mc, delta_n_range=delta_n_range)

# --- k = 0.1 ---
print("▶ Расчёты для k = 0.1...")
an_k01, mc_k01 = run_all_estimations(np.array(base_x), k=0.1, N_mc=N_mc, delta_n_range=delta_n_range)

# --- Визуализация 8 графиков ---
for method in ["midpoint", "mean", "trimmed", "median"]:
    plot_dual(an_k02, mc_k02, delta_n_range, method, "k=0.2")
for method in ["trimmed"]:
    plot_dual(an_k01, mc_k01, delta_n_range, method, "k=0.1")
