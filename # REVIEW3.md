# [Feature] 商品レビュー機能追加

| 項目 | 内容 |
|---|---|
| 概要 (Overview) | 購入済みユーザーが商品レビューを投稿できる機能を追加する。注文詳細画面からレビュー入力へ遷移し、商品詳細画面で他ユーザーのレビューを確認できるようにする。 |

| 項目 | 内容 |
|---|---|
| 背景 | 商品購入後の評価・口コミ機能を追加し、購入判断材料として利用できるようにする。 |

| 項目 | 内容 |
|---|---|
| 重要前提 | 現在使用しているOracle Database設計を変更しない。既存テーブル構造を基準に実装すること。 |

## 既存DB前提条件

| テーブル | 主なカラム |
|---|---|
| CATEGORIES | ID NUMBER(2), NAME VARCHAR2(15), DESCRIPTION, DELETE_FLAG, INSERT_DATE |
| ITEMS | ID NUMBER(6), NAME VARCHAR2(100), PRICE NUMBER(7), DESCRIPTION, STOCK, IMAGE, CATEGORY_ID, DELETE_FLAG, INSERT_DATE |
| USERS | ID NUMBER(6), EMAIL, PASSWORD, NAME, POSTAL_CODE, ADDRESS, PHONE_NUMBER, AUTHORITY, DELETE_FLAG, INSERT_DATE |
| ORDERS | ID NUMBER(6), POSTAL_CODE, ADDRESS, NAME, PHONE_NUMBER, PAY_METHOD, USER_ID, INSERT_DATE |
| ORDER_ITEMS | ID NUMBER(6), QUANTITY, ORDER_ID, ITEM_ID, PRICE |

| 項目 | 内容 |
|---|---|
| DB制約 | 既存テーブルのカラム名・型・制約を変更禁止。 |
| DB制約 | USERS、ITEMS、ORDERS、ORDER_ITEMSとの関連を維持すること。 |
| DB制約 | Oracle Database用SQLで作成すること。 |

---

# レビュー機能仕様

| 項目 | 内容 |
|---|---|
| 投稿場所 | 注文詳細画面 |
| 操作 | 商品名横に「レビューを書く」ボタンを追加する |
| 条件 | 購入済み商品のみレビュー投稿可能 |
| 評価 | ★1〜5段階 |
| コメント | 最大2000文字、未入力可能 |
| 日付 | 登録日時を保存 |

---

# レビュー表示

| 項目 | 内容 |
|---|---|
| 表示場所 | 商品詳細画面下部 |
| 表示内容 | 投稿者名、評価、コメント、投稿日 |
| 初期並び順 | 新しいレビュー順 |
| 並び替え | 高評価順、低評価順 |

---

# DB追加仕様

| 項目 | 内容 |
|---|---|
| 新規テーブル | REVIEWSテーブルを追加する |
| 作成条件 | 既存DB設計に合わせる |

REVIEWS想定：

| カラム | 型 |
|---|---|
| ID | NUMBER(10) PRIMARY KEY |
| USER_ID | NUMBER(6) NOT NULL |
| ITEM_ID | NUMBER(6) NOT NULL |
| ORDER_ITEM_ID | NUMBER(6) NOT NULL |
| RATING | NUMBER(1) |
| REVIEW_COMMENT | VARCHAR2(2000) |
| INSERT_DATE | DATE |

外部キー：

USER_ID → USERS.ID

ITEM_ID → ITEMS.ID

ORDER_ITEM_ID → ORDER_ITEMS.ID


---

# 権限制御

| 項目 | 内容 |
|---|---|
| 削除 | 自分のレビューのみ削除可能 |
| 管理者 | ADMIN権限ユーザーは削除可能 |
| 編集 | 編集機能は作成しない |

---

# 技術的制約・前提条件 (Constraints & Context)

| 項目 | 内容 |
|---|---|
| Framework | Spring Boot |
| Language | Java |
| Template | Thymeleaf |
| DB | Oracle Database |
| ORM | Spring Data JPA |
| MVC | ControllerからRepositoryを利用する既存構成維持 |
| 禁止 | Spring Security導入禁止 |
| 禁止 | pom.xml変更禁止 |
| 禁止 | Service層新規作成禁止 |
| 認証 | HttpSession利用 |

---

# 実装ステップ案 (Implementation Steps for Copilot)

| 順番 | 内容 |
|---|---|
| 1 | 既存EntityとDB構造を確認 |
| 2 | REVIEWSテーブル作成SQLを作成 |
| 3 | Review Entity作成 |
| 4 | ReviewRepository作成 |
| 5 | 注文詳細画面へレビュー導線追加 |
| 6 | レビュー登録Controller追加 |
| 7 | 商品詳細画面へレビュー一覧表示追加 |
| 8 | 並び替え処理追加 |
| 9 | 削除処理追加 |
| 10 | 動作確認 |

---

# 受け入れ条件 (Acceptance Criteria)

| 条件 | 内容 |
|---|---|
| 1 | 既存テーブル構造が変更されていない |
| 2 | REVIEWSテーブルが作成される |
| 3 | 購入者のみレビュー投稿できる |
| 4 | 商品詳細画面でレビュー確認できる |
| 5 | 評価1〜5で登録できる |
| 6 | コメントなしでも登録できる |
| 7 | 自分のレビュー削除ができる |
| 8 | ADMINが削除できる |
| 9 | 編集機能が存在しない |
| 10 | Oracle SQLで正常実行できる |

---