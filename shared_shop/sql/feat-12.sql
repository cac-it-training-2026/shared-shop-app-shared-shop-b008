-- Issue #12: 注文取消機能
-- ordersテーブルに注文取消状態を保持するカラムを追加する。

ALTER TABLE orders ADD (
    cancel_flag NUMBER(1) DEFAULT 0 NOT NULL,
    cancel_date DATE
);

ALTER TABLE orders ADD CONSTRAINT ck_orders_cancel_flag
    CHECK (cancel_flag IN (0, 1));

-- ロールバック用SQL
-- ALTER TABLE orders DROP CONSTRAINT ck_orders_cancel_flag;
-- ALTER TABLE orders DROP COLUMN cancel_date;
-- ALTER TABLE orders DROP COLUMN cancel_flag;
