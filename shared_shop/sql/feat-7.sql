-- Issue #7: 商品レビュー機能追加
-- ordersテーブルに注文ステータスカラムを追加
-- 0:未発送、1:発送済み。既存データはレビュー可能にするため1で初期化。
ALTER TABLE orders ADD status NUMBER(1) DEFAULT 1 NOT NULL;

-- reviewsテーブルの作成
CREATE TABLE reviews (
    id NUMBER(10) PRIMARY KEY,
    user_id NUMBER(10) NOT NULL,
    product_id NUMBER(10) NOT NULL,
    order_id NUMBER(10) NOT NULL,
    rating NUMBER(1) NOT NULL,
    comment VARCHAR2(2000),
    created_date TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES items(id),
    CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT check_rating CHECK (rating BETWEEN 1 AND 5)
);

-- reviewsテーブル用シーケンスの作成
CREATE SEQUENCE seq_reviews START WITH 1 INCREMENT BY 1;

-- ロールバック用SQL
-- DROP SEQUENCE seq_reviews;
-- DROP TABLE reviews;
-- ALTER TABLE orders DROP COLUMN status;
