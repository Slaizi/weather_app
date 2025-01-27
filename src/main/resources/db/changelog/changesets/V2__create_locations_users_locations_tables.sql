CREATE TABLE IF NOT EXISTS locations
    (
        id BIGSERIAL,
        country TEXT NOT NULL,
        city_name TEXT NOT NULL,
        latitude DOUBLE PRECISION NOT NULL,
        longitude DOUBLE PRECISION NOT NULL,
        local_names JSONB,
        PRIMARY KEY (id),
        UNIQUE (country, city_name)
    );

CREATE TABLE IF NOT EXISTS users_locations
    (
        user_id BIGINT NOT NULL,
        location_id BIGINT NOT NULL,
        PRIMARY KEY (user_id, location_id),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE CASCADE ON UPDATE CASCADE
    );