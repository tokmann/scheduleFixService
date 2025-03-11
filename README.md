# 🚀 **ScheduleFixService**

**ScheduleFixService** — это сервис для асинхронной обработки задач, связанных с анализом и улучшением расписаний. Он позволяет пользователям отправлять запросы на обработку данных, отслеживать их статус и получать результаты по завершении. Всё это происходит без использования базы данных и с простым API для взаимодействия.
- Перерывы между парами ("окна").
- Поздние пары
- Неудобные места в расписании

🔍 API позволяет получать данные о найденных проблемах с возможностью фильтрации по названию группы, ФИО преподавателя или для всего МИРЭА сразу.

## 📑 **Содержание**

- [Описание](#описание)
- [Технологии](#технологии)
- [Установка](#установка)
- [Структура проекта](#структура-проекта)
- [API](#api)
- [Состояние задач](#состояние-задач)
- [Лицензия](#лицензия)

## ✨ **Описание**

🔹 **Основные особенности**:
- Асинхронная обработка задач.
- Получение статуса задачи по `taskId`.
- Получение результата задачи по `taskId` после её завершения.

## 🛠️ **Технологии**

Проект использует следующие технологии:

- **Java 23** — основная версия языка программирования.
- **Spring Boot** — для создания REST API и обработки запросов.
- **Lombok** — для упрощения работы с моделями данных.
- **Docker** — для контейнеризации и упрощения запуска приложения.
- **Maven** — для сборки и управления зависимостями.

## 💻 **Установка**

1. **Клонируйте репозиторий:**
   ```bash
   git clone https://github.com/tokmann/scheduleFixService.git
   cd scheduleFixService
   ```

2. Используйте Docker для запуска:
    - Соберите и запустите контейнеры с помощью Docker Compose:
   ```bash
   docker-compose up --build
   ```

3. **Приложение доступно** по адресу:  
   http://localhost:8080/api/schedule-bad-spaces/



## 📂 **Структура проекта**

```
/src
    /main
        /java
            /app
                /controller     # Контроллеры для обработки запросов
                /service        # Сервисы для бизнес-логики
                /model          # Модели данных
                /utils          # Вспомогательные классы
        /resources
              application.properties   # Настройки приложения
               logback-spring.xml # Настройка логирования
```

## 🛠️ **API**

### 📊 **/match-async/{criteria}**
- **Метод**: `POST`
- **Описание**: Поиск неудобных мест по определенному параметру, 
  получение идентификатора задачи `taskId`.

### 📊 **/find-all-async**
- **Метод**: `POST`
- **Описание**: Поиск неудобных мест по всему расписанию, 
  получение идентификатора задачи `taskId`.

### 🎯 **/status/{taskId}**
- **Метод**: `GET`
- **Описание**: Получить статус или результат задачи по `taskId` после завершения обработки.

## 📊 **Состояние задач**

Задачи могут находиться в следующих состояниях:
- `Task not found` — задача по ID не найдена.
- `Status: in progress` — задача обрабатывается.
- `Status: completed` - задача обработана, возврат найденных данных.
- `Error processing task` — ошибка при обработке задачи.

Каждая задача идентифицируется уникальным `taskId`, который используется для отслеживания её статуса и получения результатов.

## 📜 **Лицензия**
MIT License. Используйте свободно! 🎉
