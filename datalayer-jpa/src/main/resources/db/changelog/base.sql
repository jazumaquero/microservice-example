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
    categoryId bigint REFERENCES ec.categories(id) ON DELETE CASCADE,
    productId bigint REFERENCES ec.products(id) ON DELETE CASCADE,
    CONSTRAINT product_category_pkey PRIMARY KEY (id)
);


DROP SEQUENCE IF EXISTS ec.subscriber_categories_seq;
CREATE SEQUENCE ec.subscriber_categories_seq INCREMENT 1 START 1 NO CYCLE;
CREATE TABLE IF NOT EXISTS ec.subscriber_categories(
    id bigint NOT NULL DEFAULT nextval('ec.subscriber_categories_seq'::regclass),
    categoryId bigint REFERENCES ec.categories(id) ON DELETE CASCADE,
    subscriberId bigint REFERENCES ec.subscribers(id) ON DELETE CASCADE,
    CONSTRAINT subscriber_category_pkey PRIMARY KEY (id)
);


DROP SEQUENCE IF EXISTS ec.buy_event_seq;
CREATE SEQUENCE ec.buy_event_seq INCREMENT 1 START 1 NO CYCLE;
CREATE TABLE IF NOT EXISTS ec.buy_event(
    id bigint NOT NULL DEFAULT nextval('ec.buy_event_seq'::regclass),
    tstamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    productId bigint REFERENCES ec.products(id) ON DELETE CASCADE,
    subscriberId bigint REFERENCES ec.subscribers(id) ON DELETE CASCADE,
    CONSTRAINT buy_event_pkey PRIMARY KEY (id)
);