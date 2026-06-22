-- Issue #7: 商品レビュー機能追加
-- ordersテーブルに注文ステータスカラムを追加
-- 0:未発送、1:発送済み。既存データはレビュー可能にするため1で初期化。
ALTER TABLE orders ADD status NUMBER(1) DEFAULT 1 NOT NULL;

-- reviewsテーブルの作成 (ユーザー指定の定義に従う)
CREATE TABLE reviews (
    id NUMBER(6,0) PRIMARY KEY,
    user_id NUMBER(6,0) NOT NULL,
    item_id NUMBER(6,0) NOT NULL,
    order_item_id NUMBER(6,0) NOT NULL,
    rating NUMBER(1,0) NOT NULL,
    review_comment VARCHAR2(2000),
    insert_date DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT fk_reviews_order_item FOREIGN KEY (order_item_id) REFERENCES order_items(id),
    CONSTRAINT check_review_rating CHECK (rating BETWEEN 1 AND 5)
);

-- reviewsテーブル用シーケンスの作成
CREATE SEQUENCE seq_reviews START WITH 1 INCREMENT BY 1;

-- ロールバック用SQL
-- DROP SEQUENCE seq_reviews;
-- DROP TABLE reviews;
-- ALTER TABLE orders DROP COLUMN status;
