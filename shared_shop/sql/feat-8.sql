-- お気に入りテーブルの作成
CREATE TABLE favorite (
    id NUMBER(10) NOT NULL,
    user_id NUMBER(10) NOT NULL,
    item_id NUMBER(10) NOT NULL,
    insert_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_favorite PRIMARY KEY (id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_favorite_item FOREIGN KEY (item_id) REFERENCES items(id),
    CONSTRAINT uq_favorite_user_item UNIQUE(user_id, item_id)
);

-- シーケンスの作成
CREATE SEQUENCE seq_favorite START WITH 1 INCREMENT BY 1;

-- ロールバック用SQL
-- DROP SEQUENCE seq_favorite;
-- DROP TABLE favorite;
