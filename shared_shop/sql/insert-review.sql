-- レビュー登録用サンプルSQL (Oracle Database用)

-- 基本的なインサート文
-- user_id, item_id, order_item_id は既存のデータに合わせて変更してください
INSERT INTO reviews (id, user_id, item_id, order_item_id, rating, review_comment)
VALUES (seq_reviews.NEXTVAL, 1, 1, 1, 5, 'とても使いやすくて満足しています。');

INSERT INTO reviews (id, user_id, item_id, order_item_id, rating, review_comment)
VALUES (seq_reviews.NEXTVAL, 1, 2, 2, 3, '普通でした。');

INSERT INTO reviews (id, user_id, item_id, order_item_id, rating, review_comment)
VALUES (seq_reviews.NEXTVAL, 2, 1, 3, 4, 'デザインが気に入りました。');

-- サブクエリを使用した例 (特定の会員メールアドレスと商品名を指定してインサート)
INSERT INTO reviews (id, user_id, item_id, order_item_id, rating, review_comment)
SELECT
    seq_reviews.NEXTVAL,
    (SELECT id FROM users WHERE email = 'test@example.com'),
    (SELECT item_id FROM order_items WHERE id = 100),
    100,
    5,
    'サブクエリでの登録テストです。'
FROM DUAL;

COMMIT;
