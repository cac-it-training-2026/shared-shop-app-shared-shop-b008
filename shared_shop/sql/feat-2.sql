-- Issue #2: ログイン失敗時のアカウントロック機能に伴うテーブル変更
-- usersテーブルにログイン失敗回数とロック解除時刻を追加

ALTER TABLE users ADD (
    login_failure_count NUMBER(10) DEFAULT 0,
    lock_until TIMESTAMP
);

COMMENT ON COLUMN users.login_failure_count IS 'ログイン失敗回数';
COMMENT ON COLUMN users.lock_until IS 'ロック解除日時';

-- ロールバック用SQL
-- ALTER TABLE users DROP COLUMN login_failure_count;
-- ALTER TABLE users DROP COLUMN lock_until;
