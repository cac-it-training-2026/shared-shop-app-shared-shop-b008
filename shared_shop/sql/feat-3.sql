-- Issue #3: ログイン履歴管理機能

-- ログイン履歴テーブルの作成
CREATE TABLE login_histories (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    login_date_time TIMESTAMP NOT NULL,
    ip_address VARCHAR2(45) NOT NULL,
    CONSTRAINT fk_login_history_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ID生成用のシーケンス作成
CREATE SEQUENCE seq_login_histories NOCACHE;
