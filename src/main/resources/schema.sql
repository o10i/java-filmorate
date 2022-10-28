CREATE TABLE user
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    TEXT NOT NULL,
    login    TEXT NOT NULL,
    name     TEXT,
    birthday TIMESTAMP,
    CONSTRAINT login_space
        CHECK (login NOT LIKE '%\s%'),
    CONSTRAINT email_at
        CHECK (email LIKE '%@%')
);

CREATE TABLE film
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         TEXT NOT NULL,
    description  VARCHAR(200),
    release_date TIMESTAMP,
    duration     BIGINT,
    rating       TEXT NOT NULL,
    CONSTRAINT release_date_check
        CHECK (CAST(release_date AS date) >= CAST('28.12.1895' AS date)),
    CONSTRAINT positive_duration
        CHECK (duration > 0),
    CONSTRAINT fk_film_rating
        FOREIGN KEY (rating)
            REFERENCES rating (name)
);

CREATE TABLE friendship
(
    user_id   BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    status    BOOL,
    CONSTRAINT fk_friendship_user_id
        FOREIGN KEY (user_id)
            REFERENCES user (id),
    CONSTRAINT fk_friendship_friend_id
        FOREIGN KEY (friend_id)
            REFERENCES user (id)
);

CREATE TABLE "like"
(
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_like_film_id
        FOREIGN KEY (film_id)
            REFERENCES film (id),
    CONSTRAINT fk_like_user_id
        FOREIGN KEY (user_id)
            REFERENCES user (id)
);

CREATE TABLE film_genre
(
    film_id    BIGINT NOT NULL,
    genre_name TEXT   NOT NULL,
    CONSTRAINT fk_film_genre_film_id
        FOREIGN KEY (film_id)
            REFERENCES film (id),
    CONSTRAINT fk_genre_name_id
        FOREIGN KEY (genre_name)
            REFERENCES genre (name)
);

CREATE TABLE rating
(
    name TEXT PRIMARY KEY
);

CREATE TABLE GENRE
(
    name TEXT PRIMARY KEY
);
