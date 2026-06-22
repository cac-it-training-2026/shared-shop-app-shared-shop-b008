package jp.co.sss.shop.controller.client.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
	 * 注文商品情報リポジトリ
	 */
	@Autowired
	OrderItemRepository orderItemRepository;

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
	 * @param orderItemId 注文商品ID
	 * @param itemId      商品ID
	 * @param model       Viewとの値受渡し
	 * @return "client/review/regist_input" レビュー入力画面
	 */
	@RequestMapping(path = "/client/review/regist/input/{orderItemId}/{itemId}", method = RequestMethod.GET)
	public String registInput(@PathVariable Integer orderItemId, @PathVariable Integer itemId, Model model) {

		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		// 注文商品情報を取得
		OrderItem orderItem = orderItemRepository.findById(orderItemId).orElse(null);
		if (orderItem == null || !orderItem.getItem().getId().equals(itemId)
				|| !orderItem.getOrder().getUser().getId().equals(userBean.getId())) {
			return "redirect:/client/order/list";
		}

		// 発送済みであることを検証
		if (orderItem.getOrder().getStatus() == null || orderItem.getOrder().getStatus() != 1) {
			return "redirect:/client/order/detail/" + orderItem.getOrder().getId();
		}

		Item item = itemRepository.findByIdAndDeleteFlag(itemId, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/client/order/detail/" + orderItem.getOrder().getId();
		}

		ReviewForm form = new ReviewForm();
		form.setOrderItemId(orderItemId);
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
		OrderItem orderItem = orderItemRepository.findById(form.getOrderItemId()).orElse(null);
		if (orderItem == null || !orderItem.getItem().getId().equals(form.getItemId())
				|| !orderItem.getOrder().getUser().getId().equals(userBean.getId())
				|| orderItem.getOrder().getStatus() == null || orderItem.getOrder().getStatus() != 1) {
			return "redirect:/client/order/list";
		}

		Item item = itemRepository.findByIdAndDeleteFlag(form.getItemId(), Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/client/order/detail/" + orderItem.getOrder().getId();
		}

		Review review = new Review();
		User user = new User();
		user.setId(userBean.getId());
		review.setUser(user);
		review.setItem(item);
		review.setOrderItem(orderItem);
		review.setRating(form.getRating());
		review.setReviewComment(form.getReviewComment());

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
			reviews = reviewRepository.findByItemIdOrderByRatingDescInsertDateDesc(itemId);
		} else if (sortType == 3) {
			reviews = reviewRepository.findByItemIdOrderByRatingAscInsertDateDesc(itemId);
		} else {
			reviews = reviewRepository.findByItemIdOrderByInsertDateDesc(itemId);
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
