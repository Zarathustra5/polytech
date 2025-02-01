import numpy as np

def generate_population(size, length):
    return np.random.randint(2, size=(size, length))

def fitness(sequence):
    # Здесь должна быть ваша функция для оценки автокорреляционной функции
    # Например, возвращаемое значение может быть отклонением от цели
    return np.random.rand()  # Замените на вашу логику

def tournament_selection(population, fitness_scores, tournament_size=3):
    selected = np.random.choice(len(population), tournament_size)
    best = selected[np.argmin(fitness_scores[selected])]
    return population[best]

def crossover(parent1, parent2):
    point = np.random.randint(1, len(parent1)-1)
    child1 = np.concatenate((parent1[:point], parent2[point:]))
    child2 = np.concatenate((parent2[:point], parent1[point:]))
    return child1, child2

def mutate(sequence, mutation_rate):
    for i in range(len(sequence)):
        if np.random.rand() < mutation_rate:
            sequence[i] = 1 - sequence[i]  # Инвертируем бит
    return sequence

def genetic_algorithm(pop_size, seq_length, generations, mutation_rate):
    population = generate_population(pop_size, seq_length)

    for generation in range(generations):
        fitness_scores = np.array([fitness(seq) for seq in population])
        
        new_population = []
        for _ in range(pop_size // 2):
            parent1 = tournament_selection(population, fitness_scores)
            parent2 = tournament_selection(population, fitness_scores)
            child1, child2 = crossover(parent1, parent2)
            new_population.append(mutate(child1, mutation_rate))
            new_population.append(mutate(child2, mutation_rate))
        
        population = np.array(new_population)

    # Возвращаем лучшую последовательность
    best_index = np.argmin(fitness_scores)
    return population[best_index]

# Параметры
pop_size = 100 # - P - размер начальной популяции – количество генерируемых КП
seq_length = 10 # - N – длина кодовой последовательности (КП)
generations = 50
mutation_rate = 0.15 # вероятность мутации потомков (Pm)

best_sequence = genetic_algorithm(pop_size, seq_length, generations, mutation_rate)
print("Лучшая последовательность:", best_sequence)
