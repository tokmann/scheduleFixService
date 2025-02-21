# 📅 Schedule Fix Fix API

## 🚀 Описание
Schedule Fix Service API — это сервис, который анализирует расписание занятий и выявляет неудобства в нём. Например:
- Перерывы между парами ("окна").
- Поздние пары
- Неудобные места в расписании

🔍 API позволяет получать данные о найденных проблемах с возможностью фильтрации по названию группы, ФИО преподавателя или для всего МИРЭА сразу.

---


## 🛠 Требования
Для запуска проекта вам понадобятся:
- Если хотите запустить с помощью Docker:
- **Docker**
- Если хотите запустить без Docker:
- **Maven**
- **Java 23**
- **Git**

---

## 🔧 Установка и запуск
### 📌 Запуск без Docker
```sh
# Клонируем репозиторий
git clone https://github.com/tokmann/ScheduleFixService.git
cd ScheduleFixService

# Сборка проекта
mvn clean install

# Запуск приложения
java -jar target/ScheduleFixService-1.0.jar
```
Приложение будет доступно на `http://localhost:8080`.

---

### 🐳 Запуск в Docker
```sh
# Загрузите Docker-образ приложения из Docker Hub:
docker pull tokmann/schedulefixservice-app:1.0

# Запустите контейнер с приложением:
docker run -p 8080:8080 tokmann/schedulefixservice-app:1.0

# API приложение доступно по адресу:
http://localhost:8080
```

Приложение запустится в контейнере, и API будет доступен по адресу `http://localhost:8080`.

---

## 🔗 API эндпоинты
| Метод | URL                                  | Описание                           |
|--------|--------------------------------------|------------------------------------|
| GET | `/api/schedule-bad-spaces/all`       | Получить список всех "плохих мест" |
| GET | `/api/schedule-bad-spaces/match/XXX` | Фильтр по группе                   |
| GET | `/api/schedule-bad-spaces/match/YYY` | Фильтр по преподавателю            |

---

## 📜 Лицензия
MIT License. Используйте свободно! 🎉

