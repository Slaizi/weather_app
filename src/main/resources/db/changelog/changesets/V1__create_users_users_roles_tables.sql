CREATE TABLE IF NOT EXISTS users
    (
        id BIGSERIAL,
        email VARCHAR(255) NOT NULL,
        password VARCHAR(255) NOT NULL,
        register_date TIMESTAMP DEFAULT current_timestamp,
        PRIMARY KEY (id), 
	    UNIQUE (email),
        CHECK (length(password) >= 5)
    );

CREATE TABLE IF NOT EXISTS users_roles 
    (
        user_id BIGINT NOT NULL,
        role VARCHAR(10) NOT NULL,
        PRIMARY KEY (user_id, role),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
        CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
    );