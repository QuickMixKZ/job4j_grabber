CREATE TABLE post(
    id SERIAL PRIMARY KEY,
    name TEXT,
    text TEXT,
    link TEXT unique,
    created TIMESTAMP
)