DROP TABLE IF EXISTS posts;

CREATE TABLE users (
    id TEXT PRIMARY KEY,
    pk TEXT NOT NULL
);

CREATE TABLE messages (
    message TEXT,
    client_id TEXT NOT NULL,
    peer_id TEXT NOT NULL,
    time_stamp INT NOT NULL,
    message_id TEXT PRIMARY KEY
);