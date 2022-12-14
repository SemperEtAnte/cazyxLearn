# Учебный проект Spring Boot для CAZYX

## Основное описание проекта

В данном проекте написан простой REST API бэкенд, содержащий в себе минимальный набор механик используемых при разработке.

По сути, целью данного бэка является показать, что и как, и, возможно, послужить источником каких-то решений (прекрасно понимаю что запоминать такие вещи, как написание настроек SpringSecurity - сложнее чем скопировать уже имеющиеся и адаптировать под задачу).

В качестве примера на данном бэкенде рассмотрена система из 3-х моделей:

* Пользователь
* Сообщения пользователя
* Токены обновления авторизации пользователя (зачем - ниже расскажу)

Сам пользователь имеет стандартный набор полей и роль (USER, MODERATOR, ADMIN).
Для авторизации запросов используется JWT передаваемый в заголовке `Authorization`.

Т.к. эти JWT лучше делать кратковременными ("мало живущими") (5 минут в примере) для них так же существует т.н.
<b>Refresh Token</b> - более долгоживущий токен используемый для перевыпуска основного JWT. Эти токены хранятся в специальной таблице и удаляются при первом же использовании или вызове `logout`

Сообщения - простейшая модель, сделанная, по сути, просто чтобы чуть усложнить архитектуру, не более того.

Также (как было сказано ранее) в проекте присутствует система ролей:

* USER - обычный пользователь, может отправлять и просматривать сообщения
* MODERATOR - "модератор" системы, может всё то же, что и пользователь + редактировать сообщения
* ADMIN - "администратор" системы, может всё то же, что и модератор + удалять сообщения.

Очевидно, что роуты регистрации и авторизации являются открытыми для всех и не требует токена авторизации. Остальные роуты секции пользователей доступны все ролям.

В проекте также представлен пример кода валидации роли, в случае несоответствия требованиям, вылетает стандартная ошибка `403 Forbidden`

Архитектура строится при помощи `flyway` миграций - руками отправляемые SQL запросы.
Лично Я больше приветствую такой вариант, т.к. контроля за происходящим в архитектуре конечной БД куда больше, т.е. она будет иметь ровно такой вид, какой нам надо.

## Разворачивание проекта с 0

Возьмем за основу систему `Ubuntu 22.04 LTS` для того чтобы проект работал, нам нужно всего пару вещей:

1) JDK 17
2) PostgreSQL

### Установка JDK 17

Так как данная версия является LTS, она уже давно есть в репозитории openjdk.
Тут на ваш выбор, jdk обычный или headless версия (разница там в библиотеках идущих в комплекте, вроде графической и всё такое)

```
sudo apt-get update && sudo apt-get install openjdk-17-jdk -y
```

Проверяем что всё установилось:

`java -version`

Вывод будет похож на этот (за исключением, что у меня Oracle версия):

```
java version "17.0.2" 2022-01-18 LTS
Java(TM) SE Runtime Environment (build 17.0.2+8-LTS-86)
Java HotSpot(TM) 64-Bit Server VM (build 17.0.2+8-LTS-86, mixed mode, sharing)

```

В принципе, важно, что вывод есть и там видна версия 17.

### Установка Postgresql

Можно, конечно, установить из стандартной репозитории, но там как я помню староватый. Рассмотрим как установить актуальный (14 на данный момент)

`curl -fsSL https://www.postgresql.org/media/keys/ACCC4CF8.asc|sudo gpg --dearmor -o /etc/apt/trusted.gpg.d/postgresql.gpg`

`sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'`

`sudo apt  update`

`sudo apt install postgresql-14`

<b>Готово. Установили.</b>

### Настройка БД

Теперь необходимо создать БД и пользователя для проекта.

Подключаемся к БД:

`sudo -su postgres psql`

Создаем пользователя:

`CREATE USER имяПользователя WITH ENCRYPTED PASSWORD 'Пароль';`

Создаем БД:

`CREATE DATABASE имяБД`;

Выдаем созданному пользователю права к созданной БД:

`GRANT ALL PRIVILEGES ON DATABASE имяБД TO имяПользователя;`

Готово.

### Настройка проекта

Теперь можем склонировать проект в нужную нам папку

`git clone (ссылка этого проекта)`

Далее в файлике application.yml необходимо поменять данные для подключения к БД (смотрите комментарии) на свои созданные ранее

### Подключение в IDE

Не знаю как в Eclipse, в intellij просто открываем проект из исходного кода, указываю локацию файла `build.gradle` - готово. В принципе после этого команда старта внутри IDE должна работать, но рассмотрим как собрать и запустить проект из консоли (на сервере допустим):

### Сборка и запуск проекта

Если всё работает как надо, собираем проект командой (запускать в папке проекта):

`./gradlew clean --build-cache assemble --parallel`

И запускаем проект

`java -jar build/libs/learnProject-1.0.jar`

Готово, проект запущен. Открываем браузер, вводим `http://127.0.0.1:8080/api-docs` - попадаем в сваггер (документацию) бэкенда где можем тыкать роуты.

### Рекомендация

Для разворачивания на машине рекомендую не использовать исходники, а закидывать туда готовый собранный .jar файл и рядом с ним класть `application.yml` настроенный под нужды уже конечного сервера.

Spring Boot сначала проверяет `application.yml` в директории откуда произведен запуск (как правило, это директория где лежит .jar) и если там его не находит, тогда уже лезет внутрь .jar файла (где лежат стандартные настройки из исходников)

### **Поздравляю проект собран и запущен. Остальные комментарии в формате javadoc лежат внутри кода.**

Если же javadoc по какой-то причине читать неудобно внутри кода

`./gradlew javadoc`

Внутри папки `build` появится директория `docs`, открываем в браузере `index.html` из неё и смотрим документацию в обычном java стиле.