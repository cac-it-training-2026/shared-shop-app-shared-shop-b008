-- Issue #7: 商品レビュー機能
-- reviewsテーブルの作成

CREATE TABLE reviews (
    id NUMBER(10) NOT NULL,
    user_id NUMBER(10) NOT NULL,
    item_id NUMBER(10) NOT NULL,
    order_item_id NUMBER(10) NOT NULL,
    rating NUMBER(1) NOT NULL,
    review_comment VARCHAR2(2000 CHAR),
    insert_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_reviews PRIMARY KEY (id),
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_reviews_order_item FOREIGN KEY (order_item_id) REFERENCES order_items(id),
    CONSTRAINT ck_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);

-- シーケンスの作成
CREATE SEQUENCE seq_reviews START WITH 1 INCREMENT BY 1;

COMMENT ON COLUMN reviews.id IS 'レビューID';
COMMENT ON COLUMN reviews.user_id IS '会員ID';
COMMENT ON COLUMN reviews.item_id IS '商品ID';
COMMENT ON COLUMN reviews.order_item_id IS '注文商品ID';
COMMENT ON COLUMN reviews.rating IS '評価';
COMMENT ON COLUMN reviews.review_comment IS 'レビュー本文';
COMMENT ON COLUMN reviews.insert_date IS '投稿日';

-- ロールバック用SQL
-- DROP SEQUENCE seq_reviews;
-- DROP TABLE reviews;
