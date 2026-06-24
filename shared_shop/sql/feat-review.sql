CREATE TABLE reviews (
    id NUMBER(10),
    user_id NUMBER(6) NOT NULL,
    item_id NUMBER(6) NOT NULL,
    order_item_id NUMBER(6) NOT NULL,
    rating NUMBER(1) NOT NULL,
    review_comment VARCHAR2(2000),
    insert_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_reviews PRIMARY KEY (id),
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_reviews_order_item FOREIGN KEY (order_item_id) REFERENCES order_items(id)
);

CREATE SEQUENCE seq_reviews START WITH 1 INCREMENT BY 1;
