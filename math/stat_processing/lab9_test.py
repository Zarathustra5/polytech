import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import mannwhitneyu
import statsmodels.stats.proportion as smp

N = 1000
v = 12
alpha = 0.05
gamma = 0.01

np.random.seed(0)

# Пункт 1: Генерация выборок
m = 50
normal_samples_1 = [np.random.normal(loc=v, scale=1.0, size=m) for _ in range(N)]
normal_samples_2 = [np.random.normal(loc=v, scale=2.0, size=m) for _ in range(N)]
uniform_samples_1 = [np.random.uniform(10, 14, size=m) for _ in range(N)]
uniform_samples_2 = [np.random.uniform(8, 16, size=m) for _ in range(N)]

# Пункт 2: Проверка гипотезы
def run_mwu_test(samples1, samples2):
    rejections = 0
    for x, y in zip(samples1, samples2):
        _, p = mannwhitneyu(x, y, alternative='two-sided')
        if p < alpha:
            rejections += 1
    return rejections

rej_norm = run_mwu_test(normal_samples_1, normal_samples_2)
rej_unif = run_mwu_test(uniform_samples_1, uniform_samples_2)

p_norm = rej_norm / N
p_unif = rej_unif / N

ci_norm = smp.proportion_confint(rej_norm, N, alpha=0.05, method='wilson')
ci_unif = smp.proportion_confint(rej_unif, N, alpha=0.05, method='wilson')

print(f"Нормальное: {rej_norm}/1000 -> α = {p_norm:.4f}, CI = {ci_norm}")
print(f"Равномерное: {rej_unif}/1000 -> α = {p_unif:.4f}, CI = {ci_unif}\n")

# Пункт 4: Зависимость доверительных границ от m
sample_sizes = range(20, 201, 20)

# График для нормального распределения
lower_bounds_norm, upper_bounds_norm = [], []
for m in sample_sizes:
    rejections_norm = 0
    for _ in range(N):
        x_norm = np.random.normal(loc=v, scale=1.0, size=m)
        y_norm = np.random.normal(loc=v, scale=2.0, size=m)
        _, p_norm = mannwhitneyu(x_norm, y_norm, alternative='two-sided')
        if p_norm < alpha:
            rejections_norm += 1

    ci_low_norm, ci_up_norm = smp.proportion_confint(rejections_norm, N, alpha=0.05, method='wilson')
    lower_bounds_norm.append(ci_low_norm)
    upper_bounds_norm.append(ci_up_norm)
    print(f"Нормальное: m = {m}, ΔCI = {ci_up_norm - ci_low_norm}")

plt.figure()
plt.plot(sample_sizes, lower_bounds_norm, marker='o', label='Нижняя граница CI (норм.)')
plt.plot(sample_sizes, upper_bounds_norm, marker='o', label='Верхняя граница CI (норм.)')
plt.axhline(0.05, linestyle='--', color='gray', label='α = 0.05')
plt.title('Пункт 4: CI уровня значимости (нормальное распределение)')
plt.xlabel('Размер выборки m')
plt.ylabel('Границы доверительного интервала')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig('lab9_number_4_norm.png')
plt.show()

# График для равномерного распределения
print("")
lower_bounds_unif, upper_bounds_unif = [], []
for m in sample_sizes:
    rejections_unif = 0
    for _ in range(N):
        x_unif = np.random.uniform(10, 14, size=m)
        y_unif = np.random.uniform(8, 16, size=m)
        _, p_unif = mannwhitneyu(x_unif, y_unif, alternative='two-sided')
        if p_unif < alpha:
            rejections_unif += 1

    ci_low_unif, ci_up_unif = smp.proportion_confint(rejections_unif, N, alpha=0.05, method='wilson')
    lower_bounds_unif.append(ci_low_unif)
    upper_bounds_unif.append(ci_up_unif)
    print(f"Равномерное: m = {m}, ΔCI = {ci_up_unif - ci_low_unif}")

plt.figure()
plt.plot(sample_sizes, lower_bounds_unif, marker='s', label='Нижняя граница CI (равн.)')
plt.plot(sample_sizes, upper_bounds_unif, marker='s', label='Верхняя граница CI (равн.)')
plt.axhline(0.05, linestyle='--', color='gray', label='α = 0.05')
plt.title('Пункт 4: CI уровня значимости (равномерное распределение)')
plt.xlabel('Размер выборки m')
plt.ylabel('Границы доверительного интервала')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig('lab9_number_4_unif.png')
plt.show()

# Пункт 5-6: Наследственная погрешность
instability_ratios = []
for m in sample_sizes:
    unstable = 0
    for _ in range(N):
        x = np.random.normal(loc=v, scale=1.0, size=m)
        y = np.random.normal(loc=v, scale=2.0, size=m)

        _, p_orig = mannwhitneyu(x, y, alternative='two-sided')
        r1 = p_orig < alpha

        x_noisy = x * (1 + np.random.uniform(-gamma, gamma, size=m))
        y_noisy = y * (1 + np.random.uniform(-gamma, gamma, size=m))

        _, p_noisy = mannwhitneyu(x_noisy, y_noisy, alternative='two-sided')
        r2 = p_noisy < alpha

        if r1 != r2:
            unstable += 1
    instability_ratios.append(unstable / N)

plt.figure()
plt.plot(sample_sizes, instability_ratios, marker='o', color='crimson')
plt.title('Пункт 6: Неустойчивость от размера выборки')
plt.xlabel('Размер выборки m')
plt.ylabel('Доля неустойчивых решений')
plt.grid(True)
plt.tight_layout()
plt.savefig('lab9_number_6.png')
plt.show()

# Пункт 8: Ошибка II рода при смещении
k_values = np.concatenate((np.arange(0.1, 1.1, 0.1), np.arange(2, 11)))
beta_vals, ci_lowers, ci_uppers = [], [], []

for k in k_values:
    beta_count = 0
    for _ in range(N):
        x = np.random.normal(loc=v, scale=1.0, size=m)
        y = np.random.normal(loc=k * v, scale=2.0, size=m)
        _, p = mannwhitneyu(x, y, alternative='two-sided')
        if p >= alpha:
            beta_count += 1
    beta = beta_count / N
    ci_low, ci_up = smp.proportion_confint(beta_count, N, alpha=0.05, method='wilson')
    beta_vals.append(beta)
    ci_lowers.append(ci_low)
    ci_uppers.append(ci_up)

plt.figure()
plt.plot(k_values, beta_vals, marker='o', label='Ошибка II рода')
plt.fill_between(k_values, ci_lowers, ci_uppers, color='skyblue', alpha=0.3, label='95% CI')
plt.axhline(0.05, linestyle='--', color='gray', label='α = 0.05')
plt.title('Пункт 8: Ошибка II рода от сдвига (k·v)')
plt.xlabel('Коэффициент смещения k')
plt.ylabel('Вероятность ошибки II рода (β)')
plt.grid(True)
plt.legend()
plt.tight_layout()
plt.savefig('lab9_number_8.png')
plt.show()
