CREATE TABLE Blossom_User (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    image_url VARCHAR(255),
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE Blossom_Role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE Blossom_User_Role (
    user_id INT REFERENCES Blossom_User(id),
    role_id INT REFERENCES Blossom_Role(id),
    PRIMARY KEY (user_id, role_id)
);