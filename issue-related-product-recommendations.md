# [Feature] 同一カテゴリと売れ筋情報に基づく関連商品のレコメンド表示

## 概要 (Overview)

商品詳細画面に、現在閲覧している商品と関連性の高い別商品を表示する機能を実装します。

現在の商品と同じカテゴリに属する商品の中から、注文数量の多い商品を優先して最大4件取得し、「同じカテゴリのおすすめ商品」として商品詳細画面に表示してください。

ユーザー個人の閲覧履歴には依存せず、現在閲覧している商品のカテゴリと、既存の注文実績を利用して興味を持つ可能性が高い商品を推測します。

これにより、別商品の発見を支援し、商品詳細ページからの回遊性および購入機会の向上を図ります。

## 表示仕様 (Display Specification)

- 商品詳細画面のメイン商品情報および操作ボタンの下に表示すること。
- 見出しは「同じカテゴリのおすすめ商品」または「関連商品」とすること。
- 関連商品は最大4件まで表示すること。
- 各商品について以下を表示すること。
  - 商品画像
  - 商品名
  - 価格
  - 商品詳細画面へのリンク
- 商品画像が登録されていない場合は、既存の`no_image.jpg`を表示すること。
- 関連商品が1件も存在しない場合は、関連商品エリア自体を表示しないこと。
- 未ログインのユーザーにも表示すること。
- PC画面では横並びを基本とし、画面幅が狭い場合は折り返して表示すること。

## レコメンド判定仕様 (Recommendation Logic)

以下の条件で関連商品を取得してください。

1. 現在閲覧している商品と同じカテゴリに属すること。
2. 現在閲覧している商品自身を除外すること。
3. `delete_flag`が未削除状態の商品であること。
4. 商品のカテゴリも未削除状態であること。
5. 在庫数が1以上の商品であること。
6. `order_items.quantity`の合計が多い商品を優先すること。
7. 注文実績がない商品も候補から除外せず、注文数0件として扱うこと。
8. 注文数量が同じ場合は、商品登録日の新しい順、商品IDの降順で並べること。
9. 上位4件のみ取得すること。

想定する優先順位は以下です。

```text
同一カテゴリ
  ↓
現在の商品・削除済み商品・在庫切れ商品を除外
  ↓
累計注文数量の多い順
  ↓
登録日の新しい順
  ↓
商品IDの降順
  ↓
最大4件
```

本機能は機械学習による個人向け推薦ではなく、「現在の商品とのカテゴリ類似性」と「全ユーザーの購入傾向」を組み合わせたルールベースの推薦とします。

## 技術的制約・前提条件 (Constraints & Context)

- ThymeleafおよびSpring Data JPAを使用すること。
- 新しい外部ライブラリを追加しないこと。
- `pom.xml`を変更しないこと。
- サービスレイヤを新設せず、既存のMVC構成に合わせること。
- 商品詳細表示処理は既存の`ClientItemShowController`を拡張すること。
- 商品取得処理は既存の`ItemRepository`へ追加すること。
- Entityへレコメンド専用フィールドを追加しないこと。
- レコメンド結果を保存する新規テーブルを作成しないこと。
- 「最近見た商品」機能や`view_histories`テーブルには依存しないこと。
- 他メンバーが実装する閲覧履歴機能が変更されても、本機能が単独で動作すること。
- Oracle Database環境で動作すること。
- ブランチ名は`feat#<Issue番号>`とすること。

## データベース設計 (Database Design)

本機能では既存データのみを使用します。

- `items.category_id`
  - 現在の商品と同じカテゴリの商品を判定するために使用
- `items.delete_flag`
  - 削除済み商品を除外するために使用
- `items.stock`
  - 在庫切れ商品を除外するために使用
- `items.insert_date`
  - 注文数量が同じ場合の並び順に使用
- `order_items.item_id`
  - 商品と注文明細を関連付けるために使用
- `order_items.quantity`
  - 商品ごとの累計注文数量を計算するために使用

Entityおよびテーブル定義の変更は行わないため、本機能では追加SQLの作成は不要です。

将来的に大量データによる性能問題が確認された場合は、実行計画を確認したうえで`items.category_id`や`order_items.item_id`のインデックス追加を別Issueで検討します。

## 受け入れ条件 (Acceptance Criteria)

- 商品詳細画面に同一カテゴリの別商品が表示されること。
- 現在表示している商品自身が関連商品に含まれないこと。
- 関連商品が最大4件まで表示されること。
- 関連商品が5件以上存在しても、5件目以降は表示されないこと。
- 注文数量の合計が多い商品ほど先に表示されること。
- 注文実績のない商品も候補として取得できること。
- 注文数量が同じ場合、登録日の新しい商品が優先されること。
- 他カテゴリの商品が表示されないこと。
- 論理削除された商品が表示されないこと。
- 論理削除されたカテゴリの商品が表示されないこと。
- 在庫数が0の商品が表示されないこと。
- 関連商品が存在しない場合でもエラーにならないこと。
- 関連商品が存在しない場合、空の見出しや枠が表示されないこと。
- 未ログイン状態でも関連商品が表示されること。
- 商品画像がない場合、既存の代替画像が表示されること。
- 関連商品の商品名または画像をクリックすると、その商品の詳細画面へ遷移すること。
- 遷移先の商品詳細画面でも、その商品を基準とした関連商品へ更新されること。
- 既存の商品詳細表示、商品一覧、買い物かご追加処理へ影響を与えないこと。
- 「最近見た商品」機能が存在しない状態でも単独で動作すること。

## 実装ステップ案 (Implementation Steps for Copilot)

