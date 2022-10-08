CREATE TABLE IF NOT EXISTS learn_user_messages
(
    id        SERIAL8 PRIMARY KEY,
    author_id int8 NOT NULL REFERENCES learn_users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    message   TEXT NOT NULL,
    sent_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS learn_user_messages_author_idx ON learn_user_messages (author_id);
CREATE INDEX IF NOT EXISTS learn_user_messages_sent_idx ON learn_user_messages (sent_at DESC);
