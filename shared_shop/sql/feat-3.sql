-- Issue #3: ログイン履歴管理機能
-- このファイルには、ログイン履歴テーブルの作成などのSQLを記述します。

-- ログイン履歴テーブルの作成
CREATE TABLE login_histories (
    id NUMBER(10) NOT NULL,
    user_id NUMBER(10) NOT NULL,
    login_date_time TIMESTAMP NOT NULL,
    ip_address VARCHAR2(45) NOT NULL,
    CONSTRAINT pk_login_histories PRIMARY KEY (id),
    CONSTRAINT fk_login_histories_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ログイン履歴ID用シーケンスの作成
CREATE SEQUENCE seq_login_histories START WITH 1 INCREMENT BY 1;
