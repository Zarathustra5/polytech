import numpy as np
import pandas as pd
from sklearn.datasets import make_blobs
from sklearn.cluster import SpectralClustering
from sklearn.metrics.pairwise import rbf_kernel
import matplotlib.pyplot as plt

# Генерация искусственных данных
n_samples = 300  # Уменьшено для наглядности таблицы
centers = 3

# Создаем данные
data, labels_true = make_blobs(n_samples=n_samples, centers=centers, cluster_std=0.6, random_state=42)

# Вычисление матрицы аффинности (с использованием RBF kernel)
n_clusters = 3
gamma = 1.0  # Коэффициент для радиальной базисной функции
affinity_matrix = rbf_kernel(data, gamma=gamma)

# Вывод матрицы аффинности
affinity_df = pd.DataFrame(affinity_matrix, columns=[f"Point {i}" for i in range(n_samples)], index=[f"Point {i}" for i in range(n_samples)])
print("Матрица аффинности:")
print(affinity_df)

# Построение модели спектральной кластеризации
spectral_clustering = SpectralClustering(
    n_clusters=n_clusters,
    #affinity='nearest_neighbors',
    affinity='precomputed',  # Указываем предвычисленную матрицу аффинности
    random_state=42
)

# Обучение модели
#predicted_labels = spectral_clustering.fit_predict(data)
predicted_labels = spectral_clustering.fit_predict(affinity_matrix)

# Визуализация результатов
plt.figure(figsize=(8, 6))
plt.scatter(data[:, 0], data[:, 1], c=predicted_labels, cmap='viridis', s=50)
plt.title("Результаты спектральной кластеризации")
plt.xlabel("Признак 1")
plt.ylabel("Признак 2")
plt.show()
