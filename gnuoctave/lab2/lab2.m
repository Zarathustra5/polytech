% Задаем матрицу вероятностей согласия
P = [
    1/2, 1/2, 3/4;  % Без сопровождения
    1/2, 1/3, 1/4;   % Вместе с В.
    1/4, 2/3, 1/5;   % Вместе с Г.
    3/5, 2/5, 1/5;   % Вместе с Д.
];

n = 4; % Количество недель
num_states = 4; % Количество состояний (без сопровождения, В., Г., Д.)
num_choices = 3; % Количество вариантов выбора (В., Г., Д.)

% Инициализация таблиц для значений и стратегий
V = zeros(n + 1, num_states); % V(k, s) - макс. ожидаемое число согласий с недели k до конца, если предыдущее состояние s
d_opt = zeros(n, num_states); % d_opt(k, s) - оптимальный выбор на неделе k при предыдущем состоянии s

% Граничное условие: V(n+1, :) = 0
V(n + 1, :) = 0;

% Динамическое программирование "назад"
for k = n:-1:1
    for s_prev = 1:num_states
        max_val = -Inf;
        best_choice = 0;
        for d = 1:num_choices
            current_val = P(s_prev, d) + V(k + 1, d + 1);
            if current_val > max_val
                max_val = current_val;
                best_choice = d;
            end
        end
        V(k, s_prev) = max_val;
        d_opt(k, s_prev) = best_choice;
    end
end

% Начальное состояние: без сопровождения (s=1)
initial_state = 1;

% Выводим результаты
fprintf('Максимальное ожидаемое количество согласий: %.2f\n', V(1, initial_state));
fprintf('Оптимальная стратегия:\n');

% Восстанавливаем стратегию
current_state = initial_state;
for k = 1:n
    choice = d_opt(k, current_state);
    fprintf('Неделя %d: обратиться к ', k);
    switch choice
        case 1
            fprintf('В.');
            next_state = 2;
        case 2
            fprintf('Г.');
            next_state = 3;
        case 3
            fprintf('Д.');
            next_state = 4;
    end
    fprintf(' (Ожидаемое согласий: %.2f)\n', V(k, current_state));
    current_state = next_state;
end
