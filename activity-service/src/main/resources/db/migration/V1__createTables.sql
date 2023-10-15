CREATE TABLE Blossom_Local_User (
    user_id INT PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    full_name VARCHAR(255),
    image_url VARCHAR(255)
);

create table Blossom_Interaction (
	id SERIAL PRIMARY KEY,
	user_id INT REFERENCES Blossom_Local_User(id),
    post_id VARCHAR(255),
    interaction_type VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Blossom_Comment (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES Blossom_Local_User(id),
    post_id VARCHAR(255),
    parent_comment_id INT REFERENCES Blossom_Comment(id),
    top_level_comment_id INT REFERENCES Blossom_Comment(id),
    comment_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOL DEFAULT FALSE
);