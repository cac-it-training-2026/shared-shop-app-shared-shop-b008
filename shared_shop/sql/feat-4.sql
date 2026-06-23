-- 閲覧履歴テーブルの作成
CREATE TABLE view_histories (
    id NUMBER(10) NOT NULL,
    user_id NUMBER(10) NOT NULL,
    item_id NUMBER(10) NOT NULL,
    view_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_view_histories PRIMARY KEY (id),
    CONSTRAINT fk_view_histories_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_view_histories_item FOREIGN KEY (item_id) REFERENCES items(id)
);

-- シーケンスの作成
CREATE SEQUENCE seq_view_histories START WITH 1 INCREMENT BY 1;

-- ロールバック用SQL
-- DROP SEQUENCE seq_view_histories;
-- DROP TABLE view_histories;
