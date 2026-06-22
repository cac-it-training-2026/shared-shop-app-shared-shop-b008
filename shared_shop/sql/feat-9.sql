-- Issue #9: クーポン機能
-- 適用順序: coupon_types -> user_coupons -> coupon_gacha_histories -> orders拡張

CREATE TABLE coupon_types (
    id NUMBER(6) NOT NULL,
    name VARCHAR2(50 CHAR) NOT NULL,
    discount_rate NUMBER(3) NOT NULL,
    minimum_order_amount NUMBER(8) NOT NULL,
    validity_days NUMBER(4) DEFAULT 30 NOT NULL,
    active_flag NUMBER(1) DEFAULT 1 NOT NULL,
    insert_date DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT pk_coupon_types PRIMARY KEY (id),
    CONSTRAINT uq_coupon_types_name UNIQUE (name),
    CONSTRAINT ck_coupon_types_rate CHECK (discount_rate BETWEEN 1 AND 100),
    CONSTRAINT ck_coupon_types_min_amount CHECK (minimum_order_amount >= 0),
    CONSTRAINT ck_coupon_types_validity CHECK (validity_days > 0),
    CONSTRAINT ck_coupon_types_active CHECK (active_flag IN (0, 1))
);

CREATE SEQUENCE seq_coupon_types START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE user_coupons (
    id NUMBER(10) NOT NULL,
    user_id NUMBER(6) NOT NULL,
    coupon_type_id NUMBER(6) NOT NULL,
    acquired_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_user_coupons PRIMARY KEY (id),
    CONSTRAINT fk_user_coupons_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_coupons_type FOREIGN KEY (coupon_type_id) REFERENCES coupon_types(id),
    CONSTRAINT ck_user_coupons_expiry CHECK (expires_at > acquired_at)
);

CREATE SEQUENCE seq_user_coupons START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE INDEX idx_user_coupons_user_expiry ON user_coupons(user_id, expires_at);

CREATE TABLE coupon_gacha_histories (
    id NUMBER(10) NOT NULL,
    user_id NUMBER(6) NOT NULL,
    business_date DATE NOT NULL,
    result_coupon_type_id NUMBER(6),
    drawn_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_coupon_gacha_histories PRIMARY KEY (id),
    CONSTRAINT fk_coupon_gacha_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_coupon_gacha_type FOREIGN KEY (result_coupon_type_id) REFERENCES coupon_types(id),
    CONSTRAINT uq_coupon_gacha_daily UNIQUE (user_id, business_date)
);

CREATE SEQUENCE seq_coupon_gacha_histories START WITH 1 INCREMENT BY 1 NOCACHE;

ALTER TABLE orders ADD (
    coupon_type_id NUMBER(6),
    coupon_name VARCHAR2(50 CHAR),
    coupon_discount_rate NUMBER(3),
    coupon_discount_amount NUMBER(7) DEFAULT 0 NOT NULL,
    CONSTRAINT fk_orders_coupon_type FOREIGN KEY (coupon_type_id) REFERENCES coupon_types(id),
    CONSTRAINT ck_orders_coupon_rate CHECK (coupon_discount_rate IS NULL OR coupon_discount_rate BETWEEN 1 AND 100),
    CONSTRAINT ck_orders_coupon_amount CHECK (coupon_discount_amount >= 0)
);

INSERT INTO coupon_types (
    id, name, discount_rate, minimum_order_amount, validity_days, active_flag, insert_date
) VALUES (
    seq_coupon_types.NEXTVAL,
    '5' || UNISTR('\FF05\5272\5F15\30AF\30FC\30DD\30F3'),
    5, 500, 30, 1, SYSDATE
);

INSERT INTO coupon_types (
    id, name, discount_rate, minimum_order_amount, validity_days, active_flag, insert_date
) VALUES (
    seq_coupon_types.NEXTVAL,
    '10' || UNISTR('\FF05\5272\5F15\30AF\30FC\30DD\30F3'),
    10, 1000, 30, 1, SYSDATE
);

INSERT INTO coupon_types (
    id, name, discount_rate, minimum_order_amount, validity_days, active_flag, insert_date
) VALUES (
    seq_coupon_types.NEXTVAL,
    '15' || UNISTR('\FF05\5272\5F15\30AF\30FC\30DD\30F3'),
    15, 2000, 30, 1, SYSDATE
);

COMMIT;

-- ロールバック用SQL（以下を記載順に実行）
-- ALTER TABLE orders DROP CONSTRAINT fk_orders_coupon_type;
-- ALTER TABLE orders DROP CONSTRAINT ck_orders_coupon_rate;
-- ALTER TABLE orders DROP CONSTRAINT ck_orders_coupon_amount;
-- ALTER TABLE orders DROP COLUMN coupon_type_id;
-- ALTER TABLE orders DROP COLUMN coupon_name;
-- ALTER TABLE orders DROP COLUMN coupon_discount_rate;
-- ALTER TABLE orders DROP COLUMN coupon_discount_amount;
-- DROP SEQUENCE seq_coupon_gacha_histories;
-- DROP TABLE coupon_gacha_histories CASCADE CONSTRAINTS;
-- DROP INDEX idx_user_coupons_user_expiry;
-- DROP SEQUENCE seq_user_coupons;
-- DROP TABLE user_coupons CASCADE CONSTRAINTS;
-- DROP SEQUENCE seq_coupon_types;
-- DROP TABLE coupon_types CASCADE CONSTRAINTS;
