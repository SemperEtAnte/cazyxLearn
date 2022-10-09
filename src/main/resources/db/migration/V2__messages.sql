/* Таблица сообщений */
CREATE TABLE IF NOT EXISTS learn_user_messages
(
    id        SERIAL8 PRIMARY KEY, /* ИД сообщений первичный ключ*/
    author_id int8 NOT NULL REFERENCES learn_users (id) ON DELETE CASCADE ON UPDATE CASCADE, /* Подвязка к автору сообщения*/
    message   TEXT NOT NULL, /* Текст сообщения */
    sent_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP /* Дата отправки сообщения */
);
/* Индекс на пользователя для ускорения запросов JOIN */
CREATE INDEX IF NOT EXISTS learn_user_messages_author_idx ON learn_user_messages (author_id);

/* Индекс на время отправки для сортировки. DESC - означает что индексы будут упорядочиваться снизу-вверх*/
CREATE INDEX IF NOT EXISTS learn_user_messages_sent_idx ON learn_user_messages (sent_at DESC);
