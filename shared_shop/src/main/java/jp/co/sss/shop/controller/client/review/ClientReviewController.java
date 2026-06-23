package jp.co.sss.shop.controller.client.review;

import java.util.List;
import java.util.Optional;

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
import jp.co.sss.shop.util.Constant;

/**
 * レビュー投稿機能のコントローラクラス
 */
@Controller
public class ClientReviewController {

	@Autowired
	ReviewRepository reviewRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	OrderItemRepository orderItemRepository;

	@Autowired
	HttpSession session;

	/**
	 * レビュー一覧表示（全件）
	 */
	@RequestMapping(path = "/client/review/list/{itemId}", method = { RequestMethod.GET, RequestMethod.POST })
	public String showReviewList(@PathVariable Integer itemId, @RequestParam(name = "sortType", defaultValue = "1") Integer sortType, Model model) {
		Item item = itemRepository.findByIdAndDeleteFlag(itemId, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/syserror";
		}

		List<Review> reviews;
		if (sortType == 2) {
			// 評価が高い順
			reviews = reviewRepository.findByItemIdOrderByRatingDescInsertDateDesc(itemId);
		} else if (sortType == 3) {
			// 評価が低い順
			reviews = reviewRepository.findByItemIdOrderByRatingAscInsertDateDesc(itemId);
		} else {
			// 新しい順（デフォルト）
			reviews = reviewRepository.findByItemIdOrderByInsertDateDesc(itemId);
		}

		Double avgRating = reviewRepository.getAvgRatingByItemId(itemId);
		Long reviewCount = reviewRepository.getReviewCountByItemId(itemId);

		model.addAttribute("item", item);
		model.addAttribute("reviews", reviews);
		model.addAttribute("avgRating", avgRating);
		model.addAttribute("reviewCount", reviewCount);
		model.addAttribute("sortType", sortType);

		return "client/review/list";
	}

	/**
	 * レビュー入力画面表示
	 */
	@RequestMapping(path = "/client/review/regist/input", method = RequestMethod.POST)
	public String registInput(@ModelAttribute ReviewForm form, Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		// 購入済みチェック
		Optional<OrderItem> orderItemOpt = orderItemRepository.findById(form.getOrderItemId());
		if (orderItemOpt.isEmpty() || !orderItemOpt.get().getOrder().getUser().getId().equals(userBean.getId())) {
			return "redirect:/syserror";
		}

		// 重複投稿チェック
		if (reviewRepository.existsByUserIdAndOrderItemId(userBean.getId(), form.getOrderItemId())) {
			return "redirect:/syserror";
		}

		Item item = itemRepository.findByIdAndDeleteFlag(form.getItemId(), Constant.NOT_DELETED);
		model.addAttribute("item", item);
		model.addAttribute("reviewForm", form);

		return "client/review/regist_input";
	}

	/**
	 * レビュー投稿処理
	 */
	@RequestMapping(path = "/client/review/regist/complete", method = RequestMethod.POST)
	public String registComplete(@Valid @ModelAttribute ReviewForm form, BindingResult result, Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		if (result.hasErrors()) {
			Item item = itemRepository.findByIdAndDeleteFlag(form.getItemId(), Constant.NOT_DELETED);
			model.addAttribute("item", item);
			model.addAttribute("reviewForm", form);
			return "client/review/regist_input";
		}

		// 購入済み・重複チェック（サーバー側）
		Optional<OrderItem> orderItemOpt = orderItemRepository.findById(form.getOrderItemId());
		if (orderItemOpt.isEmpty() || !orderItemOpt.get().getOrder().getUser().getId().equals(userBean.getId())
				|| reviewRepository.existsByUserIdAndOrderItemId(userBean.getId(), form.getOrderItemId())) {
			return "redirect:/syserror";
		}

		Review review = new Review();
		User user = new User();
		user.setId(userBean.getId());
		review.setUser(user);

		Item item = new Item();
		item.setId(form.getItemId());
		review.setItem(item);

		OrderItem orderItem = new OrderItem();
		orderItem.setId(form.getOrderItemId());
		review.setOrderItem(orderItem);

		review.setRating(form.getRating());
		review.setReviewComment(form.getReviewComment());

		reviewRepository.save(review);

		return "client/review/regist_complete";
	}

	/**
	 * レビュー削除処理
	 */
	@RequestMapping(path = "/client/review/delete/{id}", method = RequestMethod.POST)
	public String deleteReview(@PathVariable Integer id, @RequestParam Integer itemId, Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/login";
		}

		Optional<Review> reviewOpt = reviewRepository.findById(id);
		if (reviewOpt.isEmpty()) {
			return "redirect:/syserror";
		}

		Review review = reviewOpt.get();

		// 削除権限チェック（本人またはADMINユーザー）
		boolean isAdmin = userBean.getAuthority() == Constant.AUTH_ADMIN || userBean.getAuthority() == Constant.AUTH_SYSTEM;
		if (!review.getUser().getId().equals(userBean.getId()) && !isAdmin) {
			return "redirect:/syserror";
		}

		reviewRepository.delete(review);

		return "redirect:/client/item/detail/" + itemId;
	}
}
