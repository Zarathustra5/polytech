from tensorflow import keras
from tensorflow.keras import layers
import numpy as np
import random
import io
import sys
import matplotlib.pyplot as plt
import re

random.seed(0)

path = "./how_the_steel_was_tempered.txt"

# Форматируем текст перед обучением
with io.open(path, encoding="utf-8") as f:
    text = f.read().lower() # Переводим буквы текста в нижний индекс
text = text[text.rfind("часть вторая"):] # Оставляем только вторую главу для уменьшения размера текста
text = text[:text.rfind("1930–1934")] # Удаляем примечания
text = text.replace("\n", " ") # Удаляем знаки переноса текста

print("Длина исходного текста:", len(text))

# Векторизация последовательностей символов
maxlen = 60 # Длина последовательностей в символах
step = 3 # Новые последовательности выбираются через каждые 3 символа
sentences = [] # Массив последовательностей
next_chars = [] # Массив целей
for i in range(0, len(text) - maxlen, step):
    sentences.append(text[i: i + maxlen])
    next_chars.append(text[i + maxlen])

print("Число последовательностей: ", len(sentences))

chars = sorted(list(set(text))) # Список уникальных символов в корпусе
print("Число уникальных символов: ", len(chars))
char_indices = dict((char, chars.index(char)) for char in chars) # словарь, отображающий уникальные символы в их индексы в списке "chars"

print("Векторизация...")
x = np.zeros((len(sentences), maxlen, len(chars)), dtype=np.bool)
y = np.zeros((len(sentences), len(chars)), dtype=np.bool)
for i, sentence in enumerate(sentences):
    for t, char in enumerate(sentence):
        x[i, t, char_indices[char]] = 1
    y[i, char_indices[next_chars[i]]] = 1  # Прямое кодирование символов в бинарные массивы

# Модель с единственным слоем LSTM для предсказания следующего символа
model = keras.Sequential( 
    [   keras.Input(shape=(maxlen, len(chars))),
        layers.LSTM(128),
        layers.Dense(len(chars), activation="softmax"), ]
)
optimizer = keras.optimizers.RMSprop(learning_rate=0.01)
model.compile(loss="categorical_crossentropy", optimizer=optimizer)

# Функция выборки следующего символа с учетом прогнозов модели
def sample(preds, temperature=1.0):
    preds = np.asarray(preds).astype("float64")
    preds = np.log(preds) / temperature
    exp_preds = np.exp(preds)
    preds = exp_preds / np.sum(exp_preds) # нормализация значений вероятностей
    probas = np.random.multinomial(1, preds, 1)
    return np.argmax(probas)

# Цикл генерации текста - 30 эпох
losses = []
temperatures = { 0.2: [], 0.5: [], 1.0: [], 1.2: [] }
epochs = 30
for epoch in range(1, epochs + 1):
    print("\n Эпоха: ", epoch)
    history = model.fit(x, y, batch_size=128, epochs=1) # Выполнение одной итерации обучения
    start_index = random.randint(0, len(text) - maxlen - 1) # Выбор случайного начального текста
    start_text = text[start_index: start_index + maxlen]
    print('--- Generating with seed: "' + start_text + '"')
    losses.append(history.history["loss"][0])

    # Генерация текста для разных температур
    for temperature in temperatures:
        print('\n --------- temperature: ', temperature)
        sys.stdout.write(start_text)
        prev_text = start_text
        final_text = ""
        for i in range(200): # Генерация 400 символов, начиная с начального текста
            sampled = np.zeros((1, maxlen, len(chars))) # Прямое кодирование символов, сгенерированных до сих пор
            for t, char in enumerate(prev_text):
                sampled[0, t, char_indices[char]] = 1

            preds = model.predict(sampled, verbose=0)[0] # Выбор следующего символа
            next_index = sample(preds, temperature)
            next_char = chars[next_index]

            prev_text += next_char
            prev_text = prev_text[1:]

            sys.stdout.write(next_char)

            final_text += next_char

        temperatures[temperature].append(final_text) # Сохраняем сгенерированный текст в массив для дальнейшего использования при построении графика

# График потерь
plt.figure(figsize=(10, 6))
plt.plot(range(1, epochs + 1), losses)
plt.title('График изменения потерь (loss)')
plt.ylabel('Потеря')
plt.xlabel('Эпоха')
plt.legend()
plt.grid(True)
plt.savefig('loss_plot.png')
plt.show()

# Функция расчета процента правильных слов
def calculate_accuracy(generated_texts, original_text):
    original_words = set(re.findall(r'\b\w+\b', original_text.lower())) # Разделяем исходный текст на слова
    accuracy_by_epoch = []
    for generated_text in generated_texts:
        generated_words = re.findall(r'\b\w+\b', generated_text.lower()) # Разделяем сгенерированный текст на слова
        correct_words = sum(1 for word in generated_words if word in
original_words) # Сравниваем сгенерированные слова со словами исходного текста
        if generated_words:
            accuracy = (correct_words / len(generated_words)) * 100
        else:
            accuracy = 0
        accuracy_by_epoch.append(accuracy)
    return accuracy_by_epoch

# Выводим графики изменения процента правильных слов для каждой температуры
for temperature, generated_texts in temperatures.items():

    accuracy_by_epoch = calculate_accuracy(generated_texts, text)

    plt.figure(figsize=(10, 6))
    plt.plot(range(1, epochs + 1), accuracy_by_epoch)
    plt.title(f"Процент правильно сгенерированных слов при температуре: {temperature}")
    plt.ylabel('Процент правильных слов')
    plt.xlabel('Эпоха')
    plt.grid(True)
    plt.savefig(f"accuracy_plot_{temperature}.png")
    plt.show()