AIアシスタントは以下のステップに従って実装と提案を行ってください。

### 1. ItemRepositoryの改修

`ItemRepository`に、関連商品を取得するメソッドを追加してください。

検索条件は以下とします。

- 同一カテゴリ
- 現在の商品ID以外
- 商品が未削除
- カテゴリが未削除
- 在庫数が1以上
- 注文明細はLEFT JOINする
- 累計注文数量の降順
- 登録日の降順
- 商品IDの降順

注文実績がない商品も取得する必要があるため、`orderItemList`はINNER JOINではなくLEFT JOINを使用してください。

実装イメージ：

```java
@Query("""
    SELECT i
    FROM Item i
    INNER JOIN i.category c
    LEFT JOIN i.orderItemList oi
    WHERE c.id = :categoryId
      AND i.id <> :currentItemId
      AND i.deleteFlag = :deleteFlag
      AND c.deleteFlag = :deleteFlag
      AND i.stock > 0
    GROUP BY i
    ORDER BY COALESCE(SUM(oi.quantity), 0) DESC,
             i.insertDate DESC,
             i.id DESC
    """)
List<Item> findRelatedItems(
        @Param("categoryId") Integer categoryId,
        @Param("currentItemId") Integer currentItemId,
        @Param("deleteFlag") int deleteFlag,
        Pageable pageable);
```

`PageRequest.of(0, 4)`などを使用し、DBから取得する段階で最大4件に制限してください。

全件取得後にJava側で4件へ切り詰める実装は避けてください。

### 2. ClientItemShowControllerの改修

`showItem()`で現在の商品を取得した後、以下の処理を追加してください。

1. 現在の商品からカテゴリIDを取得する。
2. `ItemRepository`から関連商品を最大4件取得する。
3. `BeanTools#copyEntityListToItemBeanList()`を利用して`ItemBean`へ変換する。
4. Modelへ`relatedItems`という名前で格納する。

実装イメージ：

```java
List<Item> relatedItemList = itemRepository.findRelatedItems(
        item.getCategory().getId(),
        item.getId(),
        Constant.NOT_DELETED,
        PageRequest.of(0, 4));

List<ItemBean> relatedItemBeanList =
        beanTools.copyEntityListToItemBeanList(relatedItemList);

model.addAttribute("relatedItems", relatedItemBeanList);
```

対象商品またはカテゴリが取得できない場合は、既存のシステムエラー処理を維持してください。

### 3. 商品詳細画面の改修

`templates/client/item/detail.html`へ関連商品エリアを追加してください。

- `relatedItems`が空でない場合のみ表示すること。
- `th:each`を使用して商品カードを繰り返し表示すること。
- 商品詳細URLは以下の形式とすること。

```html
<a th:href="@{/client/item/detail/{id}(id=${relatedItem.id})}">
```

- 画像がNULLの場合は`/images/common/no_image.jpg`を表示すること。
- 商品名と価格を表示すること。

表示判定イメージ：

```html
<section class="related_items"
         th:if="${relatedItems != null and !#lists.isEmpty(relatedItems)}">
    <h3 th:text="#{title.related.items}"></h3>

    <div class="related_item_list">
        <article class="related_item"
                 th:each="relatedItem : ${relatedItems}">
            <!-- 商品画像、商品名、価格、詳細リンク -->
        </article>
    </div>
</section>
```

### 4. メッセージ定義の追加

`messages.properties`に表示文言を追加してください。

例：

```properties
title.related.items=同じカテゴリのおすすめ商品
```

必要に応じて、商品価格など既存のメッセージキーを再利用してください。

### 5. CSSの追加

`stylesheet.css`へ関連商品カード用のスタイルを追加してください。

- 商品カードを横並びで表示すること。
- 画面幅が狭い場合は折り返すこと。
- 画像サイズを統一すること。
- 長い商品名によってレイアウトが崩れないこと。
- 既存の商品詳細画面のデザインと調和させること。

### 6. テストの追加

RepositoryまたはControllerのテストを追加し、少なくとも以下を確認してください。

- 同一カテゴリの商品だけが取得される。
- 現在の商品が除外される。
- 削除済み商品が除外される。
- 在庫切れ商品が除外される。
- 注文数量の多い順に並ぶ。
- 注文実績のない商品も取得される。
- 最大4件まで取得される。
- 候補が0件でも商品詳細画面を正常表示できる。
- 未ログイン状態でも正常表示できる。

### 7. 既存機能への影響確認

以下の既存機能が正常に動作することを確認してください。

- 商品詳細表示
- 商品一覧表示
- カテゴリ検索
- 売れ筋順表示
- 買い物かごへの商品追加
- 論理削除済み商品の非表示

## 主な修正候補ファイル

### 修正

- `ClientItemShowController.java`
- `ItemRepository.java`
- `templates/client/item/detail.html`
- `messages.properties`
- `static/css/stylesheet.css`

### 新規作成候補

- 関連商品取得用のRepositoryテスト
- 商品詳細画面用のControllerテスト

### 変更不要

- `Item.java`
- `Category.java`
- `OrderItem.java`
- `ItemBean.java`
- `BeanTools.java`
- DBテーブル定義
- SQLファイル
- 閲覧履歴関連のEntityおよびRepository

## 完了条件 (Definition of Done)

- すべての受け入れ条件を満たしていること。
- Mavenテストが正常終了すること。
- Oracle Database上で関連商品取得クエリが正常に実行されること。
- 既存の商品詳細および買い物かご機能にリグレッションがないこと。
- Pull Requestに推薦条件、テスト結果、画面キャプチャを記載すること。
