package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;
import jp.co.sss.shop.util.Constant;

/**
 * 注文登録機能(一般会員用)のコントローラクラスです。
 *
 * @author SystemShared
 */
@Controller
public class ClientOrderRegistController {

	/**
	 * 買い物かご情報のセッション属性名
	 */
	private static final String BASKET_BEANS = "basketBeans";

	/**
	 * 注文入力フォームのセッション属性名
	 */
	private static final String ORDER_FORM = "orderForm";

	private static final String[] ORDER_FORM_FIELD_ORDER = {
			"postalCode", "address", "name", "phoneNumber"
	};

	/**
	 * 在庫不足商品名リストの属性名
	 */
	private static final String ITEM_NAME_LIST_LESS_THAN = "itemNameListLessThan";

	/**
	 * 在庫切れ商品名リストの属性名
	 */
	private static final String ITEM_NAME_LIST_ZERO = "itemNameListZero";

	/**
	 * 商品情報リポジトリ
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * 注文情報リポジトリ
	 */
	@Autowired
	OrderRepository orderRepository;

	/**
	 * 注文商品情報リポジトリ
	 */
	@Autowired
	OrderItemRepository orderItemRepository;

	/**
	 * 会員情報リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * 金額計算サービス
	 */
	@Autowired
	PriceCalc priceCalc;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	// ===== 担当: シュエ ジーハン / 届け先入力（入力チェック） =====
	/**
	 * 注文手続き開始時に届け先入力フォームを初期化します。
	 *
	 * @return "redirect:/client/order/address/input" 届け先入力画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String addressInputInit() {
		if (isBasketEmpty()) {
			return "redirect:/client/basket/list";
		}

		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/login";
		}

		// セッションに保存されているログインユーザーBeanには、基本的に会員IDなど最低限の情報のみが入っている。
		// そのため、届け先入力画面の初期表示に必要な郵便番号・住所・氏名・電話番号などの最新情報を、
		// userテーブルから削除されていない会員情報として改めて取得する。
		// findByIdAndDeleteFlagを使うことで、論理削除済みの会員情報を誤って利用しないようにしている。
		User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), Constant.NOT_DELETED);
		if (user == null) {
			return "redirect:/syserror";
		}

		// 注文入力用のFormを新しく作成し、会員情報Entityから届け先入力に必要な項目をコピーする。
		// これにより、画面を開いた時点で会員登録済みの住所情報が初期値として表示される。
		OrderForm orderForm = new OrderForm();
		BeanUtils.copyProperties(user, orderForm);

		// OrderFormのidには「注文ID」ではなく、注文者である「会員ID」を保持させる。
		// 後続の注文登録処理では、このidを使ってOrder EntityのUser情報を設定する。
		orderForm.setId(user.getId());

		// 支払方法は、初期状態ではシステム共通のデフォルト支払方法を設定しておく。
		// ユーザーが支払方法選択画面で変更した場合は、後続の処理でこの値が上書きされる。
		orderForm.setPayMethod(Constant.DEFAULT_PAYMENT_METHOD);

		// 入力途中の注文情報を複数画面にまたがって利用するため、OrderFormをセッションスコープへ保存する。
		// 注文手続きは「届け先入力 → 支払方法選択 → 注文確認 → 注文完了」と画面遷移するため、
		// リクエストスコープではなくセッションに保持している。
		session.setAttribute(ORDER_FORM, orderForm);

		// 前回の入力チェックエラー情報がセッションに残っていると、初期表示時にもエラーが表示されてしまう。
		// 新しく注文手続きを開始するタイミングでは不要なため、ここで削除しておく。
		session.removeAttribute("result");
		return "redirect:/client/order/address/input";
	}

	// ===== 担当: シュエ ジーハン / 届け先入力（入力チェック） =====
	/**
	 * 届け先入力画面を表示します。
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/order/address_input" 届け先入力画面
	 */
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String addressInput(Model model) {
		OrderForm orderForm = (OrderForm) session.getAttribute(ORDER_FORM);
		if (orderForm == null) {
			return "redirect:/syserror";
		}

		// POSTの入力チェックでエラーが発生した場合、PRG(Post-Redirect-Get)形式でこのGETメソッドへ戻ってくる。
		// Redirectを挟むと通常のリクエストスコープのBindingResultは消えてしまうため、
		// addressInputCheckメソッド側で一時的にセッションへ退避したエラー情報をここで取り出す。
		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			
			// Thymeleaf/Springのフォームエラー表示が正しく動作するように、
			// BindingResultを「org.springframework.validation.BindingResult.フォーム名」というキーでModelへ戻す。
			// ここでは画面側のForm名がorderFormであるため、この固定形式のキーを使用している。
			model.addAttribute("org.springframework.validation.BindingResult.orderForm", result);

			// 一度Modelへ戻したエラー情報は再利用しない。
			// 削除しないと、次回正常に画面を開いた場合でも古いエラーが表示される可能性がある。
			session.removeAttribute("result");
		}

