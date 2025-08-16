
;;;======================================================
;;;   Automotive Expert System
;;;
;;;     This expert system diagnoses some simple
;;;     problems with a website.
;;;
;;;     CLIPS Version 6.4 Example
;;;
;;;     To execute, merely load, reset and run.
;;;======================================================

;;****************
;;* DEFFUNCTIONS *
;;****************

;; Функция вывода вопросов
(deffunction ask-question (?question $?allowed-values)
   (print ?question)
   (bind ?answer (read))
   (if (lexemep ?answer) 
       then (bind ?answer (lowcase ?answer)))
   (while (not (member$ ?answer ?allowed-values)) do
      (print ?question)
      (bind ?answer (read))
      (if (lexemep ?answer) 
          then (bind ?answer (lowcase ?answer))))
   ?answer)

;; Функция обработки ответа типа "да/нет"
(deffunction yes-or-no-p (?question)
   (bind ?response (ask-question ?question yes no y n))
   (if (or (eq ?response yes) (eq ?response y))
       then yes 
       else no))

;;;***************
;;;* QUERY RULES *
;;;***************

;; Определение наличия ответа от сервера 
(defrule determine-ping-state ""
   (not (domain-pings ?))
   (not (repair ?))
   =>
   (assert (domain-pings (yes-or-no-p "Приходит ли ответ от сервера на запрос по домену сайта утилитой ping? (yes/no) "))))
   
;; Определение состояния сервера
(defrule determine-server-state ""
   (domain-pings no)
   (not (repair ?))
   =>
   (assert (server-on (yes-or-no-p "Сервер с сайтом включен? (yes/no) "))))
   
;; Определение наличия проблем с ssl
(defrule determine-certificate-state ""
   (domain-pings yes)
   (not (repair ?))
   =>
   (assert (certificate-expired (yes-or-no-p "Появляется ли предупреждение о незащищенном подключении при попытке открыть сайт? (yes/no) "))))
   
;; Определение состояния веб-сервера
(defrule determine-webserver-runs ""
   (and (domain-pings yes)      
		(certificate-expired no))
   (domain-pings yes)
   (not (repair ?))
   =>
   (assert (webserver-runs (yes-or-no-p "Выходит ли код ошибки веб-сервера (404, 503...) при открытии сайта в браузере? (yes/no) "))))
   
;; Определение кодовой группы ошибки
(defrule determine-webserver-code-state ""
   (and (domain-pings yes)      
		(certificate-expired no)
		(webserver-runs yes))
   (not (repair ?))
   =>
   (assert (webserver-code-state
      (ask-question "Какая первая цифра ошибки? (1/2/3/4/5)"
                    1 2 3 4 5))))

;;;****************
;;;* REPAIR RULES *
;;;****************

;; Рекомендация при включенном, но недоступном из интернета сервере
(defrule server-on-state-conclusions ""
   (server-on yes)
   (not (repair ?))
   =>
   (assert (repair "Проверьте доступ сервера к сети интернет")))

;; Рекомендация при выключенном сервере
(defrule server-off-state-conclusions ""
   (server-on no)
   (not (repair ?))
   =>
   (assert (repair "Включите сервер. В случае возникновения ошибок проверьте логи запуска операционной системы")))

;; Рекомендация при ошибке сертификата
(defrule certificate-state-conclusions ""
   (certificate-expired yes)
   (not (repair ?))
   =>
   (assert (repair "Срок действия ssl-сертификата истек, продлите существующий сертификат или выпустите новый")))

;; Рекомендация при отсутствии ошибок от веб-сервера
(defrule webserver-runs-conclusions ""
   (webserver-runs no)
   (not (repair ?))
   =>
   (assert (repair "Веб-сервер не запущен. Попробуйте перезапустить сервисы nginx/apache на сервере и проверьте их логи")))

;; Рекомендация для кодовой группы ошибок 1XX
(defrule webserver-code-state-1 ""
   (webserver-code-state 1)
   (not (repair ?))
   =>
   (assert (repair "Информационные коды ответа веб-серверов 100-199 — появляются при ошибке сети на стороне устройства пользователя. Убедитесь в том, что введен правильный url сайта, а также, что сетевые и браузерные настройки пользователя не препятствуют подключению")))

;; Рекомендация для кодовой группы ошибок 2XX
(defrule webserver-code-state-2 ""
   (webserver-code-state 2)
   (not (repair ?))
   =>
   (assert (repair "Успешные коды ответа веб-серверов 200-299 — говорят о том, что запрос обработан и информация передана браузеру. При проблеме отображения сайта необходимо удостовериться в корректности передаваемых сервером данных")))

;; Рекомендация для кодовой группы ошибок 3XX
(defrule webserver-code-state-3 ""
   (webserver-code-state 3)
   (not (repair ?))
   =>
   (assert (repair "Коды ответа веб-серверов перенаправления 300-399 — подразумевают собой, что браузер получает не то, что запрашивал пользователь.Проверьте правильность настройки перенаправлений сайта")))

;; Рекомендация для кодовой группы ошибок 4XX
(defrule webserver-code-state-4 ""
   (webserver-code-state 4)
   (not (repair ?))
   =>
   (assert (repair "Ошибка на стороне клиента 400-499. Убедитесь, что запрос клиента имеет правильный формат, а также, что у клиента есть все необходимые права доступа к запрашиваемым ресурсам")))

;; Рекомендация для кодовой группы ошибок 5XX
(defrule webserver-code-state-5 ""
   (webserver-code-state 5)
   (not (repair ?))
   =>
   (assert (repair "Ошибка на стороне сервера 500-599. Проверьте логи веб-сервера, при необходимости следует ослабить ограничения веб-сервера на обработку запросов клиента. Если ресурсы сервера предельно загружены, перезагрузите сервер")))

;;;********************************
;;;* STARTUP AND CONCLUSION RULES *
;;;********************************

;; Вывод приветствия при запуске экспертной системы
(defrule system-banner ""
  (declare (salience 10))
  =>
  (println crlf "The Website Down Expert System" crlf))

;; Вывод итоговой рекомендации
(defrule print-repair ""
  (declare (salience 10))
  (repair ?item)
  =>
  (println crlf "Совет экспертной системы по восстановлению работоспособности сайта:" crlf)
  (println " " ?item crlf))
