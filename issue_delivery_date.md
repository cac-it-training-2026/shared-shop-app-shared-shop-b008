# Issue: 配送希望日指定機能の実装

## 概要

注文時に配送希望日を指定できる機能を追加する。

ユーザーは注文日の3日後〜14日後の範囲で配送希望日を指定できる。

配送希望日は任意入力とし、「希望しない」を選択可能とする。

---

## 背景

フェーズ2要件対応。

顧客体験（UX）向上のため、注文時に配送希望日を指定できるようにする。

---

## 技術的制約・前提条件

- Spring Security等のライブラリ追加禁止
- Spring Boot + Thymeleaf + Spring Data JPA構成を維持すること
- サービスレイヤは追加しないこと
- ControllerからRepositoryを呼び出す既存構成を維持すること
- ブランチ名は `feat#<Issue番号>` とする

---

## 要件

### 配送希望日の入力

届け先入力画面に配送希望日入力欄を追加する。

- 日付のみ指定
- 時間帯指定なし
- 任意入力
- 「希望しない」を選択可能

### 入力チェック

配送希望日が入力された場合のみ以下をチェックする。

- 注文日＋3日以上
- 注文日＋14日以内

範囲外の場合はエラーとする。

**エラーメッセージ**

配送希望日は注文日の3日後から14日後までの範囲で指定してください。

### 注文確認画面

注文確認画面へ配送希望日を表示する。

未指定の場合は「希望しない」を表示する。

### 注文登録

注文確定時に配送希望日を保存する。

### 注文履歴

注文履歴画面で配送希望日を表示する。

未指定の場合は「希望しない」と表示する。

---

## DB変更

### SQL

```sql
ALTER TABLE orders
ADD COLUMN delivery_date DATE NULL;
```

---

## 実装対象

### Entity

- Order.java

```java
@Column
private Date deliveryDate;
```

### Form

- OrderForm.java

```java
private Date deliveryDate;
```

### Controller

- ClientOrderRegistController.java

#### addressInputCheck()

配送希望日の範囲チェックを実装する。

#### createOrder()

配送希望日をOrderへコピーする。

```java
order.setDeliveryDate(orderForm.getDeliveryDate());
```

#### ORDER_FORM_FIELD_ORDER

配送希望日のエラー表示順を追加する。

### View

- client/order/address_input.html
- client/order/check.html
- 注文履歴画面

---

## 実装上の注意事項

- 配送希望日は既存ORDER_FORMセッションで管理すること
- 新規セッション属性は追加しないこと
- 独自Validatorは作成しないこと
- Controllerで業務チェックを実装すること
- 既存注文フローを変更しないこと

---

## Acceptance Criteria

- [ ] ordersテーブルにdelivery_dateが追加されている
- [ ] OrderエンティティにdeliveryDateが追加されている
- [ ] OrderFormにdeliveryDateが追加されている
- [ ] 配送希望日を入力できる
- [ ] 「希望しない」を選択できる
- [ ] 未指定時はnullで保持される
- [ ] 注文日+3日〜14日の入力制限が機能する
- [ ] 範囲外入力時にエラーが表示される
- [ ] 注文確認画面に表示される
- [ ] 注文確定時にDBへ保存される
- [ ] 注文履歴画面で確認できる
- [ ] 未指定時は「希望しない」と表示される
- [ ] 既存注文機能に影響を与えない
