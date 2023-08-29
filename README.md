# :handshake: Explore with me
_Дипломный проект._

### :question: Что это?
Бэкенд приложения-афиши для публикации и поиска событий.

Свободное время — ценный ресурс. Ежедневно мы планируем, как его потратить — куда и с кем сходить. 
Сложнее всего в таком планировании поиск информации и переговоры. 
Нужно учесть много деталей: какие намечаются мероприятия, свободны ли в этот момент друзья, как всех пригласить и где собраться.

Приложение состоит из двух микросервисов и двух БД (для каждого из сервисов):
#### :ferris_wheel: Main
Основной сервис, отвечающий за бизнес-логику приложения.
API основного сервиса состоит из трех частей:
* публичная доступна без регистрации любому пользователю сети;
* закрытая доступна только авторизованным пользователям;
* административная — для администраторов сервиса.

#### :computer: Stats
Сервер статистики, отвечающий за сохранение и получение статистики просмотров. \
Модуль статистики состоит из трех подмодулей:
1. Основной сервис статистики
2. Библиотека клиента сервиса статистики для отправки и получения статистики 
3. Библиотека DTO

_Схема модулей приложения:_
![Схема модулей приложения](https://github.com/JackobTheLion/java-explore-with-me/blob/main/info/Explore%20with%20me.jpg)

___

### :star: Возможности приложения:
#### Публичный доступ:
* Поиск событий по критериям (дата, категория события, место проведения и т.д.)
* Просмотр существующих категорий событий
* Просмотр подборок, созданных администратором

#### Авторизованные пользователи
* Поиск событий
* Добавление и редактирование событий
* Подача заявок на участие в событиях других пользователей
* Просмотр и модерация заявок, поданных на участие в своих событиях

#### Администратор
* Создание, редактирование и удаление пользователей
* Создание, редактирование и удаление категорий
* Создание, редактирование и удаление подборок событий
* Поиск и редактирование событий (включая публикацию)

#### Фича приложения для самостоятельной разработки
Возможность для администратора добавлять конкретные локации — города, театры, концертные залы и другие в виде координат (широта, долгота, радиус). 
Получение списка этих локаций. Возможность поиска событий в конкретной локации.

___
### :globe_with_meridians: Описание API приложения:
Описание API сервера [здесь](api). \
Тесты Postman [здесь](postman).
___
### :gear: Технологический стек
* Java 11
* Spring Boot
* Hibernate (main service)
* JDBC (stats service)
* Junit
* Mockito
* Lombok
* PostgresSQL
* Maven
* Docker
___
### :page_with_curl: Инструкция по запуску:
1. Скачать проект
2. По умолчанию приложение работает на порту 8080 (основной сервис) и 9090 (сервис статистики). \
docker-compose по-умолчанию:
``` dockerfile
version: '3.1'
services:
  stats-server:
    build: explore-stats/explore-stats-service
    image: explore-stats-service
    container_name: explore-stats-service
    ports:
      - "9090:9090"
    depends_on:
      - stats-db
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://stats-db:5432/explorestats
      - POSTGRES_USER=root
      - POSTGRES_PASSWORD=root

  stats-db:
    image: postgres:14-alpine
    container_name: explore-stats-db
    ports:
      - "6543:5432"
    environment:
      - POSTGRES_DB=explorestats
      - POSTGRES_USER=root
      - POSTGRES_PASSWORD=root

  ewm-service:
    build: explore-service
    image: explore-main
    container_name: explore-main
    ports:
      - "8080:8080"
    depends_on:
      - main-db
      - stats-server
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://main-db:5432/exploremain
      - POSTGRES_USER=root
      - POSTGRES_PASSWORD=root
      - APP_NAME=main-app
      - STAT_SERVER_URL=http://stats-server:9090

  main-db:
    image: postgres:14-alpine
    container_name: explore-main-db
    ports:
      - "6544:5432"
    environment:
      - POSTGRES_DB=exploremain
      - POSTGRES_USER=root
      - POSTGRES_PASSWORD=root
```
3. Собрать проект:
```shell
docker compose build
```
4. Запустить проект:
```shell
docker compose up
```
___
### :man_technologist: TODO
- [ ] добавить Geohash
- [ ] добавить сервис авторизации
- [ ] добавить Fabric8 для корректной работы интеграционных тестов 
- [ ] добавить возможность прикреплять афишу или фото
