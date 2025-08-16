import numpy as np
from scipy.stats import mannwhitneyu
from statsmodels.stats.proportion import proportion_confint
import matplotlib.pyplot as plt

np.random.seed(42)

# === Константы ===
v = 14
alpha = 0.05
N = 1000
sample_sizes = np.arange(30, 211, 20)
gamma = 0.01  # 1% относительная погрешность

# === Генераторы выборок ===
def generate_normal_pair(m, mean, std1=1.0, std2=2.0):
    return np.random.normal(mean, std1, m), np.random.normal(mean, std2, m)

def generate_uniform_pair(m, mean, width1=2.0, width2=4.0):
    a1, b1 = mean - width1/2, mean + width1/2
    a2, b2 = mean - width2/2, mean + width2/2
    return np.random.uniform(a1, b1, m), np.random.uniform(a2, b2, m)

# === Проведение эксперимента ===
def mann_whitney_experiment(generator, m, alpha, add_noise=False):
    rejections = 0
    undecidable = 0
    for _ in range(N):
        x, y = generator(m, v)
        if add_noise:
            x += x * np.random.uniform(-gamma, gamma, size=x.shape)
            y += y * np.random.uniform(-gamma, gamma, size=y.shape)
        stat, p = mannwhitneyu(x, y, alternative='two-sided')
        if p < alpha:
            rejections += 1
        elif abs(p - alpha) < 0.01:  # зона неопределённости
            undecidable += 1
    p_hat = rejections / N
    undecidable_ratio = undecidable / N
    ci = proportion_confint(rejections, N, alpha=0.05, method='wilson')
    return p_hat, ci, undecidable_ratio

# === Пункты 1–3 ===
print(" Пункты 1–3: Уровень значимости (m = 50)")

p_norm, ci_norm, _ = mann_whitney_experiment(generate_normal_pair, 50, alpha)
print(f"[Normal] α_оценка = {p_norm:.4f}, CI = [{ci_norm[0]:.4f}, {ci_norm[1]:.4f}]")

p_unif, ci_unif, _ = mann_whitney_experiment(generate_uniform_pair, 50, alpha)
print(f"[Uniform] α_оценка = {p_unif:.4f}, CI = [{ci_unif[0]:.4f}, {ci_unif[1]:.4f}]\n")

# === Пункт 4: График CI от m ===
print(" Пункт 4: Зависимость доверительного интервала от размера выборки m")
ci_lows, ci_highs = [], []

for m in sample_sizes:
    p_hat, ci, _ = mann_whitney_experiment(generate_normal_pair, m, alpha)
    ci_lows.append(ci[0])
    ci_highs.append(ci[1])
    print(f"m = {m}: CI = [{ci[0]:.4f}, {ci[1]:.4f}]")

plt.figure(figsize=(10, 5))
plt.plot(sample_sizes, ci_lows, label="Нижняя граница CI", marker='o')
plt.plot(sample_sizes, ci_highs, label="Верхняя граница CI", marker='s')
plt.axhline(y=alpha, color='gray', linestyle='--', label="Заданный уровень значимости (α=0.05)")
plt.xlabel("Размер выборки m")
plt.ylabel("Границы доверительного интервала")
plt.title("Границы CI уровня значимости от размера выборки (Normal)")
plt.legend()
plt.grid()
plt.tight_layout()
plt.show()

# === Пункты 5–6: Влияние погрешности ===
print("\n Пункты 5–6: Наследственная погрешность и зона неопределённости")

undecidables = []
for m in sample_sizes:
    _, _, undec_ratio = mann_whitney_experiment(generate_normal_pair, m, alpha, add_noise=True)
    undecidables.append(undec_ratio)
    print(f"m = {m}: Доля неопределённых решений = {undec_ratio:.4f}")

plt.figure(figsize=(10, 5))
plt.plot(sample_sizes, undecidables, marker='d', color='purple', label="Доля неопределённых решений")
plt.xlabel("Размер выборки m")
plt.ylabel("Доля неопределённости")
plt.title("Зависимость неопределённости от размера выборки (с погрешностью)")
plt.grid()
plt.legend()
plt.tight_layout()
plt.show()

# === Пункт 8: Ошибка второго рода при сдвиге среднего ===
print("\n Пункт 8: Ошибка II рода при сдвиге среднего")
ks = list(np.round(np.arange(0.1, 1.1, 0.1), 1)) + list(range(2, 11))
errors_beta_normal = []
errors_beta_uniform = []

def shifted_normal_pair(m, mean_shift):
    return np.random.normal(v, 1.0, m), np.random.normal(v * mean_shift, 1.0, m)

def shifted_uniform_pair(m, mean_shift):
    width = 2.0
    a1, b1 = v - width/2, v + width/2
    a2, b2 = v * mean_shift - width/2, v * mean_shift + width/2
    return np.random.uniform(a1, b1, m), np.random.uniform(a2, b2, m)

for k in ks:
    p_norm, _, _ = mann_whitney_experiment(lambda m, _: shifted_normal_pair(m, k), 50, alpha)
    p_unif, _, _ = mann_whitney_experiment(lambda m, _: shifted_uniform_pair(m, k), 50, alpha)
    errors_beta_normal.append(1 - p_norm)
    errors_beta_uniform.append(1 - p_unif)
    print(f"k = {k:.1f} | β (Normal) = {1 - p_norm:.4f} | β (Uniform) = {1 - p_unif:.4f}")

plt.figure(figsize=(10, 5))
plt.plot(ks, errors_beta_normal, label="Ошибка II рода (Normal)", marker='o')
plt.plot(ks, errors_beta_uniform, label="Ошибка II рода (Uniform)", marker='s')
plt.xlabel("Коэффициент сдвига k")
plt.ylabel("Вероятность ошибки II рода")
plt.title("Ошибка II рода при сдвиге среднего")
plt.grid()
plt.legend()
plt.tight_layout()
plt.show()