# お気に入り機能実装（AI実装向け詳細版）

## 概要

ログイン会員向けのお気に入り機能を追加する。

商品詳細画面から商品をお気に入り登録できるようにし、サイドバーからお気に入り一覧画面へ遷移できるようにする。

また、お気に入り一覧画面では登録済み商品の確認およびお気に入り解除を可能とする。

---

## 技術的制約・前提条件 (Constraints & Context)

- Spring Securityなどpom.xmlの変更を伴うライブラリを使用しないこと。
- サービスレイヤは作成しないこと。
- ControllerからRepositoryを直接呼び出す既存構成に合わせること。
- テンプレートエンジンはThymeleafを使用すること。
- DBアクセスはSpring Data JPAを使用すること。
- 既存Entityである `User`、`Item` を利用して実装すること。
- ログインユーザー情報は既存の `session.user` を利用すること。
- 共通サイドバーは `templates/common/sidebar.html` を修正すること。
- 商品詳細画面は `/client/item/detail/{id}` を利用すること。
- 実装時はプロジェクトのコーディング規約(PRGDE004)を遵守すること。
- クラス・メソッド・変数命名規則を遵守すること。
- Javadocコメントを適切に付与すること。
- コメント記述ルール、インデント、半角スペース規約を遵守すること。

---

## DB設計

### 新規テーブル

テーブル名

```sql
favorite
```

### カラム

|カラム名|型|備考|
|---|---|---|
|id|INT|PK AUTO_INCREMENT|
|user_id|INT|会員ID|
|item_id|INT|商品ID|
|insert_date|DATETIME|登録日時|

### 制約

- PK(id)
- FK(user_id) → users.id
- FK(item_id) → items.id
- UNIQUE(user_id,item_id)

### SQL

```sql
CREATE TABLE favorite (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    insert_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_favorite_user
        FOREIGN KEY(user_id)
        REFERENCES users(id),

    CONSTRAINT fk_favorite_item
        FOREIGN KEY(item_id)
        REFERENCES items(id),

    CONSTRAINT uq_favorite_user_item
        UNIQUE(user_id, item_id)
);
```

---

## 想定修正ファイル

### Entity

- entity/Favorite.java（新規）

### Repository

- repository/FavoriteRepository.java（新規）

### Controller

- controller/client/ClientFavoriteController.java（新規）
- controller/client/ClientItemController.java（修正）

### Template

- templates/client/item/detail.html（修正）
- templates/client/favorite/list.html（新規）
- templates/common/sidebar.html（修正）

### SQL

- favoriteテーブル追加

---

## Entity設計

### Favorite Entity

Favoriteはお気に入り管理テーブルとする。

### Relation

- Favorite : User = ManyToOne
- Favorite : Item = ManyToOne

### 構成

```text
Favorite
 ├ id
 ├ user
 ├ item
 └ insertDate
```

---

## Repository要件

FavoriteRepositoryを新規作成すること。

以下のメソッドを実装すること。

```java
List<Favorite> findByUserId(Integer userId);

Optional<Favorite> findByUserIdAndItemId(
    Integer userId,
    Integer itemId);

boolean existsByUserIdAndItemId(
    Integer userId,
    Integer itemId);

void deleteByUserIdAndItemId(
    Integer userId,
    Integer itemId);
```

---

## URL設計

### お気に入り一覧

```http
GET /client/favorite/list
```

### お気に入り登録

```http
POST /client/favorite/regist/{itemId}
```

### お気に入り解除

```http
POST /client/favorite/delete/{itemId}
```

---

## 画面遷移

### お気に入り登録完了

```text
/client/item/detail/{itemId}
```

へリダイレクトすること。

### お気に入り解除完了

遷移元画面へリダイレクトすること。

---

## 受け入れ条件 (Acceptance Criteria)

### お気に入り登録

- ログイン会員のみお気に入り登録できること。
- 商品詳細画面に「お気に入り登録」ボタンが表示されること。
- ボタン押下時にfavoriteテーブルへ登録されること。
- 同一会員が同一商品を重複登録できないこと。
- 登録済商品の場合は「お気に入り解除」ボタンへ表示が切り替わること。

### お気に入り解除

- 登録済商品を解除できること。
- 解除後にfavoriteテーブルのレコードが削除されること。

### サイドバー

- ログイン済一般会員のみ「お気に入り商品一覧」リンクが表示されること。
- カテゴリ検索欄の下に表示されること。
- リンク押下時にお気に入り一覧画面へ遷移できること。

### お気に入り一覧

- ログイン会員本人のお気に入り商品のみ表示されること。
- favorite.insert_date DESC で表示されること。
- 以下が表示されること。
  - 商品画像
  - 商品名
  - 商品価格
  - 商品詳細リンク
  - お気に入り解除ボタン

### アクセス制御

- 未ログイン状態でお気に入り機能へアクセスした場合はログイン画面へ遷移すること。
- 他会員のお気に入りデータを参照できないこと。

---

## 実装ステップ案 (Implementation Steps for Copilot)

### 1. Favorite Entity作成

- favoriteテーブルとのマッピング
- UserとのManyToOne関連
- ItemとのManyToOne関連

### 2. FavoriteRepository作成

- 一覧取得
- 登録判定
- 登録
- 削除

### 3. 商品詳細画面改修

対象URL

`/client/item/detail/{id}`

対象テンプレート

`templates/client/item/detail.html`

実装内容

- お気に入り状態判定
- 画面へ状態を渡す
- 買い物かごボタン左側へお気に入りボタン追加
- 登録済みの場合は解除ボタン表示

### 4. お気に入り登録機能追加

- 商品ID取得
- セッションユーザー取得
- favorite登録
- 商品詳細画面へリダイレクト

### 5. お気に入り解除機能追加

- favorite削除
- 遷移元へリダイレクト

### 6. サイドバー改修

対象

`templates/common/sidebar.html`

- お気に入り商品一覧リンク追加

### 7. お気に入り一覧画面作成

対象URL

`/client/favorite/list`

表示内容

- 商品画像
- 商品名
- 商品価格
- 商品詳細リンク
- お気に入り解除ボタン

### 8. 商品削除時整合性対応

- FK制約利用
- またはfavoriteレコード削除

---

## レビュー観点

- N+1問題が発生していないか
- 重複登録防止がアプリとDBの両方で担保されているか
- 未ログインアクセス時にログイン画面へ遷移するか
- 他会員データへアクセスできないか
- 商品削除時に整合性が崩れないか
- ThymeleafでNullPointerExceptionが発生しないか
- 一覧表示順がinsert_date DESCになっているか

---

## 実装禁止事項

- Serviceクラスを新規作成しないこと
- Spring Securityを導入しないこと
- 既存ログイン処理を変更しないこと
- 既存テーブル定義を変更しないこと
- favoriteテーブル以外のDDL変更を行わないこと
