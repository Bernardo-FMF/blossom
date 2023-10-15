CREATE TABLE Blossom_Chat_User (
    id INT PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    full_name VARCHAR(255),
    image_url VARCHAR(255)
);

CREATE TABLE Blossom_Chat (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    owner_id INT REFERENCES Blossom_Chat_User(id),
    chat_type VARCHAR(255),
    last_update TIMESTAMP
);

CREATE TABLE Blossom_Chat_Participants (
    chat_id INT REFERENCES Blossom_Chat(id),
    user_id INT REFERENCES Blossom_Chat_User(id),
    PRIMARY KEY (chat_id, user_id)
);

CREATE TABLE Blossom_Message (
    id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    chat_id INT REFERENCES Blossom_Chat(id),
    sender_id INT REFERENCES Blossom_Chat_User(id),
    chat_type VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOL DEFAULT FALSE
);