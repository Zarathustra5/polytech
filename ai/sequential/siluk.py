import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn import preprocessing
import keras
from keras.models import Sequential
from keras.layers import Dense, Dropout, Flatten
from keras.layers import Conv2D, MaxPooling2D
from keras.callbacks import EarlyStopping
from keras import backend as K
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix
import seaborn as sns
from keras import utils
from sklearn.model_selection import train_test_split
import tensorflow as tf

if __name__ == '__main__':
    df = pd.read_csv("mnist_test.csv", header=None)
    # отбор примеров по признаку соответствия метки класса – для варианта цифр (1,2,3,4,5)
    data = df.loc[(df[0] == 1) | (df[0] == 2) | (df[0] == 3) | (df[0] == 4) | (df[0] == 5)]
    # запись в X содержимого data без первого символа (748 значений яркостей пикселей)
    X = data.drop(columns=[0])
    # запись закодированных цифр в y
    y = data[0]
    # составление обучающей и тестовой выборки
    X_train, X_test, y_train, y_test = train_test_split(X, y, train_size=1000, test_size=500, stratify=y,
    random_state=42)
    # определение размерности изображения 28х82
    img_rows, img_cols = 28, 28
    # нормализация входных данных – к интервалу [0, 1]
    X_train /= 255
    X_test /= 255
    # преобразование входных данных в двумерный массив
    X_train = X_train.values.reshape(X_train.shape[0], img_rows, img_cols, 1)
    X_test = X_test.values.reshape(X_test.shape[0], img_rows, img_cols, 1)
    # количество распознаваемых классов - 5
    num_classes = 5
    # Кодирование целевых меток со значением от 0 до 2.
    le = preprocessing.LabelEncoder()
    # сопоставление закодированных цифр(y = {0, 2, 4}) числовым меткам (0, 1, 2)
    le.fit(y)
    # преобразование правильных ответов в one-hot представление: векторы размерностью 5, в которых
    # единственная единица на месте нужной цифры
    y_train = utils.to_categorical(le.transform(y_train), num_classes)
    y_test = utils.to_categorical(le.transform(y_test), num_classes)
    model = Sequential()
    # добавление сверточных слоев
    # аргумент input_shape сообщает размер тензора на входе
    model.add(Conv2D(32, kernel_size=(3, 3), activation='relu', input_shape=(28, 28, 1)))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(0.25))
    model.add(Conv2D(64, (3, 3), activation='relu')) # слой 1
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(0.25))
    model.add(Conv2D(64, (3, 3), activation='relu')) # слой 2
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(0.25))
    # из двумерных массивов сделать вектор для передачи в первый полносвязный слой
    model.add(Flatten())
    # первый полносвязный слой
    model.add(Dense(128, activation='relu'))
    model.add(Dropout(0.25))
    # второй полносвязный слой
    model.add(Dense(5, activation='softmax')) # Заменил num_classes на 5
    # размер пакета данных - 64
    batch_size = 64
    # определение количества эпох - 25
    epochs = 25
    # прекращение обучения в случае, когда показатель val_loss перестает улучшаться
    earlyStopping = EarlyStopping(monitor='val_loss', patience=10, verbose=0, restore_best_weights=True)
    # вычисление ошибки с использованием функции кросс-энтропии, алгоритм оптимизации Adam
    model.compile(loss=keras.losses.categorical_crossentropy, optimizer='adam', metrics=['accuracy'])
    # запуск обучения
    history = model.fit(X_train, y_train, batch_size=batch_size, epochs=epochs, verbose=1, validation_data=(X_test, y_test), callbacks=[earlyStopping]).history
    # вывод результатов обучения сети
    score = model.evaluate(X_train, y_train, verbose=0)
    print('Final train loss:', score[0])
    print('Final train accuracy:', score[1])
    score = model.evaluate(X_test, y_test, verbose=0)
    print('Test loss:', score[0])
    print('Test accuracy:', score[1])
    plt.plot(history['loss'])
    plt.plot(history['val_loss'])
    plt.title('model loss')
    plt.ylabel('loss')
    plt.xlabel('epoch')
    plt.legend(['train', 'test'], loc='upper left')
    plt.show()
    plt.plot(history['accuracy'])
    plt.plot(history['val_accuracy'])
    plt.title('model accuracy')
    plt.ylabel('accuracy')
    plt.xlabel('epoch')
    plt.legend(['train', 'test'], loc='upper left')
    plt.show()
    # вычисление матрицы неточности и ее отображение
    cm = confusion_matrix(np.argmax(y_test, axis=1), np.argmax(model.predict(X_test), axis=-1))
    cmn = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis]
    fig, ax = plt.subplots(figsize=(8, 8))
    classes = [1, 2, 3, 4, 5]
    sns.heatmap(cmn, annot=True, fmt='.2f', xticklabels=classes, yticklabels=classes)
    plt.show()
