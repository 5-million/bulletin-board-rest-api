INSERT INTO users(id, email, password, display_name, create_at) VALUES (1, 'abc@test.com', 'password', 'name abc', NOW());
INSERT INTO users(id, email, password, display_name, create_at) VALUES (2, 'def@test.com', 'password', 'name def', NOW());
INSERT INTO users(id, email, password, display_name, create_at) VALUES (3, 'ghi@test.com', 'password', 'name ghj', NOW());

INSERT INTO posts(id, writer, title, content, views, create_at, update_at) VALUES (1, 1, 'title1', 'content1', 5, NOW(), NOW());
INSERT INTO posts(id, writer, title, content, views, create_at, update_at) VALUES (2, 1, 'title2', 'content2', 6, NOW(), NOW());
INSERT INTO posts(id, writer, title, content, views, create_at, update_at) VALUES (3, 2, 'title3', 'content3', 1, NOW(), NOW());
INSERT INTO posts(id, writer, title, content, views, create_at, update_at) VALUES (4, 2, 'title4', 'content4', 34, NOW(), NOW());
INSERT INTO posts(id, writer, title, content, views, create_at, update_at) VALUES (5, 3, 'title5', 'content5', 57, NOW(), NOW());
INSERT INTO posts(id, writer, title, content, views, create_at, update_at) VALUES (6, 3, 'title6', 'content6', 23, NOW(), NOW());
INSERT INTO posts(id, writer, title, content, views, create_at, update_at) VALUES (7, 3, 'title7', 'content7', 346, NOW(), NOW());
INSERT INTO posts(id, writer, title, content, views, create_at, update_at) VALUES (8, 3, 'title8', 'content8', 111, NOW(), NOW());
