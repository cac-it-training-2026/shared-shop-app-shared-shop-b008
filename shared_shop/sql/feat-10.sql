-- Issue #10: ポイント機能
-- usersテーブルにポイント管理用カラムを追加

ALTER TABLE users ADD (
    point NUMBER(10) DEFAULT 0 NOT NULL,
    CONSTRAINT ck_users_point CHECK (point >= 0)
);

COMMIT;

-- ロールバック用SQL
-- ALTER TABLE users DROP CONSTRAINT ck_users_point;
-- ALTER TABLE users DROP COLUMN point;
