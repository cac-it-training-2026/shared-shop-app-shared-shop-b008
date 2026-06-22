package jp.co.sss.shop.controller.client.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jakarta.validation.Valid;
import java.util.List;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.ReviewForm;
import jp.co.sss.shop.repository.ReviewRepository;
import org.springframework.validation.BindingResult;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * レビュー機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ReviewController {

	/**
	 * レビュー情報リポジトリ
	 */
	@Autowired
	ReviewRepository reviewRepository;

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
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * レビュー入力画面表示
	 *
	 * @param orderId 注文ID
	 * @param itemId  商品ID
	 * @param model   Viewとの値受渡し
	 * @return "client/review/regist_input" レビュー入力画面
	 */
	@RequestMapping(path = "/client/review/regist/input/{orderId}/{itemId}", method = RequestMethod.GET)
	public String registInput(@PathVariable Integer orderId, @PathVariable Integer itemId, Model model) {

		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		// 購入済みかつ発送済みであることを検証
		Order order = orderRepository.findByIdAndUserId(orderId, userBean.getId());
		if (order == null || order.getStatus() == null || order.getStatus() != 1) {
			return "redirect:/client/order/list";
		}

		// 対象商品が含まれているか確認
		boolean hasItem = order.getOrderItemsList().stream()
				.anyMatch(oi -> oi.getItem().getId().equals(itemId));
		if (!hasItem) {
			return "redirect:/client/order/detail/" + orderId;
		}

		Item item = itemRepository.findByIdAndDeleteFlag(itemId, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/client/order/detail/" + orderId;
		}

		ReviewForm form = new ReviewForm();
		form.setOrderId(orderId);
		form.setItemId(itemId);
		form.setRating(5); // デフォルト評価

		model.addAttribute("reviewForm", form);
		model.addAttribute("item", beanTools.copyEntityToItemBean(item));

		return "client/review/regist_input";
	}

	/**
	 * レビュー登録実行
	 *
	 * @param form   レビューフォーム
	 * @param result バリデーション結果
	 * @param model  Viewとの値受渡し
	 * @return "client/review/regist_complete" レビュー投稿完了画面
	 */
	@RequestMapping(path = "/client/review/regist/complete", method = RequestMethod.POST)
	public String registComplete(@Valid @ModelAttribute ReviewForm form, BindingResult result, Model model) {

		if (result.hasErrors()) {
			Item item = itemRepository.findByIdAndDeleteFlag(form.getItemId(), Constant.NOT_DELETED);
			model.addAttribute("item", beanTools.copyEntityToItemBean(item));
			return "client/review/regist_input";
		}

		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		// 再度検証
		Order order = orderRepository.findByIdAndUserId(form.getOrderId(), userBean.getId());
		if (order == null || order.getStatus() == null || order.getStatus() != 1) {
			return "redirect:/client/order/list";
		}

		Item item = itemRepository.findByIdAndDeleteFlag(form.getItemId(), Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/client/order/detail/" + form.getOrderId();
		}

		Review review = new Review();
		User user = new User();
		user.setId(userBean.getId());
		review.setUser(user);
		review.setItem(item);
		review.setOrder(order);
		review.setRating(form.getRating());
		review.setComment(form.getComment());

		reviewRepository.save(review);

		model.addAttribute("itemId", form.getItemId());

		return "client/review/regist_complete";
	}

	/**
	 * レビュー一覧表示
	 *
	 * @param itemId   商品ID
	 * @param sortType 並び替え種別 (1:新しい順, 2:評価高い順, 3:評価低い順)
	 * @param model    Viewとの値受渡し
	 * @return "client/review/list" レビュー一覧画面
	 */
	@RequestMapping(path = "/client/review/list/{itemId}", method = RequestMethod.GET)
	public String showReviewList(@PathVariable Integer itemId,
			@RequestParam(name = "sortType", defaultValue = "1") Integer sortType, Model model) {

		Item item = itemRepository.findByIdAndDeleteFlag(itemId, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/";
		}

		List<Review> reviews;
		if (sortType == 2) {
			reviews = reviewRepository.findByItemIdOrderByRatingDescCreatedDateDesc(itemId);
		} else if (sortType == 3) {
			reviews = reviewRepository.findByItemIdOrderByRatingAscCreatedDateDesc(itemId);
		} else {
			reviews = reviewRepository.findByItemIdOrderByCreatedDateDesc(itemId);
		}

		Double averageRating = reviewRepository.getAverageRatingByItemId(itemId);
		Long reviewCount = reviewRepository.countByItemId(itemId);

		model.addAttribute("item", beanTools.copyEntityToItemBean(item));
		model.addAttribute("reviews", reviews);
		model.addAttribute("averageRating", averageRating);
		model.addAttribute("reviewCount", reviewCount);
		model.addAttribute("sortType", sortType);

		return "client/review/list";
	}

	/**
	 * レビュー削除実行
	 *
	 * @param id     レビューID
	 * @param itemId 商品ID（リダイレクト用）
	 * @return レビュー一覧画面へリダイレクト
	 */
	@RequestMapping(path = "/client/review/delete/{id}", method = RequestMethod.POST)
	public String deleteReview(@PathVariable Integer id, @RequestParam Integer itemId) {

		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		Review review = reviewRepository.findById(id).orElse(null);
		if (review == null) {
			return "redirect:/client/review/list/" + itemId;
		}

		// 投稿者本人または管理者権限のチェック
		if (review.getUser().getId().equals(userBean.getId()) || userBean.getAuthority() == 1) {
			reviewRepository.delete(review);
		}

		return "redirect:/client/review/list/" + itemId;
	}

}
