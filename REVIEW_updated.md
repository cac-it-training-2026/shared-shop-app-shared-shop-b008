# [Feature] 商品レビュー機能追加（評価集計機能含む）

| 項目 | 内容 |
|---|---|
| 概要 (Overview) | 購入済みユーザーが商品レビューを投稿できる機能を追加する。注文詳細からレビュー入力へ遷移し、商品詳細画面でレビュー一覧を表示する。 |
| 目的 | 商品評価を表示し、購入判断材料として利用できるようにする。 |
| 対象システム | Spring Boot / Java / Thymeleaf / Spring Data JPA / Oracle Database |

| 項目 | 内容 |
|---|---|
| DB対応 | レビュー機能に必要なREVIEWSテーブルを新規作成してよい。 |
| DB設計方針 | 既存USERS、ITEMS、ORDERS、ORDER_ITEMSとのリレーションを維持すること。 |
| DB作成 | Oracle Database用CREATE TABLE文を作成すること。 |
| DB作成 | Entity定義と一致するカラム設計にすること。 |

| 項目 | 内容 |
|---|---|
| REVIEWSテーブル | ID / USER_ID / ITEM_ID / ORDER_ITEM_ID / RATING / REVIEW_COMMENT / INSERT_DATE |
| 制約 | USER_ID → USERS.ID |
| 制約 | ITEM_ID → ITEMS.ID |
| 制約 | ORDER_ITEM_ID → ORDER_ITEMS.ID |
| 制約 | RATINGは1〜5のみ許可 |

| 項目 | 内容 |
|---|---|
| 技術制約 | Service層は作成しない。ControllerからRepositoryを利用する既存MVC構成を維持する。 |
| 技術制約 | Spring Security導入禁止。 |
| 技術制約 | pom.xml変更禁止。 |
| 技術制約 | Sessionログイン管理を利用する。 |

| 項目 | 内容 |
|---|---|
| 投稿機能 | 注文詳細の商品名横に「レビューを書く」ボタン追加。 |
| 条件 | 購入済み商品のみレビュー可能。 |
| 評価 | ★1〜5 |
| コメント | 未入力可 |
| 日付 | 登録日時を保存 |

| 項目 | 内容 |
|---|---|
| 表示 | 商品詳細画面下部にレビュー一覧表示。 |
| 表示内容 | 投稿者名、評価、コメント、投稿日 |
| 並び順 | 初期は新しい順 |
| 並び替え | 高評価順、低評価順 |

| 項目 | 内容 |
|---|---|
| 削除 | 自分のレビューまたはADMINのみ削除可能 |
| 編集 | 編集機能なし |

| 項目 | 内容 |
|---|---|
| 実装ステップ | ①既存Entity確認 |
| 実装ステップ | ②REVIEWSテーブル作成SQL作成 |
| 実装ステップ | ③Review Entity作成 |
| 実装ステップ | ④ReviewRepository作成 |
| 実装ステップ | ⑤Controller追加 |
| 実装ステップ | ⑥Thymeleaf画面追加 |
| 実装ステップ | ⑦動作確認 |

| 項目 | 内容 |
|---|---|
| 実装ルール | 既存テーブル構造を壊さないこと。 |
| 実装ルール | 不要なService追加禁止。 |
| 実装ルール | 実装前に変更ファイル一覧を提示すること。 |

| ブランチ名 | feat#<Issue番号> |