CREATE SCHEMA IF NOT EXISTS ec AUTHORIZATION ${dbusername};


DROP SEQUENCE IF EXISTS ec.category_seq;
CREATE SEQUENCE ec.category_seq INCREMENT 1 START 1 NO CYCLE;
CREATE TABLE IF NOT EXISTS ec.categories (
    id bigint NOT NULL DEFAULT nextval('ec.category_seq'::regclass),
    name varchar(255) NOT NULL,
    CONSTRAINT category_pkey PRIMARY KEY (id),
    CONSTRAINT category_name_unique UNIQUE (name)
);


DROP SEQUENCE IF EXISTS ec.product_seq;
CREATE SEQUENCE ec.product_seq INCREMENT 1 START 1 NO CYCLE;
CREATE TABLE IF NOT EXISTS ec.products (
    id bigint NOT NULL DEFAULT nextval('ec.product_seq'::regclass),
    name varchar(255) NOT NULL,
    price float NOT NULL,
    CONSTRAINT product_pkey PRIMARY KEY (id),
    CONSTRAINT product_name_unique UNIQUE (name)
);


DROP SEQUENCE IF EXISTS ec.subscriber_seq;
CREATE SEQUENCE ec.subscriber_seq INCREMENT 1 START 1 NO CYCLE;
CREATE TABLE IF NOT EXISTS ec.subscribers (
    id bigint NOT NULL DEFAULT nextval('ec.subscriber_seq'::regclass),
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    CONSTRAINT subscriber_pkey PRIMARY KEY (id),
    CONSTRAINT subscriber_email_unique UNIQUE (email)
);


DROP SEQUENCE IF EXISTS ec.product_categories_seq;
CREATE SEQUENCE ec.product_categories_seq INCREMENT 1 START 1 NO CYCLE;
CREATE TABLE IF NOT EXISTS ec.product_categories(
    id bigint NOT NULL DEFAULT nextval('ec.product_categories_seq'::regclass),
    categoryId bigint NOT NULL REFERENCES ec.categories(id) ON DELETE CASCADE,
    productId bigint NOT NULL REFERENCES ec.products(id) ON DELETE CASCADE,
    CONSTRAINT product_category_pkey PRIMARY KEY (id)
);


DROP SEQUENCE IF EXISTS ec.subscriber_categories_seq;
CREATE SEQUENCE ec.subscriber_categories_seq INCREMENT 1 START 1 NO CYCLE;
CREATE TABLE IF NOT EXISTS ec.subscriber_categories(
    id bigint NOT NULL DEFAULT nextval('ec.subscriber_categories_seq'::regclass),
    categoryId bigint NOT NULL REFERENCES ec.categories(id) ON DELETE CASCADE,
    subscriberId bigint NOT NULL REFERENCES ec.subscribers(id) ON DELETE CASCADE,
    CONSTRAINT subscriber_category_pkey PRIMARY KEY (id)
);


DROP SEQUENCE IF EXISTS ec.buy_event_seq;
CREATE SEQUENCE ec.buy_event_seq INCREMENT 1 START 1 NO CYCLE;
CREATE TABLE IF NOT EXISTS ec.buy_event(
    id bigint NOT NULL DEFAULT nextval('ec.buy_event_seq'::regclass),
    tstamp TIMESTAMP WITH TIME ZONE DEFAULT now(),
    productId bigint NOT NULL REFERENCES ec.products(id) ON DELETE CASCADE,
    subscriberId bigint NOT NULL REFERENCES ec.subscribers(id) ON DELETE CASCADE,
    CONSTRAINT buy_event_pkey PRIMARY KEY (id)
);


CREATE OR REPLACE VIEW ec.product_events AS
	SELECT t.productid AS productid, t.categoryid AS categoryid, t.num AS num, p.name AS product, c.name AS category
	FROM (
	    SELECT e.productid AS productid, q.categoryid AS categoryid, e.num AS num 
	    FROM (
	        SELECT productid, count(1) AS num 
	        FROM ec.buy_event 
	        GROUP BY productid 
	    ) e
	    JOIN ec.product_categories  q
	    ON e.productid = q.productid
	) t
	JOIN ec.products as p
	ON t.productid = p.id 
	JOIN ec.categories as c
	ON t.categoryid = c.id
;


CREATE OR REPLACE VIEW ec.subscribers_events AS
	SELECT t.subscriberid AS subscriberid, t.categoryid AS categoryid, t.num AS num, s.email AS subscriber_name, s.email AS subscriber_email, c.name AS category
	FROM (
	    SELECT subscriberid, categoryid, count(1) AS num
	    FROM (
	        SELECT subscriberid ,categoryid
	        FROM ec.buy_event AS e
	        JOIN ec.product_categories AS pc
	        ON e.productid = pc.productid
	    ) p
	    GROUP BY subscriberid, categoryid
	) AS t
	JOIN ec.subscribers AS s
	ON t.subscriberid = s.id 
	JOIN ec.categories AS c
	ON t.categoryid=c.id
;