CREATE TABLE Blossom_Password_Reset (
    user_id INT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Blossom_User (id)
);