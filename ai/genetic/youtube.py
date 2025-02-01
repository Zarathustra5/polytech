import random
import matplotlib.pyplot as plt

# Подключаем библиотеки, объявляем константы, создаем особь со случайным генетическим кодом, объединяем особи в популяцию, вычисляем по заданной функции приспособленность каждой особи
# Запускаем цикл эпох, в котором описываем генетический алгоритм: отбираем среди особей наиболее приспособленных с помощью турнира, создаем экземпляры классов для клонированных особей, скрещиваем особи, вызываем мутацию, обновляем популяцию новым поколением, составляем промежуточную статистику для эпохи
# Формируем окончательную статистику по всем эпохам

#константы
ONE_MAX_LENGTH = 33 #длина хромосомы
POPULATION_SIZE = 70 #кол-во индивидуумов в популяции
P_CROSSOVER = 0.8 #вероятность скрещивания
P_MUTATION = 0.15 #вероятность мутации

MAX_GENERATIONS = 50 #максимальное число поколений
RANDOM_SEED = 42
random.seed(RANDOM_SEED)

class IndividualFitness():
    def __init__(self):
        self.values = [0]

class Individual(list):
    def __init__(self, *args):
        super().__init__(*args)
        self.fitness = IndividualFitness()

#функция принадлежности
def geneticFitness(individual):
    PSL = get_psl(individual)
    N = len(individual)
    return N / PSL if PSL > 0 else 0,

def individualCreator():
    return Individual([random.randint(0,1) for i in range(ONE_MAX_LENGTH)])

def populationCreator(n = 0):
    return list([individualCreator() for i in range(n)])

def get_psl(individual):
    N = len(individual)
    Rk = []
    for k in range(-N + 1, N):
        Rk.append(sum(individual[i] * individual[i - k] for i in range(N) if 0 <= i - k < N))
    PSL = max(Rk[i] for i in range(len(Rk)) if i != N - 1)
    return PSL

population = populationCreator(n=POPULATION_SIZE)
generationCounter = 0

fitnessValues = list(map(geneticFitness, population)) #приспособленности каждой особи
for individual, fitnessValue in zip(population, fitnessValues):
    individual.fitness.values = fitnessValue

maxFitnessValues = []
averageFitnessValues = []

def individualizateOffspring(value):
    ind = Individual(value[:])
    ind.fitness.values[0] = value.fitness.values[0]
    return ind

def selTournament(population, p_len):
    offspring = []
    # выбираем 3 случайные особи таким образом, чтобы ни одна из особей не повторялась
    for n in range(p_len):
        i1 = i2 = i3 = 0
        while i1 == i2 or i1 == i3 or i2 == i3:
            i1, i2, i3 = random.randint(0, p_len-1), random.randint(0, p_len-1), random.randint(0, p_len-1)
        offspring.append(max([population[i1], population[i2], population[i3]], key=lambda ind: ind.fitness.values[0])) # из 3х различных особей выбираем особь с наилучшей приспособленностью
    return offspring 

#одноточечный кроссенговер (скрещивание)
def cxOnePoint(child1, child2):
    cutPoint = random.randint(2, len(child1)-3) #случайная точка разреза хромосомы
    child1[cutPoint:], child2[cutPoint:] = child2[cutPoint:], child1[cutPoint:]

def mutFlipBit(mutant, indpb=0.01):
    for indx in range(len(mutant)):
        if random.random() < indpb:
            mutant[indx] = 0 if mutant[indx] == 1 else 1 # инверсия бита

fitnessValues = [individual.fitness.values[0] for individual in population]

# главный цикл выполнения генетического алгоритма
while max(fitnessValues) < ONE_MAX_LENGTH and generationCounter < MAX_GENERATIONS:
    generationCounter += 1
    # отбираем наиболее приспособленные особи
    offspring = selTournament(population, len(population))
    # пересоздаем экземпляры классов для каждой отобранной особи для предотвращения использования несколькими особями одного и того же экземпляра класса (в случае победы особи в турнире более одного раза)
    offspring = list(map(individualizateOffspring, offspring))

    for child1, child2 in zip(offspring[::2], offspring[1::2]):
        if random.random() < P_CROSSOVER:
            cxOnePoint(child1, child2)

    for mutant in offspring:
        if random.random() < P_MUTATION:
            mutFlipBit(mutant, indpb=1.0/ONE_MAX_LENGTH)

    freshFitnessValues = list(map(geneticFitness, offspring))
    for individual, fitnessValue in zip(offspring, freshFitnessValues):
        individual.fitness.values = fitnessValue

    population[:] = offspring

    fitnessValues = [ind.fitness.values[0] for ind in population]

    # Вывод статистики в консоль после каждой эпохи
    maxFitness = max(fitnessValues)
    averageFitness = sum(fitnessValues) / len(population)
    maxFitnessValues.append(maxFitness)
    averageFitnessValues.append(averageFitness)
    print(f"Поколение {generationCounter}: Максимальная приспособленность = {maxFitness}, Средняя приспособленность = {averageFitness}")
    best_index = fitnessValues.index(max(fitnessValues))
    print("Лучший индивидуум = ", *population[best_index], "\n")

# Формирование графиков статистики
def draw_stat():
    plt.plot(maxFitnessValues, color='red')
    plt.plot(averageFitnessValues, color='green')
    plt.xlabel('Поколение')
    plt.ylabel('Макс/средняя приспособленность')
    plt.title('Зависимость макс и средней приспособленности от поколения')
    plt.show()

def draw_acf():
    best_individual = max(population, key=lambda seq: geneticFitness(seq))
    N = len(best_individual)
    Rk = []
    PSL = get_psl(best_individual)
    for k in range(-N + 1, N):
        Rk.append(sum(best_individual[i] * best_individual[i - k] for i in range(N) if 0 <= i - k < N))
    plt.figure(figsize=(10, 6))
    plt.plot(range(-N + 1, N), Rk, marker='o', label="АКФ")
    plt.axhline(y=PSL, color='r', linestyle='--', label=f"PSL = {PSL}")
    plt.axhline(y=-PSL, color='r', linestyle='--')
    plt.title('Автокорреляционная функция (АКФ)')
    plt.xlabel('k')
    plt.ylabel('Rk')
    plt.legend()
    plt.grid()
    plt.show()

draw_acf()
