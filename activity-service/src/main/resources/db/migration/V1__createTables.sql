create table interaction (
	id SERIAL PRIMARY KEY,
	user_id INT,
    post_id VARCHAR(255),
    interaction_type VARCHAR(255) NOT NULL
);

CREATE TABLE comment (
    id SERIAL PRIMARY KEY,
    user_id INT,
    post_id VARCHAR(255),
    parent_comment_id INT REFERENCES comment(id),
    comment_content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOL DEFAULT FALSE,
    FOREIGN KEY (parent_comment_id) REFERENCES comment(id)
);
