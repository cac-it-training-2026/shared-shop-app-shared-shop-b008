-- Issue #20: お届け先登録機能の実装

-- お届け先管理テーブルの作成
CREATE TABLE delivery_addresses (
    id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    address_no NUMBER NOT NULL,
    name VARCHAR2(50) NOT NULL,
    postal_code VARCHAR2(10) NOT NULL,
    address VARCHAR2(255) NOT NULL,
    phone_number VARCHAR2(20) NOT NULL,
    CONSTRAINT fk_delivery_address_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ID生成用のシーケンス作成
CREATE SEQUENCE seq_delivery_addresses NOCACHE;
