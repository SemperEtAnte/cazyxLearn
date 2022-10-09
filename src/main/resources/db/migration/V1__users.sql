/* Создаем таблицу пользователей */
CREATE TABLE IF NOT EXISTS learn_users
(
    id              SERIAL8 PRIMARY KEY, /* Первичный ключ ID serial8 == bigint auto_increment */
    login           VARCHAR(16) NOT NULL, /* Логин, не более 16 символов */
    email           TEXT NOT NULL, /* Почта */
    password_digest TEXT NOT NULL, /* Хэш пароля */
    role            TEXT NOT NULL DEFAULT 'USER', /* Роль в системе. По-умолчанию юзер */
    registered_at   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP /* Дата регистрации */
);
/* Уникальный индекс на логин, без учета регистра */
CREATE UNIQUE INDEX IF NOT EXISTS learn_users_login_idx ON learn_users (LOWER(login));
/* Уникальный индекс на почту, без учета регистра */
CREATE UNIQUE INDEX IF NOT EXISTS learn_users_email_idx ON learn_users (LOWER(email));

/* Таблица с токенами для перевыпуска JWT */
CREATE TABLE IF NOT EXISTS learn_refresh_tokens
(
    token      TEXT PRIMARY KEY, /* Сам токен перевыпуска идет как первичный ключ (нет смысла делать ИД, т.к. зависимостей нет) */
    user_id    int8 NOT NULL REFERENCES learn_users (id) ON DELETE CASCADE ON UPDATE CASCADE, /* Пользователь к которому подвязывается токен*/
    expires_at TIMESTAMP NOT NULL /* Время истечения жизни токена*/
);

/* Индекс на зависимое поле для ускорения JOIN запросов */
CREATE INDEX IF NOT EXISTS learn_reset_tokens_user_idx ON learn_refresh_tokens (user_id);
/* Индекс на поле истечения для запросов удаления истекших */
CREATE INDEX IF NOT EXISTS learn_reset_tokens_expires_idx ON learn_refresh_tokens (expires_at);
