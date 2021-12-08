## Дипломная работа “Облачное хранилище”
![cloud](.//clouda.jpg)
### Описание проекта
Разработано приложение - REST-сервис. Сервис предоставляет REST интерфейс для возможности загрузки файлов и вывода списка уже загруженных файлов пользователя. Все запросы к сервису авторизованы. Заранее подготовленное веб-приложение (FRONT) подключается к разработанному сервису без доработок, а также использует функционал FRONT для авторизации, загрузки и вывода списка файлов пользователя.

### При создании приложения были использованы:

* Spring Boot
* Spring Data JPA
* В качестве базы данных - PostgreSQL
* Сборщик Maven
* Для запуска используется Docker compose
* Логирование с помощью Slf4j
* Liquibase для миграций
* JWT для генерации токенов


### Запуск сервера и базы данных:
* Чтобы запустить приложение, перейдите в корневой каталог приложения и выполните в терминале команду:
docker-compose up

* Тестовые данные для входа:
login: user, password: pass

### Запуск FRONT
1. Установить nodejs (версия не ниже 14.15.0) на компьютер следуя инструкции: https://nodejs.org/ru/download/
2. Перейти в папку FRONT приложения и все команды для запуска выполнять из нее.
3. Следуя описанию README.md FRONT проекта запустить nodejs приложение (npm install...)

* По-умолчанию FRONT запускается на порту 8080 и доступен по url в браузере http://localhost:8080
