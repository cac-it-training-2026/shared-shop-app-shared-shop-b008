package jp.co.sss.shop.controller.client.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.entity.ViewHistory;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.ViewHistoryRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

class ClientItemShowControllerTest {

	@InjectMocks
	private ClientItemShowController controller;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private ViewHistoryRepository viewHistoryRepository;

	@Mock
	private BeanTools beanTools;

	private MockHttpSession session;
	private Model model;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		session = new MockHttpSession();
		model = new ConcurrentModel();
	}

	@Test
	void showItemRecordsHistoryAndFetchesRecentItemsWhenLoggedIn() {
		int itemId = 1;
		Item item = new Item();
		item.setId(itemId);
		item.setDeleteFlag(Constant.NOT_DELETED);

		UserBean loginUser = new UserBean();
		loginUser.setId(10);
		session.setAttribute("user", loginUser);

		when(itemRepository.findByIdAndDeleteFlag(itemId, Constant.NOT_DELETED)).thenReturn(item);
		when(beanTools.copyEntityToItemBean(item)).thenReturn(new ItemBean());

		List<Item> recentItems = new ArrayList<>();
		recentItems.add(new Item());
		when(viewHistoryRepository.findItemsByUser(any(User.class), eq(item), eq(PageRequest.of(0, 5)))).thenReturn(recentItems);
		when(beanTools.copyEntityListToItemBeanList(recentItems)).thenReturn(new ArrayList<>());

		String view = controller.showItem(itemId, session, model);

		assertEquals("client/item/detail", view);
		verify(viewHistoryRepository).save(any(ViewHistory.class));
		assertNotNull(model.getAttribute("recentlyViewedItems"));
	}

	@Test
	void showItemDoesNotRecordHistoryWhenNotLoggedIn() {
		int itemId = 1;
		Item item = new Item();
		item.setId(itemId);
		item.setDeleteFlag(Constant.NOT_DELETED);

		when(itemRepository.findByIdAndDeleteFlag(itemId, Constant.NOT_DELETED)).thenReturn(item);
		when(beanTools.copyEntityToItemBean(item)).thenReturn(new ItemBean());

		String view = controller.showItem(itemId, session, model);

		assertEquals("client/item/detail", view);
		assertEquals(null, model.getAttribute("recentlyViewedItems"));
	}
}