		// 画面の入力欄に現在の注文フォーム情報を表示するため、OrderFormをModelへ設定する。
		model.addAttribute(ORDER_FORM, orderForm);
		return "client/order/address_input";
	}

	// ===== 担当: シュエ ジーハン / 届け先入力（入力チェック） =====
	/**
	 * 届け先入力値をチェックし、支払方法選択画面へ遷移します。
	 *
	 * @param form 注文入力フォーム
	 * @param result 入力チェック結果
	 * @return 入力エラーあり: "redirect:/client/order/address/input"、なし: "redirect:/client/order/payment/input"
	 */
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String addressInputCheck(@Valid @ModelAttribute OrderForm form, BindingResult result) {
		OrderForm lastOrderForm = (OrderForm) session.getAttribute(ORDER_FORM);
		if (lastOrderForm == null) {
			return "redirect:/syserror";
		}
		
		// 画面からPOSTされる届け先入力フォームには、入力項目として表示されていない値が含まれない場合がある。
		// 特に会員IDや支払方法は、注文登録・確認の後続処理で必要になるため、
		// POSTされたformに値が入っていない場合は、セッションに保存していた前回のOrderFormから補完する。
		if (form.getId() == null) {
			form.setId(lastOrderForm.getId());
		}
		if (form.getPayMethod() == null) {
			form.setPayMethod(lastOrderForm.getPayMethod());
		}

		if (result.hasErrors()) {
			clearInvalidAddressFields(form, result);
			session.setAttribute(ORDER_FORM, form);
			
			// 入力エラーがある場合は、エラー情報をセッションへ一時退避する。
			// Redirect先のGETメソッド(addressInput)でこのBindingResultをModelへ戻すことで、
			// 入力画面にエラーメッセージを表示できる。
			session.setAttribute("result", createClearedRejectedValueResult(form, result));
			return "redirect:/client/order/address/input";
		}
		
		// 入力チェック結果に関係なく、ユーザーが入力した最新の届け先情報を一度セッションへ保存する。
		// これにより、エラーで入力画面に戻った場合でも、入力済みの値を画面に再表示できる。
		session.setAttribute(ORDER_FORM, form);
		return "redirect:/client/order/payment/input";
	}

	// ===== 担当: シュエ ジーハン / 支払方法選択 =====
	/**
	 * 支払方法選択画面を表示します。
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/order/payment_input" 支払方法選択画面
	 */
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String paymentInput(Model model) {
		OrderForm orderForm = (OrderForm) session.getAttribute(ORDER_FORM);
		if (orderForm == null) {
			return "redirect:/syserror";
		}

		// 支払方法選択画面で、現在セッションに保持されている注文情報を利用できるようModelへ設定する。
		model.addAttribute(ORDER_FORM, orderForm);

		// 画面側で現在選択中の支払方法を初期選択状態にするため、payMethodを個別にModelへ渡す。
		// 住所入力から初めて遷移した場合はデフォルト支払方法、
		// 戻る操作などで再表示された場合は前回選択された支払方法が表示される。
		model.addAttribute("payMethod", orderForm.getPayMethod());
		return "client/order/payment_input";
	}

	// ===== 担当: 秋葉　真穂/ 注文確認 =====
	/**
	 * 支払方法を保存し、注文確認画面へ遷移します。
	 *
	 * @param payMethod 支払方法
	 * @return "redirect:/client/order/check" 注文確認画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheck(@RequestParam Integer payMethod) {
		
		// 秋葉　真穂担当: 選択された支払方法を注文入力フォームへ設定し、セッションへ保存する。
		// 1. セッションからORDER_FORMキーでOrderFormを取得する。
		OrderForm orderForm = (OrderForm) session.getAttribute(ORDER_FORM);
		
		// 2. OrderFormがnullの場合、注文手続きの途中情報が失われているため、システムエラー画面へリダイレクトする。
		if (orderForm == null) {
	        return "redirect:/syserror";
	    }
		
		// 3. 取得できたOrderFormに、画面で選択されたpayMethodをsetPayMethodで設定する。
		orderForm.setPayMethod(payMethod);
		
		// 4. 更新後のOrderFormを再度セッションへ保存する。
		session.setAttribute(ORDER_FORM, orderForm);
		
		// 5. 注文確認画面表示用のGETメソッドへリダイレクトする。
		return "redirect:/client/order/check";
	}

	// ===== 担当: 秋葉　真穂 / 注文確認 =====
	/**
	 * 注文確認画面を表示します。
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/order/check" 注文確認画面
	 */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String orderCheckView(Model model) {
		
		// 秋葉　真穂担当: 買い物かご商品の在庫確認、注文商品Bean生成、合計金額計算を行い画面へ渡す。
		// 実装手順参考メモ:
		// 1. セッションからORDER_FORMキーでOrderFormを取得する。
		OrderForm orderForm = (OrderForm) session.getAttribute(ORDER_FORM);
		
		// 2. OrderFormがnullの場合、注文者情報や届け先情報が確認できないため、システムエラー画面へリダイレクトする。
		if (orderForm == null) {
			return "redirect:/syserror";
			}
		
		// 3. createOrderItemBeansForCheck(model)を呼び出し、買い物かご情報をもとに注文確認画面用のOrderItemBeanリストを生成する。
		// このメソッド内で、在庫切れ商品・在庫不足商品の判定と、買い物かご数量の補正も行われる。
		List<OrderItemBean> orderItemBeans = createOrderItemBeansForCheck(model);
		
		// 注文時点で注文商品すべての在庫が0の場合の考慮
		if (orderItemBeans == null || orderItemBeans.isEmpty()) {
		    model.addAttribute("orderItemBeans", null);
		    return "client/order/check";
		}
		
		// 4. OrderFormをModelへ追加し、画面で届け先情報・支払方法を表示できるようにする。
		model.addAttribute(ORDER_FORM, orderForm);
		
		// 5. OrderItemBeanリストがnullではなく空でもない場合、priceCalcで小計込みの合計金額を計算する。
		if (orderItemBeans != null && !orderItemBeans.isEmpty()) {
			Integer total = priceCalc.orderItemBeanPriceTotalUseSubtotal(orderItemBeans);
			
			// totalをModelへ追加
			model.addAttribute("total", total);
			}
		
		// 6. orderItemBeansをModelへ追加し、注文確認画面へ渡す。
		model.addAttribute("orderItemBeans", orderItemBeans);
		
		// 7. 最後に注文確認画面のView名を返す。
		return "client/order/check";
	}

	// ===== 担当: 秋葉　真穂 / 注文確認 =====
	/**
	 * 注文確認画面または支払方法選択画面から前画面へ戻ります。
	 *
	 * @return "redirect:/client/order/address/input" 届け先入力画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String paymentBack() {
		
		// 秋葉　真穂担当: 設計書の戻り先に従い、届け先入力画面表示処理へリダイレクトする。
		return "redirect:/client/order/address/input";
	}

	// ===== 担当: 秋葉　真穂 / 注文完了 =====
	/**
	 * 注文を確定します。
	 *
	 * @return 在庫エラーあり: "redirect:/client/order/check"、なし: "redirect:/client/order/complete"
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderComplete() {
		
		// 秋葉　真穂担当: 注文確定直前の在庫確認、注文/注文商品登録、セッション情報削除を行う。
		// 注意点: OrderFormのidは会員IDのため、Orderエンティティのidへコピーしないこと。

		// 1. セッションからOrderFormと買い物かご情報(BasketBeanリスト)を取得する。
		OrderForm orderForm = (OrderForm) session.getAttribute(ORDER_FORM);
		List<BasketBean> basketBeans = getBasketBeans();
		
		// 2. OrderFormがnull、買い物かごがnull、または空の場合は、注文に必要な情報が不足しているためシステムエラーへ遷移する。
		if (orderForm == null || basketBeans == null || basketBeans.isEmpty()) {
			return "redirect:/syserror";
		}
		
		// 3. canOrder(basketBeans)を呼び出し、注文確定直前の最新在庫で本当に注文可能か確認する。
		if (!canOrder(basketBeans)) {
			
			// 4. 注文不可の場合は注文確認画面へ戻す
			return "redirect:/client/order/check";
		}
		
		// 5. createOrder(orderForm)を呼び出し、届け先・支払方法・会員情報を持つOrder Entityを生成する。
		Order order = createOrder(orderForm);
		
		// 6. orderRepository.save(order)で注文情報を登録し、保存後のOrderを取得する。
		order = orderRepository.save(order);
		
		// 7. 買い物かご内の商品ごとに、商品情報をDBから取得する。
		for (BasketBean basketBean : basketBeans) {

			Item item = itemRepository.findByIdAndDeleteFlag(
					basketBean.getId(),
					Constant.NOT_DELETED);
			
			// 8. OrderItem Entityを生成し、保存済みOrder、商品、注文数、注文時点の商品単価を設定して保存する。
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setItem(item);
			orderItem.setQuantity(basketBean.getOrderNum());
			
			// 注文時点の価格を保存
			orderItem.setPrice(item.getPrice());
			orderItemRepository.save(orderItem);

			// 9. 注文数分だけItemの在庫数を減らし、itemRepository.save(item)で在庫を更新する。
			item.setStock(item.getStock() - basketBean.getOrderNum());
			itemRepository.save(item);
		}
		
		// 10. 注文登録後は、セッションからORDER_FORMとBASKET_BEANSを削除する。
		session.removeAttribute(ORDER_FORM);
		session.removeAttribute(BASKET_BEANS);
		
		// 11. 注文完了画面表示用のGETメソッドへリダイレクトする。
		// 補足: この処理は注文登録・注文商品登録・在庫更新をまとめて行うため、
		//       実装時は@Transactionalを付与すると、途中でエラーが発生した場合に一括ロールバックできる。
		return "redirect:/client/order/complete";
	}

	// ===== 担当: 秋葉　真穂 / 注文完了 =====
	/**
	 * 注文完了画面を表示します。
	 *
	 * @return "client/order/complete" 注文完了画面
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String orderCompleteFinish() {
		
		// 秋葉　真穂担当: 注文完了画面を表示するための後処理が必要な場合はここに実装する。
		return "client/order/complete";
	}

	/**
	 * 注文情報を生成します。
	 * @return 注文情報Entityクラス
	 *
	 */
	private Order createOrder(OrderForm orderForm) {
		Order order = new Order();

		// OrderFormには、届け先入力画面で入力・確認した注文者情報が保持されている。
		// ここではその内容をOrder Entityへ移し替え、DBのordersテーブルへ保存できる形にする。
		// ただし、OrderFormのidは「会員ID」として使っているため、
		// Order Entity自身のid(注文ID)へはコピーしない。
		// 注文IDはDB側のシーケンス／自動採番に任せる。
		order.setPostalCode(orderForm.getPostalCode());
		order.setAddress(orderForm.getAddress());
		order.setName(orderForm.getName());
		order.setPhoneNumber(orderForm.getPhoneNumber());
		order.setPayMethod(orderForm.getPayMethod());

		// 注文者の会員情報をOrderへ紐付ける。
		// ここでは会員IDだけを持つUser Entityを作成して設定している。
		// これにより、ordersテーブルのuser_idに該当会員のIDが登録される。
		User user = new User();
		user.setId(orderForm.getId());
		order.setUser(user);
		return order;
	}

	private void clearInvalidAddressFields(OrderForm form, BindingResult result) {
		Set<String> invalidFields = new HashSet<String>();
		for (FieldError fieldError : result.getFieldErrors()) {
			invalidFields.add(fieldError.getField());
		}

		if (invalidFields.contains("postalCode")) {
			form.setPostalCode("");
		}
		if (invalidFields.contains("address")) {
			form.setAddress("");
		}
		if (invalidFields.contains("name")) {
			form.setName("");
		}
		if (invalidFields.contains("phoneNumber")) {
			form.setPhoneNumber("");
		}
	}

	private BindingResult createClearedRejectedValueResult(OrderForm form, BindingResult result) {
		BindingResult clearedResult = new BeanPropertyBindingResult(form, result.getObjectName());
		List<ObjectError> errors = new ArrayList<ObjectError>(result.getAllErrors());
		errors.sort(Comparator.comparingInt(this::orderFormFieldOrder));
		for (ObjectError error : errors) {
			if (error instanceof FieldError fieldError) {
				clearedResult.addError(new FieldError(
						fieldError.getObjectName(),
						fieldError.getField(),
						"",
						fieldError.isBindingFailure(),
						fieldError.getCodes(),
						fieldError.getArguments(),
						fieldError.getDefaultMessage()));
			} else {
				clearedResult.addError(error);
			}
		}
		return clearedResult;
	}

	private int orderFormFieldOrder(ObjectError error) {
		if (!(error instanceof FieldError fieldError)) {
			return ORDER_FORM_FIELD_ORDER.length;
		}
		for (int i = 0; i < ORDER_FORM_FIELD_ORDER.length; i++) {
			if (ORDER_FORM_FIELD_ORDER[i].equals(fieldError.getField())) {
				return i;
			}
		}
		return ORDER_FORM_FIELD_ORDER.length;
	}

	/**
	 * 注文確認画面表示用の注文商品Beanを生成します。
	 *
	 * @param model Viewとの値受渡し
	 * @return 注文商品Beanリスト
	 */
	private List<OrderItemBean> createOrderItemBeansForCheck(Model model) {
		// 注文確認画面では、現在の買い物かご情報をもとに表示用の注文商品情報を作成する。
		// 買い物かごが存在しない、または空の場合は、表示すべき注文商品がないためnullを返す。
		List<BasketBean> basketBeans = getBasketBeans();
		if (basketBeans == null || basketBeans.isEmpty()) {
			return null;
		}

		// itemNameListLessThan: 注文数より在庫数が少なかった商品の名前を保持する。
		// itemNameListZero: 在庫切れ、または削除済みで注文対象から外す商品の名前を保持する。
		// updatedBasketBeans: 最新の在庫状況に合わせて数量補正・除外した後の買い物かご情報を保持する。
		// orderItemBeans: 注文確認画面に表示するための商品名・単価・数量・小計などを持つBeanを保持する。
		List<String> itemNameListLessThan = new ArrayList<String>();
		List<String> itemNameListZero = new ArrayList<String>();
		List<BasketBean> updatedBasketBeans = new ArrayList<BasketBean>();
		List<OrderItemBean> orderItemBeans = new ArrayList<OrderItemBean>();

		for (BasketBean basketBean : basketBeans) {
			
			// 買い物かごに入れた時点から注文確認時点までの間に、
			// 商品が削除されたり、在庫数が変わったりしている可能性がある。
			// そのため、BasketBeanの情報だけを信用せず、DBから最新の商品情報を取得して確認する。
			Item item = itemRepository.findByIdAndDeleteFlag(basketBean.getId(), Constant.NOT_DELETED);
			if (item == null || item.getStock() == null || item.getStock() == 0) {
				
				// 商品が存在しない、論理削除済み、または在庫数が0の場合は注文できない。
				// 注文確認画面でユーザーに知らせるため、対象商品名を在庫切れリストへ追加する。
				// continueにより、この商品は更新後の買い物かご・注文商品表示リストには追加しない。
				itemNameListZero.add(basketBean.getName());
				continue;
			}

			// DBから取得した最新の商品ID・商品名・在庫数と、ユーザーが買い物かごで指定した注文数を使って、
			// 確認画面用に買い物かご情報を作り直す。
			BasketBean updatedBasketBean = new BasketBean(
					item.getId(), item.getName(), item.getStock(), basketBean.getOrderNum());
			if (updatedBasketBean.getOrderNum() > item.getStock()) {
				
				// 注文数が現在在庫数を超えている場合、そのままでは注文できない。
				// 注文可能な最大数である現在在庫数まで注文数を自動補正し、
				// 注文確認画面に注意メッセージを表示するため商品名を在庫不足リストへ追加する。
				updatedBasketBean.setOrderNum(item.getStock());
				itemNameListLessThan.add(item.getName());
			}

			// 補正後も注文可能な商品だけを、更新後の買い物かご情報として保持する。
			updatedBasketBeans.add(updatedBasketBean);

			// Item EntityとBasketBeanをもとに、画面表示用のOrderItemBeanを生成する。
			// OrderItemBeanには商品名・価格・数量・小計など、注文確認画面で必要な情報がまとめられる。
			orderItemBeans.add(beanTools.generateOrderItemBean(item, updatedBasketBean));
		}

		// 在庫切れ商品の除外や在庫不足商品の数量補正を反映した買い物かご情報で、セッションを更新する。
		// これにより、確認画面以降の処理では補正後の正しい数量を利用できる。
		saveBasketBeans(updatedBasketBeans);

		// 在庫不足で数量を補正した商品がある場合、画面にメッセージ表示できるようModelへ渡す。
		if (!itemNameListLessThan.isEmpty()) {
			model.addAttribute(ITEM_NAME_LIST_LESS_THAN, itemNameListLessThan);
		}

		// 在庫切れ・削除済みで買い物かごから除外した商品がある場合、画面にメッセージ表示できるようModelへ渡す。
		if (!itemNameListZero.isEmpty()) {
			model.addAttribute(ITEM_NAME_LIST_ZERO, itemNameListZero);
		}
		return orderItemBeans;
	}

	/**
	 * 注文確定可能な在庫数であるかを判定します。
	 *
	 * @param basketBeans 買い物かご情報
	 * @return true: 注文可能、false: 注文不可
	 */
	private boolean canOrder(List<BasketBean> basketBeans) {
		for (BasketBean basketBean : basketBeans) {
			
			// 注文確認画面を表示した後、注文確定ボタンを押すまでの短い間にも、
			// 他ユーザーの注文などによって在庫数が変わる可能性がある。
			// そのため、注文確定直前にもDBから最新の商品情報を取得して再確認する。
			Item item = itemRepository.findByIdAndDeleteFlag(basketBean.getId(), Constant.NOT_DELETED);
			if (basketBean.getOrderNum() == null || basketBean.getOrderNum() <= 0
					|| item == null || item.getStock() == null || item.getStock() < basketBean.getOrderNum()) {
				
				// 注文数が不正、商品が存在しない、在庫数が取得できない、または在庫数が注文数より少ない場合は注文不可。
				return false;
			}
		}
		return true;
	}

	/**
	 * 買い物かごが空であるかを判定します。
	 *
	 * @return true: 空、false: 商品あり
	 */
	private boolean isBasketEmpty() {
		
		// 買い物かご情報はセッションに保存されているため、共通メソッドから取得する。
		// nullまたは空リストの場合は、注文手続きを開始できる商品が存在しないと判定する。
		List<BasketBean> basketBeans = getBasketBeans();
		return basketBeans == null || basketBeans.isEmpty();
	}

	/**
	 * セッションから買い物かご情報を取得します。
	 *
	 * @return 買い物かご情報
	 */
	@SuppressWarnings("unchecked")
	private List<BasketBean> getBasketBeans() {
		
		// HttpSessionから買い物かご情報を取得する。
		// session.getAttributeの戻り値はObject型のため、BasketBeanのListとしてキャストする。
		// 呼び出し元ではnullの可能性も考慮して判定する。
		return (List<BasketBean>) session.getAttribute(BASKET_BEANS);
	}

	/**
	 * 買い物かご情報をセッションへ保存します。
	 *
	 * @param basketBeans 買い物かご情報
	 */
	private void saveBasketBeans(List<BasketBean> basketBeans) {
		
		// 保存対象がnullまたは空の場合は、買い物かごに商品が存在しない状態とみなし、
		// セッションから買い物かご情報自体を削除する。
		if (basketBeans == null || basketBeans.isEmpty()) {
			session.removeAttribute(BASKET_BEANS);
		} else {
			
			// 注文可能な商品が残っている場合は、最新状態の買い物かご情報としてセッションへ保存する。
			session.setAttribute(BASKET_BEANS, basketBeans);
		}
	}

}
