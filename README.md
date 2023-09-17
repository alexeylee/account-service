# account-service
Реализация [тестового задания](etc/test_task.pdf) на создание условного сервиса для работы с учётными записями клиентов.

## Используемый стек
- Java 17
- Spring Boot 2.7.15
- PostgreSQL 13.8

## Запуск сервиса
1. Скачать архив с проектом, разархивировать у себя локально
2. Из корневой папки проекта выполнить команду:
   ```
   docker-compose up
   ```
3. Сервис поднимается на порту 8088. После запуска сервиса проверить его состояние, выполнив запрос:
   ```
   curl --location 'http://localhost:8088/account-service/api/actuator/health'
   ```

## Описание API
Сервис предоставляет эндпойнты:
- ```POST /account-service/api/v1/accounts``` - создание учётной записи.
- ```GET /account-service/api/v1/accounts/{id}``` - получение учётной записи по идентификатору. Идентификатором является UUID.
- ```GET /account-service/api/v1/accounts``` - получение списка учётных записей по переданным параметрам. Должен быть передан хотя бы один параметр для поиска.

## Примеры использования API
Cоздание учётной записи
```
curl --location 'http://localhost:8088/account-service/api/v1/accounts' \
--header 'x-Source: gosuslugi' \
--header 'Content-Type: application/json' \
--data-raw '{
    "bankId": "dc864033-c163-4b5a-8ae3-18a2726871e9",
    "lastName": "Петров",
    "firstName": "Иван",
    "middleName": "Иванович",
    "birthDate": "1980-08-05",
    "passportNumber": "7600 552552",
    "birthPlace": "г. Москва",
    "phone": "79998887760",
    "email": "mail@mail.ru",
    "registrationAddress": "г. Москва",
    "actualAddress": "г. Москва"
}'
```
Получение учётной записи по идентификатору
```
curl --location 'http://localhost:8088/account-service/api/v1/accounts/552b84d4-c94b-434d-bb50-ebbbb68c7869'
```
Получение списка учётных записей по переданным параметрам
```
curl --location 'http://localhost:8080/account-service/api/v1/accounts?lastName=Петров&firstName=Иван
```
## Замечания по реализации
1. При появлении новых приложений со своими правилами валидации, новый валидатор добавляется в два шага:
   - создать [класс](src/main/java/com/litvintsev/accounts/model/source), в котором описан набор полей и правила валидации
   - в [файле настроек](src/main/resources/application.yml) приложения прописать значение нового http заголовка и название класса-валидатора для сообщений с этим заголовком
2. Если на эндпойнт для создания учётной записи не передан заголовок "x-Source" или передано неверное значение заголовка, возвращается ошибка.
3. Сделано допущение, что учётные записи не должны дублироваться. Так как это условие не было прописано явно, предполагаю, что уникальными полями должны быть: bankId, номер паспорта, номер телефона, email. Исходя из реального опыта, допущено, что сочетание "фамилия, имя, отчество, дата рождения, место рождения" не является уникальным. Перед записью учётной записи в БД, делается запрос на поиск такой же записи в таблице. Кроме того, для "физического" предотвращения появления дубликатов, в БД созданы уникальные индексы по указанным полям.
5. Для упрощения, поля учётной записи хранятся в одной таблице, без разделения на дополнительный таблицы (например, "контакты", "адреса", "документы").
     
   
