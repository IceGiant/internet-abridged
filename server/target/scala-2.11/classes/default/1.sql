# Tasks schema

# --- !Ups

CREATE TABLE Links (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sourceId VARCHAR(255) NOT NULL,
    title VARCHAR(2000) NOT NULL,
    href VARCHAR(2000) NOT NULL,
    published TIMESTAMP
);

INSERT INTO Links(sourceId, title, href) VALUES('Reddit', 'Testing now!', 'http://reddit.com');

# --- !Downs

DROP TABLE Links;