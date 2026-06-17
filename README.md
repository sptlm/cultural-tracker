# Cultural Navigator

Cultural Navigator - MPA-приложение на Spring Boot MVC для поиска культурных событий, площадок и пользовательских маршрутов по Казани. Проект помогает находить события по интересам, району, бюджету и времени, сохранять понравившиеся мероприятия и собирать из них культурные маршруты.

Подробные требования, описание архитектуры, сущностей и сценариев можно посмотреть в [требования.md](%D1%82%D1%80%D0%B5%D0%B1%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D1%8F.md).

## Возможности

- Просмотр главной страницы с популярными событиями и публичными маршрутами.
- Каталог событий с поиском, фильтрацией по категориям, районам, цене и времени проведения.
- Карточки событий с описанием, площадкой, отзывами и оценками.
- Каталог культурных площадок с адресами, районами и географическими координатами.
- Регистрация, вход, личный профиль и настройка пользовательских предпочтений.
- Добавление событий в избранное.
- Создание, редактирование, удаление и публикация пользовательских маршрутов.
- Расчет параметров маршрута: длительность, бюджет, расстояние и состав событий.
- Административная панель для управления событиями, площадками, категориями, районами, отзывами и маршрутами.
- REST API для работы с маршрутами и AJAX-эндпоинты для поиска, избранного, отзывов и проверки данных регистрации.
- Интеграция с Yandex Geocoder API для получения координат по адресу.

## Технический стек

- Java 21
- Spring Boot 3.3.5
- Spring MVC, Spring Security, Spring Validation
- Spring Data JPA, Hibernate
- PostgreSQL 16
- Flyway
- Redis и Spring Cache
- FreeMarker
- OpenAPI Generator, Springdoc OpenAPI UI
- OkHttp
- Gradle Kotlin DSL
- Docker и Docker Compose

## Структура проекта

- `src/main/java/com/culturalnavigator` - Java-код приложения.
- `src/main/resources/templates` - FreeMarker-шаблоны страниц.
- `src/main/resources/static` - CSS и JavaScript.
- `src/main/resources/db/migration` - Flyway-миграции схемы и seed-данных.
- `src/main/resources/api.yaml` - OpenAPI-спецификация Routes API.
- `docker-compose.yml` - запуск приложения, PostgreSQL и Redis.

## Переменные окружения

Пример находится в `.env.example`.

| Переменная | Назначение | Значение по умолчанию |
| --- | --- | --- |
| `DB_URL` | JDBC URL PostgreSQL для локального запуска | `jdbc:postgresql://localhost:5432/cultural_navigator` |
| `DB_USER` | пользователь PostgreSQL | `cultural_user` |
| `DB_PASSWORD` | пароль PostgreSQL | `cultural_password` |
| `REDIS_HOST` | хост Redis | `localhost` |
| `REDIS_PORT` | порт Redis | `6379` |
| `APP_PORT` | порт приложения внутри контейнера | `8080` |
| `APP_HOST_PORT` | порт приложения на хосте при Docker-запуске | `8080` |
| `SERVER_SERVLET_CONTEXT_PATH` | context path приложения | `/cultural` |
| `YANDEX_GEOCODER_API_KEY` | ключ Yandex Geocoder API | пусто |

## Запуск через Docker

Требования: Docker и Docker Compose.

1. Скопируйте `.env.example` в `.env`:

   ```bash
   cp .env.example .env
   ```

   На Windows PowerShell:

   ```powershell
   Copy-Item .env.example .env
   ```

2. При необходимости заполните `YANDEX_GEOCODER_API_KEY` в `.env`.

3. Создайте внешнюю Docker-сеть, если ее еще нет:

   ```bash
   docker network create web_network
   ```

4. Соберите и запустите контейнеры:

   ```bash
   docker compose up --build
   ```

5. Откройте приложение:

   ```text
   http://localhost:8080/cultural
   ```

Контейнер приложения собирает jar самостоятельно через Gradle. PostgreSQL будет доступен на хосте по порту `5433`, Redis используется внутри Docker-сети.

## Локальный запуск без Docker

Требования: Java 21, PostgreSQL, Redis.

1. Создайте пользователя и базу данных PostgreSQL:

   ```sql
   CREATE USER cultural_user WITH PASSWORD 'cultural_password';
   CREATE DATABASE cultural_navigator OWNER cultural_user;
   ```

2. Запустите Redis на `localhost:6379`.

3. При необходимости задайте переменные окружения из `.env.example`. Если используются значения по умолчанию из `application.yml`, дополнительная настройка не нужна.

4. Запустите приложение:

   ```bash
   ./gradlew bootRun
   ```

   На Windows:

   ```powershell
   .\gradlew.bat bootRun
   ```

5. Откройте:

   ```text
   http://localhost:8080/cultural
   ```

Flyway применит миграции автоматически при старте приложения.

## Полезные команды

Сборка jar:

```bash
./gradlew bootJar
```

Запуск тестов:

```bash
./gradlew test
```

Остановка Docker-контейнеров:

```bash
docker compose down
```

Остановка контейнеров с удалением volume PostgreSQL:

```bash
docker compose down -v
```

## Доступы и адреса

- Приложение: `http://localhost:8080/cultural`
- Swagger UI: `http://localhost:8080/cultural/swagger-ui/index.html`
- Routes API: `http://localhost:8080/cultural/api/routes`
- Администратор из seed-миграции: `admin / admin`
- Пароль seed-пользователей: `admin`
