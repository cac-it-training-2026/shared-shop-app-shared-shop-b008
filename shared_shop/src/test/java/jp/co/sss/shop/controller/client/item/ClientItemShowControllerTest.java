package jp.co.sss.shop.controller.client.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

class ClientItemShowControllerTest {

	private ClientItemShowController controller;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private BeanTools beanTools;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		controller = new ClientItemShowController();
		controller.itemRepository = itemRepository;
		controller.beanTools = beanTools;
	}

	@Test
	void showItemAddsUpToFourRelatedItemsToModel() {
		Item currentItem = createItem(10, 3);
		Item relatedItem = createItem(11, 3);
		ItemBean currentItemBean = new ItemBean();
		ItemBean relatedItemBean = new ItemBean();
		List<Item> relatedItems = List.of(relatedItem);
		List<ItemBean> relatedItemBeans = List.of(relatedItemBean);
		Model model = new ConcurrentModel();

		when(itemRepository.findByIdAndDeleteFlag(10, Constant.NOT_DELETED)).thenReturn(currentItem);
		when(itemRepository.findRelatedItems(
				eq(3),
				eq(10),
				eq(Constant.NOT_DELETED),
				eq(PageRequest.of(0, 4))))
				.thenReturn(relatedItems);
		when(beanTools.copyEntityToItemBean(currentItem)).thenReturn(currentItemBean);
		when(beanTools.copyEntityListToItemBeanList(relatedItems)).thenReturn(relatedItemBeans);

		String view = controller.showItem(10, model);

		assertEquals("client/item/detail", view);
		assertEquals(currentItemBean, model.getAttribute("item"));
		assertEquals(relatedItemBeans, model.getAttribute("relatedItems"));
		verify(itemRepository).findRelatedItems(
				3,
				10,
				Constant.NOT_DELETED,
				PageRequest.of(0, 4));
	}

	@Test
	void showItemDisplaysNormallyWhenNoRelatedItemExists() {
		Item currentItem = createItem(10, 3);
		ItemBean currentItemBean = new ItemBean();
		Model model = new ConcurrentModel();

		when(itemRepository.findByIdAndDeleteFlag(10, Constant.NOT_DELETED)).thenReturn(currentItem);
		when(itemRepository.findRelatedItems(
				3,
				10,
				Constant.NOT_DELETED,
				PageRequest.of(0, 4)))
				.thenReturn(Collections.emptyList());
		when(beanTools.copyEntityToItemBean(currentItem)).thenReturn(currentItemBean);
		when(beanTools.copyEntityListToItemBeanList(Collections.emptyList()))
				.thenReturn(Collections.emptyList());

		String view = controller.showItem(10, model);

		assertEquals("client/item/detail", view);
		assertEquals(Collections.emptyList(), model.getAttribute("relatedItems"));
	}

	@Test
	void showItemRedirectsToSystemErrorWhenCurrentItemDoesNotExist() {
		Model model = new ConcurrentModel();
		when(itemRepository.findByIdAndDeleteFlag(99, Constant.NOT_DELETED)).thenReturn(null);

		String view = controller.showItem(99, model);

		assertEquals("redirect:/syserror", view);
		verify(itemRepository, never()).findRelatedItems(
				anyInt(),
				anyInt(),
				anyInt(),
				any(Pageable.class));
	}

	private Item createItem(Integer itemId, Integer categoryId) {
		Category category = new Category();
		category.setId(categoryId);

		Item item = new Item();
		item.setId(itemId);
		item.setCategory(category);
		return item;
	}
}
