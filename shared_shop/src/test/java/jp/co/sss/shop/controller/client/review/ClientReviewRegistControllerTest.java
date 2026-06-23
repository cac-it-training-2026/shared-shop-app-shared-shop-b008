package jp.co.sss.shop.controller.client.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.ReviewForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.ReviewRepository;

class ClientReviewRegistControllerTest {

	@InjectMocks
	private ClientReviewRegistController controller;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private OrderItemRepository orderItemRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private BindingResult bindingResult;

	private MockHttpSession session;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		session = new MockHttpSession();
		controller.session = session;
	}

	@Test
	void registInputDisplaysFormForPurchasedItem() {
		UserBean loginUser = new UserBean();
		loginUser.setId(1);
		session.setAttribute("user", loginUser);

		OrderItem orderItem = createOrderItem(10, 1, 100);
		when(orderItemRepository.findById(10)).thenReturn(Optional.of(orderItem));
		when(reviewRepository.findByOrderItemId(10)).thenReturn(null);

		Model model = new ConcurrentModel();
		String view = controller.registInput(10, model);

		assertEquals("client/review/regist", view);
		ReviewForm form = (ReviewForm) model.getAttribute("reviewForm");
		assertEquals(10, form.getOrderItemId());
		assertEquals(100, form.getItemId());
	}

	@Test
	void registSavesNewReview() {
		UserBean loginUser = new UserBean();
		loginUser.setId(1);
		session.setAttribute("user", loginUser);

		ReviewForm form = new ReviewForm();
		form.setOrderItemId(10);
		form.setItemId(100);
		form.setRating(5);
		form.setReviewComment("Great!");

		OrderItem orderItem = createOrderItem(10, 1, 100);
		when(orderItemRepository.findById(10)).thenReturn(Optional.of(orderItem));
		when(reviewRepository.findByOrderItemId(10)).thenReturn(null);
		when(bindingResult.hasErrors()).thenReturn(false);

		Model model = new ConcurrentModel();
		String view = controller.regist(form, bindingResult, model);

		assertEquals("client/review/regist_complete", view);

		ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
		verify(reviewRepository).save(reviewCaptor.capture());
		Review savedReview = reviewCaptor.getValue();
		assertEquals(5, savedReview.getRating());
		assertEquals("Great!", savedReview.getReviewComment());
		assertEquals(1, savedReview.getUser().getId());
		assertEquals(100, savedReview.getItem().getId());
		assertEquals(10, savedReview.getOrderItem().getId());
	}

	@Test
	void deleteRemovesReviewIfAuthor() {
		UserBean loginUser = new UserBean();
		loginUser.setId(1);
		session.setAttribute("user", loginUser);

		Review review = new Review();
		review.setId(50);
		User user = new User();
		user.setId(1);
		review.setUser(user);
		Item item = new Item();
		item.setId(100);
		review.setItem(item);

		when(reviewRepository.findById(50)).thenReturn(Optional.of(review));

		String view = controller.delete(50);

		assertEquals("redirect:/client/item/detail/100", view);
		verify(reviewRepository).delete(review);
	}

	private OrderItem createOrderItem(Integer id, Integer userId, Integer itemId) {
		OrderItem orderItem = new OrderItem();
		orderItem.setId(id);

		Order order = new Order();
		User user = new User();
		user.setId(userId);
		order.setUser(user);
		orderItem.setOrder(order);

		Item item = new Item();
		item.setId(itemId);
		orderItem.setItem(item);

		return orderItem;
	}
}
