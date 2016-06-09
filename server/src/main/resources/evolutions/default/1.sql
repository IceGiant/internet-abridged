# Links schema

# --- !Ups

CREATE TABLE LINKSTABLE (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sourceId VARCHAR(255),
    title VARCHAR(2000),
    href VARCHAR(2000)
);

# --- !Downs

DROP TABLE LINKSTABLE;