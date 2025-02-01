import random
import matplotlib.pyplot as plt

# Подключаем библиотеки, объявляем константы, создаем особь со случайным генетическим кодом, объединяем особи в популяцию, вычисляем по заданной функции приспособленность каждой особи
# Запускаем цикл эпох, в котором описываем генетический алгоритм: отбираем среди особей наиболее приспособленных с помощью турнира, создаем экземпляры классов для клонированных особей, скрещиваем особи, вызываем мутацию, обновляем популяцию новым поколением, составляем промежуточную статистику для эпохи
# Формируем окончательную статистику по всем эпохам

#константы
CHROMOSOME_LENGTH = 31 #длина хромосомы (N)
POPULATION_SIZE = 70 #кол-во индивидуумов в популяции (P)
P_CROSSOVER = 0.9 #вероятность скрещивания (Pk)
P_MUTATION = 0.2 #вероятность мутации (Pm)

MAX_GENERATIONS = 200 #максимальное число поколений
#RANDOM_SEED = 7
RANDOM_SEED = 7
random.seed(RANDOM_SEED)

# Класс индивидуальной особи
class Individual(list):
    def __init__(self, *args):
        super().__init__(*args)
        self.fitness = [0]
        self.rouletteFitness = 0

# Функция создания особи
def individualCreator():
    return Individual([random.randint(0,1) for i in range(CHROMOSOME_LENGTH)])

# Функция создания популяции с заданным размером
def populationCreator(n = 0):
    return list([individualCreator() for i in range(n)])

# Функция приспособленности
def geneticFitness(individual):
    PSL = getPsl(individual)
    N = len(individual)
    return N / PSL if PSL > 0 else 0,

# Функция вычисления макс значение боковых лепестков
def getPsl(individual):
    individual = [-1 if x == 0 else x for x in individual]
    N = len(individual)
    Rk = []
    for k in range(-N + 1, N):
        Rk.append(sum(individual[i] * individual[i - k] for i in range(N) if 0 <= i - k < N))
    PSL = max(Rk[i] for i in range(len(Rk)) if i != N - 1)
    return PSL

# Функция отбора особей рулеткой
def selRoulette(rouletteAims, p_len, childCount):
    offspring = []
    # Обновляем значение приспособленности для мутантов и детей
    fitnessValues = list(map(geneticFitness, rouletteAims)) #приспособленности каждой особи
    for individual, fitnessValue in zip(rouletteAims, fitnessValues):
        individual.fitness = fitnessValue
    # Формируем возвращаемый массив offspring, длиной равной длине предыдущего поколения (учитывая  то, что появились новые индвиды благодаря скрещиванию)
    for i in range(p_len - childCount):
        for j in range(len(rouletteAims)):
            rouletteAims[j].rouletteFitness = rouletteAims[j].fitness[0] / sum(rouletteAims[j]) 
        ballStop = random.uniform(0, 1)
        rouletteCount = 0
        for k in range(len(rouletteAims)):
            rouletteCount += rouletteAims[k].rouletteFitness
            if rouletteCount >= ballStop:
                offspring.append(rouletteAims[k])
                del rouletteAims[k]
                break
    return offspring 

#одноточечный кроссенговер (скрещивание)
def cxOnePoint(parent1, parent2):
    cutPoint = random.randint(2, len(parent1)-3) #случайная точка разреза хромосомы
    child1 = Individual(parent2[cutPoint:] + parent1[:cutPoint])
    child2 = Individual(parent2[:cutPoint] + parent1[cutPoint:])
    #child1 = mutFlipBit(child1)
    #child2 = mutFlipBit(child2)
    return [child1, child2]

def mutFlipBit(mutant, indpb=P_MUTATION):
    for indx in range(len(mutant)):
        if random.random() < indpb:
            mutant[indx] = 0 if mutant[indx] == 1 else 1 # инверсия бита
    return mutant

population = populationCreator(n=POPULATION_SIZE)

fitnessValues = list(map(geneticFitness, population)) #приспособленности каждой особи
for individual, fitnessValue in zip(population, fitnessValues):
    individual.fitness = fitnessValue

averageFitnessValues = []

#fitnessValues = [individual.fitness[0] for individual in population]

# массив с выведенными в консоле лучшими индивидами (нужен для устранения повторного вывода одной и той же особи в консоль)
shownIndividuals = []
# главный цикл выполнения генетического алгоритма
for i in range(MAX_GENERATIONS):
    childCount = 0
    offspring = population

    for parent1, parent2 in zip(offspring[::2], offspring[1::2]):
        if random.random() < P_CROSSOVER:
            children = cxOnePoint(parent1, parent2)
            offspring.append(children[0])
            offspring.append(children[1])
            childCount += 2

    for j in range(len(offspring)):
        if random.random() < P_MUTATION:
            offspring[j] = mutFlipBit(offspring[j], indpb=1.0/CHROMOSOME_LENGTH)

    # отбираем особи рулеткой
    offspring = selRoulette(offspring, len(offspring), childCount)

    freshFitnessValues = list(map(geneticFitness, offspring))
    for individual, fitnessValue in zip(offspring, freshFitnessValues):
        individual.fitness = fitnessValue

    population[:] = offspring

    fitnessValues = [ind.fitness[0] for ind in population]

    # Вывод статистики в консоль после каждой эпохи
    maxFitness = max(fitnessValues)
    averageFitness = sum(fitnessValues) / len(population)
    averageFitnessValues.append(averageFitness)
    bestIndividual = max(population, key=lambda seq: geneticFitness(seq))
    psl = getPsl(bestIndividual)
    bestIndividual = [-1 if x == 0 else x for x in bestIndividual]
    #if psl == 2:
    if (bestIndividual not in shownIndividuals):
        print("Поколение: ", i + 1, ". Лучший индивидуум = ", *bestIndividual, "с psl ", psl, "\n")
        shownIndividuals.append(bestIndividual)

# Формирование графиков статистики

# График средней приспособленности поколений
def drawStat():
    plt.plot(averageFitnessValues, color='green')
    plt.xlabel('Поколение')
    plt.ylabel('Средняя приспособленность')
    plt.title('Зависимость средней приспособленности от поколения')
    plt.show()

# График АКФ
def drawAcf():
    best_individual = max(population, key=lambda seq: geneticFitness(seq))
    N = len(best_individual)
    Rk = []
    PSL = getPsl(best_individual)
    best_individual = [-1 if x == 0 else x for x in best_individual]
    for k in range(-N + 1, N):
        Rk.append(sum(best_individual[i] * best_individual[i - k] for i in range(N) if 0 <= i - k < N))
    plt.figure(figsize=(10, 6))
    plt.plot(range(-N + 1, N), Rk, label="АКФ")
    plt.axhline(y=PSL, color='r', linestyle='-', label='PSL={}'.format(PSL))
    plt.legend()
    plt.title('АКФ для эпохи №{}'.format(MAX_GENERATIONS))
    plt.xlabel("k")
    plt.ylabel("АКФ")
    plt.grid(True)
    plt.show()

drawStat()
drawAcf()
