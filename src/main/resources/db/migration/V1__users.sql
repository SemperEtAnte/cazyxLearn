CREATE TABLE IF NOT EXISTS learn_users
(
    id              SERIAL8 PRIMARY KEY,
    login           VARCHAR(16) NOT NULL,
    email           TEXT NOT NULL,
    password_digest TEXT NOT NULL,
    role            TEXT NOT NULL DEFAULT 'USER',
    registered_at   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS learn_users_login_idx ON learn_users (LOWER(login));
CREATE UNIQUE INDEX IF NOT EXISTS learn_users_email_idx ON learn_users (LOWER(email));

CREATE TABLE IF NOT EXISTS learn_refresh_tokens
(
    token      TEXT PRIMARY KEY,
    user_id    int8 NOT NULL REFERENCES learn_users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    expires_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS learn_reset_tokens_user_idx ON learn_refresh_tokens (user_id);
CREATE INDEX IF NOT EXISTS learn_reset_tokens_expires_idx ON learn_refresh_tokens (expires_at);
