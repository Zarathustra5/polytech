function lab3()
    N = 8;          % Число заявок
    S_star = 28;    % Стоимость сети
    M = 4;          % Число узлов
    c = 2 * ones(1, M); % Число каналов в узлах [2,2,2,2] (2_4)
    alpha = ones(1, M); % Коэффициенты важности [1,1,1,1] (I_4)
    P = [0    0.3  0.1  0.6;   % Матрица переходов
         0.2  0    0.4  0.4;
         0.2  0.3  0    0.5;
         0.2  0.4  0.4  0];

    % Шаг 0: Нахождение стационарного распределения весов ω
    [w, ~] = eigs(P', 1, 'lm');
    w = w / sum(w); % Нормировка
    w = w(:);
    fprintf('Стационарные веса ω:\n');
    disp(w');

    % Итерационная оптимизация μ с ограничением S = S*
    mu = optimize_mu(w, c, alpha, S_star, N, M, P);
    fprintf('\nОптимальные μ:\n');
    disp(mu');

    % Вычисление максимальной пропускной способности λ
    lambda_max = calculate_lambda(w, mu, N, M, c, P);
    fprintf('\nМаксимальная λ: %.4f\n', lambda_max);

    % Проверка оптимальности
    check_optimality(w, mu, c, alpha, S_star, N, M, P);
end


function mu = optimize_mu(w, c, alpha, S_star, N, M, P)
    % Начальное приближение μ, удовлетворяющее S = S*
    mu_prev = (S_star / sum(c))^(1/alpha(1)) * ones(1, M);
    max_iter = 1000;
    tolerance = 1e-6;

    for iter = 1:max_iter
        % Вычисление G_M(N) и G_M(N-1)
        [~, ~, G_N] = compute_marginal_distribution(w, mu_prev, c, N, M, P);
        [~, ~, G_N_minus_1] = compute_marginal_distribution(w, mu_prev, c, N-1, M, P);

        % Градиенты λ и S
        grad_lambda = (G_N_minus_1 / G_N) * w(:)' ./ mu_prev; % ∂λ/∂μ_i
        grad_S = c .* alpha .* mu_prev.^(alpha - 1);         % ∂S/∂μ_i

        % Множитель Лагранжа
        gamma = -sum(grad_lambda .* grad_S) / sum(grad_S.^2);

        % Обновление μ
        mu_new = mu_prev + gamma * grad_S;

        % Нормировка для S = S*
        scaling_factor = (S_star / sum(c .* mu_new.^alpha))^(1/alpha(1));
        mu_new = mu_new * scaling_factor;

        % Проверка сходимости
        if max(abs(mu_new - mu_prev)) < tolerance
            break;
        end
        mu_prev = mu_new;
    end
    mu = mu_prev;
end

function [t, Pi, G] = compute_marginal_distribution(w, mu, c, N, M, P)
    % Инициализация
    Pi = cell(M, 1);
    for i = 1:M
        Pi{i} = zeros(N+1, 1);
        Pi{i}(1) = 1; % P_i(0,0) = 1
    end
    t = zeros(M, 1);
    G = 0;

    for r = 1:N
        % Шаг 1: Вычисление t_i(r)
        for i = 1:M
            sum_t = 0;
            for n = 1:r
                m_i = c(i);
                mu_i_n = min(n, m_i) * mu(i);
                sum_t = sum_t + (n / mu_i_n) * Pi{i}(n);
            end
            t(i) = sum_t;
        end

        % Шаг 2: Вычисление lambda_1(r)
        lambda1 = r / sum(w .* t / w(1));

        % Шаг 3: Обновление P_i(n, r)
        for i = 1:M
            m_i = c(i);
            for n = 1:r
                mu_i_n = min(n, m_i) * mu(i);
                Pi{i}(n+1) = (w(i)/w(1)) * lambda1 / mu_i_n * Pi{i}(n);
            end
            Pi{i}(1) = 1 - sum(Pi{i}(2:r+1));
        end
    end

    % Нормировочная константа G(N)
    G = sum(cellfun(@(p) sum(p), Pi));
end

function lambda_max = calculate_lambda(w, mu, N, M, c, P)
    [~, ~, G_N] = compute_marginal_distribution(w, mu, c, N, M, P);
    [~, ~, G_N_minus_1] = compute_marginal_distribution(w, mu, c, N-1, M, P);
    lambda_max = (G_N_minus_1 / G_N) * sum(w .* mu(:));
end

function check_optimality(w, mu, c, alpha, S_star, N, M, P)
    % Проверка оптимальности
    mu_opt = mu;
    Nmean = findnmean(w, mu_opt, c, N, M, P);
    Nmean1 = findnmean(w, mu_opt, c, N-1, M, P);
    mu_new = findmu(S_star, Nmean, Nmean1, c, alpha, w);

    err = max(abs(mu_new - mu_opt));
    fprintf('\nПроверка оптимальности:\n');
    fprintf('Ошибка μ: %.2e\n', err);

    if err < 1e-5
        disp('Результат корректен: ошибка близка к нулю.');
    else
        disp('Внимание: требуется дополнительная проверка.');
    end
end

function Nmean = findnmean(w, mu, c, N, M, P)
    [~, ~, G_N] = compute_marginal_distribution(w, mu, c, N, M, P);
    [~, ~, G_N_minus_1] = compute_marginal_distribution(w, mu, c, N-1, M, P);
    Nmean = (G_N_minus_1 / G_N) * sum(w .* mu(:));
end

function mu_new = findmu(S, Nmean, Nmean1, c, alpha, w)
    delta = Nmean - Nmean1;
    if abs(delta(1)) < 1e-6
        mu_new = (S / sum(c)) * ones(size(c));
        return;
    end
    mu_new = (S ./ (c .* alpha .* delta)).^(1./alpha);
    mu_new = mu_new / sum(c .* mu_new.^alpha)^(1/alpha(1));
end
