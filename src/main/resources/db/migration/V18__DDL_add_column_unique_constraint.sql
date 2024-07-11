ALTER TABLE actor ADD CONSTRAINT uk_actor_name UNIQUE (name);
ALTER TABLE movie_actor ADD CONSTRAINT uk_movie_actor UNIQUE (movie_id, actor_id);
ALTER TABLE image ADD hash VARCHAR(64) NOT NULL comment 'url的md5' AFTER url;
ALTER TABLE image ADD CONSTRAINT uk_hash UNIQUE (hash);
ALTER TABLE actor_image ADD CONSTRAINT uk_actor_image UNIQUE (actor_id, image_id);
