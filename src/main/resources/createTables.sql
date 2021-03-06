CREATE TABLE Mission (
    id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    name VARCHAR(255),
    target VARCHAR(255),
    from_date DATE,
    to_date DATE
);

CREATE TABLE Agent (
    id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    name VARCHAR(255),
    rating INT
);

CREATE TABLE Manager (
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    agent INT REFERENCES Agent(id),
    mission INT REFERENCES Mission(id)
);