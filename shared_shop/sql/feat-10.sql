-- Issue #11: ポイント機能
-- usersテーブルにポイント管理用カラムを追加
-- ordersテーブルにポイント利用・付与履歴カラムを追加

ALTER TABLE users ADD (
    point NUMBER(10) DEFAULT 0 NOT NULL,
    CONSTRAINT ck_users_point CHECK (point >= 0)
);

ALTER TABLE orders ADD (
    use_point NUMBER(10) DEFAULT 0 NOT NULL,
    earned_point NUMBER(10) DEFAULT 0 NOT NULL,
    CONSTRAINT ck_orders_use_point CHECK (use_point >= 0),
    CONSTRAINT ck_orders_earned_point CHECK (earned_point >= 0)
);

COMMIT;

-- ロールバック用SQL
-- ALTER TABLE orders DROP CONSTRAINT ck_orders_use_point;
-- ALTER TABLE orders DROP CONSTRAINT ck_orders_earned_point;
-- ALTER TABLE orders DROP COLUMN use_point;
-- ALTER TABLE orders DROP COLUMN earned_point;
-- ALTER TABLE users DROP CONSTRAINT ck_users_point;
-- ALTER TABLE users DROP COLUMN point;
