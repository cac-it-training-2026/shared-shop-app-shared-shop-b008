package jp.co.sss.shop.controller.client.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.ReviewForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.ReviewRepository;
import jp.co.sss.shop.util.Constant;

/**
 * レビュー登録用コントローラ
 */
@Controller
public class ClientReviewRegistController {

	@Autowired
	ReviewRepository reviewRepository;

	@Autowired
	OrderItemRepository orderItemRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	HttpSession session;

	/**
	 * レビュー入力画面表示
	 *
	 * @param orderItemId 注文商品ID
	 * @param model       Viewとの値受渡し
	 * @return レビュー入力画面
	 */
	@RequestMapping(path = "/client/review/regist/{orderItemId}", method = RequestMethod.GET)
	public String registInput(@PathVariable Integer orderItemId, Model model) {

		// ログインチェック
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		// 注文商品情報を取得
		OrderItem orderItem = orderItemRepository.findById(orderItemId).orElse(null);
		if (orderItem == null) {
			return "redirect:/syserror";
		}

		// ログインユーザーが注文した商品かチェック（水平認可）
		if (!orderItem.getOrder().getUser().getId().equals(userBean.getId())) {
			return "redirect:/syserror";
		}

		// 既にレビュー済みかチェック
		Review existingReview = reviewRepository.findByOrderItemId(orderItemId);
		if (existingReview != null) {
			return "redirect:/client/order/detail/" + orderItem.getOrder().getId();
		}

		ReviewForm form = new ReviewForm();
		form.setOrderItemId(orderItemId);
		form.setItemId(orderItem.getItem().getId());

		model.addAttribute("reviewForm", form);
		model.addAttribute("item", orderItem.getItem());

		return "client/review/regist";
	}

	/**
	 * レビュー登録処理
	 *
	 * @param form   レビューフォーム
	 * @param result 入力チェック結果
	 * @param model  Viewとの値受渡し
	 * @return レビュー登録完了画面
	 */
	@RequestMapping(path = "/client/review/regist", method = RequestMethod.POST)
	public String regist(@Valid @ModelAttribute ReviewForm form, BindingResult result, Model model) {

		// ログインチェック
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		if (result.hasErrors()) {
			Item item = itemRepository.findById(form.getItemId()).orElse(null);
			model.addAttribute("item", item);
			return "client/review/regist";
		}

		// 注文商品情報を取得
		OrderItem orderItem = orderItemRepository.findById(form.getOrderItemId()).orElse(null);
		if (orderItem == null) {
			return "redirect:/syserror";
		}

		// 水平認可チェック
		if (!orderItem.getOrder().getUser().getId().equals(userBean.getId())) {
			return "redirect:/syserror";
		}

		// 重複投稿チェック
		if (reviewRepository.findByOrderItemId(form.getOrderItemId()) != null) {
			return "redirect:/client/order/detail/" + orderItem.getOrder().getId();
		}

		// レビュー登録
		Review review = new Review();
		review.setRating(form.getRating());
		review.setReviewComment(form.getReviewComment());

		User user = new User();
		user.setId(userBean.getId());
		review.setUser(user);

		Item item = new Item();
		item.setId(form.getItemId());
		review.setItem(item);

		review.setOrderItem(orderItem);

		reviewRepository.save(review);

		return "client/review/regist_complete";
	}

	/**
	 * レビュー登録完了画面表示
	 *
	 * @return レビュー登録完了画面
	 */
	@RequestMapping(path = "/client/review/regist_complete", method = RequestMethod.GET)
	public String registComplete() {
		return "client/review/regist_complete";
	}

	/**
	 * レビュー削除処理
	 *
	 * @param id レビューID
	 * @return 商品詳細画面
	 */
	@RequestMapping(path = "/client/review/delete/{id}", method = RequestMethod.POST)
	public String delete(@PathVariable Integer id) {

		// ログインチェック
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		Review review = reviewRepository.findById(id).orElse(null);
		if (review == null) {
			return "redirect:/syserror";
		}

		// 削除権限チェック (本人または管理者)
		if (!review.getUser().getId().equals(userBean.getId()) && userBean.getAuthority() != Constant.AUTH_ADMIN
				&& userBean.getAuthority() != Constant.AUTH_SYSTEM) {
			return "redirect:/syserror";
		}

		Integer itemId = review.getItem().getId();
		reviewRepository.delete(review);

		return "redirect:/client/item/detail/" + itemId;
	}
}
