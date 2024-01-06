CREATE TABLE Blossom_Refresh_Token (
    id SERIAL PRIMARY KEY,
    user_id INT,
    token VARCHAR(255) NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Blossom_User (id)
);

CREATE TABLE Blossom_Verification_Token (
    user_id INT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Blossom_User (id)
);