-- Issue #6: 配送希望日指定機能
-- ordersテーブルに配送希望日カラムを追加

ALTER TABLE orders ADD (delivery_date DATE);

-- ロールバック用SQL
-- ALTER TABLE orders DROP COLUMN delivery_date;
