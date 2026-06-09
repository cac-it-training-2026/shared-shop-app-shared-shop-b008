package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;

/**
 * 注文表示機能(一般会員用)のコントローラクラスです。
 *
 * @author SystemShared
 */
@Controller
public class ClientOrderShowController {

	/**
	 * 注文情報リポジトリ
	 */
	@Autowired
	OrderRepository orderRepository;

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

	// ===== 担当: 秋葉 真穂 / 注文一覧 =====
	/**
	 * ログイン会員の注文一覧画面を表示します。
	 *
	 * @param model Viewとの値受渡し
	 * @param pageable ページング情報
	 * @return "client/order/list" 注文一覧画面
	 */
	@RequestMapping(path = "/client/order/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String showOrderList(Model model, Pageable pageable) {
		// TODO 秋葉 真穂担当: ログイン会員IDを条件に注文一覧を取得し、注文Beanリストを画面へ渡す。
		System.out.println("★★★ showOrderList開始 ★★★");
		// ログイン会員取得
		UserBean user = (UserBean) session.getAttribute("user");
		// 注文情報を注文日時の新しい順に取得
	    Page<Order> orders = orderRepository.findByUserIdOrderByInsertDateDescIdDesc(user.getId(), pageable);
	    // Beanクラスにデータ代入
	    List<OrderBean> orderBeans = new ArrayList<>();

	    for (Order order : orders.getContent()) {

	        OrderBean bean = new OrderBean();

	        bean.setId(order.getId());
	        // entityクラスのDate型をString型に変更
	        bean.setInsertDate(order.getInsertDate().toString());
	        bean.setPayMethod(order.getPayMethod());
	        // 合計金額の計算
	        int total = 0;
	        for (OrderItem orderItem : order.getOrderItemsList()) {
	            total += orderItem.getPrice() * orderItem.getQuantity();
	        }
	        bean.setTotal(total);

	        orderBeans.add(bean);
	    }
	    // リクエストスコープに保存
	    model.addAttribute("orders", orderBeans);
		return "client/order/list";
	}

	// ===== 担当: 秋葉 真穂 / 注文詳細 =====
	/**
	 * ログイン会員の注文詳細画面を表示します。
	 *
	 * @param id 注文ID
	 * @param model Viewとの値受渡し
	 * @return "client/order/detail" 注文詳細画面
	 */
	@RequestMapping(path = "/client/order/detail/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String showOrder(@PathVariable Integer id, Model model) {
		// TODO 秋葉 真穂担当: ログイン会員の注文であることを確認し、注文詳細と注文商品Beanリストを画面へ渡す。
		return "client/order/detail";
	}
}
