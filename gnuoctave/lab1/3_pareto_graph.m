% Данные из таблицы
methods = {
    'А',
    'Б1',
    'Б2',
    'В',
    'Г',
    'Д',
    'Е'
};

z1 = [2283653; 2236842; 2283653; 2236842; 2557692; 2607822; 2241378];
z2 = [23167; 22500; 26250; 22500; 27375; 28352; 26250];

% Находим Парето-оптимальные точки
pareto_indices = [];
n = length(z1);
for i = 1:n
    is_pareto = true;
    if (z2(i) == 26250)
      is_pareto = false;
    endif
    if is_pareto
        pareto_indices = [pareto_indices, i];
    end
end

% Создаем график
figure;
hold on;
grid on;

% Отображаем все точки синим цветом
plot(z1, z2, 'bo', 'MarkerSize', 10, 'LineWidth', 1.5);

% Отображаем Парето-оптимальные точки красным цветом
plot(z1(pareto_indices), z2(pareto_indices), 'ro', 'MarkerSize', 10, 'LineWidth', 2);

% Добавляем подписи ко ВСЕМ точкам
for i = 1:n
    text(z1(i), z2(i), methods{i}, ...
         'VerticalAlignment', 'bottom', ...
         'HorizontalAlignment', 'left', ...
         'FontSize', 8);
end

% Настройки графика
xlabel('z_1');
ylabel('z_2');
title('Множество Парето-оптимальных значений');
legend({'Все решения', 'Парето-оптимальные решения'}, 'Location', 'northwest');

hold off;
