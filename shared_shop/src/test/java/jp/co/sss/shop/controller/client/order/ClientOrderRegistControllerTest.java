package jp.co.sss.shop.controller.client.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;
import jp.co.sss.shop.util.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

class ClientOrderRegistControllerTest {

    @InjectMocks
    private ClientOrderRegistController controller;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BeanTools beanTools;

    @Mock
    private PriceCalc priceCalc;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        session = new MockHttpSession();
        controller.session = session;
    }

    @Test
    void orderComplete_Success_RedirectsToComplete() {
        OrderForm orderForm = new OrderForm();
        orderForm.setId(1);
        orderForm.setPostalCode("1234567");
        orderForm.setAddress("Test Address");
        orderForm.setName("Test Name");
        orderForm.setPhoneNumber("09012345678");
        orderForm.setPayMethod(1);
        session.setAttribute("orderForm", orderForm);

        List<BasketBean> basketBeans = new ArrayList<>();
        basketBeans.add(new BasketBean(1, "Test Item", 100, 1));
        session.setAttribute("basketBeans", basketBeans);

        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setStock(10);
        item.setPrice(100);
        when(itemRepository.findByIdAndDeleteFlag(1, Constant.NOT_DELETED)).thenReturn(item);

        Order order = new Order();
        order.setId(100);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        String view = controller.orderComplete();

        assertEquals("redirect:/client/order/complete", view);
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void addressInputCheck_DeliveryDate_RangeError_Before() {
        OrderForm lastForm = new OrderForm();
        session.setAttribute("orderForm", lastForm);

        OrderForm form = new OrderForm();
        LocalDate today = LocalDate.now();
        form.setDeliveryDate(Date.valueOf(today.plusDays(2))); // 2 days later (invalid)

        BindingResult result = new BeanPropertyBindingResult(form, "orderForm");

        String view = controller.addressInputCheck(form, result);

        assertEquals("redirect:/client/order/address/input", view);
        assertTrue(result.hasFieldErrors("deliveryDate"));
        assertEquals("orderForm.deliveryDate.invalid", result.getFieldError("deliveryDate").getCode());
    }

    @Test
    void addressInputCheck_DeliveryDate_RangeError_After() {
        OrderForm lastForm = new OrderForm();
        session.setAttribute("orderForm", lastForm);

        OrderForm form = new OrderForm();
        LocalDate today = LocalDate.now();
        form.setDeliveryDate(Date.valueOf(today.plusDays(15))); // 15 days later (invalid)

        BindingResult result = new BeanPropertyBindingResult(form, "orderForm");

        String view = controller.addressInputCheck(form, result);

        assertEquals("redirect:/client/order/address/input", view);
        assertTrue(result.hasFieldErrors("deliveryDate"));
        assertEquals("orderForm.deliveryDate.invalid", result.getFieldError("deliveryDate").getCode());
    }

    @Test
    void addressInputCheck_DeliveryDate_Valid() {
        OrderForm lastForm = new OrderForm();
        session.setAttribute("orderForm", lastForm);

        OrderForm form = new OrderForm();
        LocalDate today = LocalDate.now();
        form.setDeliveryDate(Date.valueOf(today.plusDays(3))); // 3 days later (valid)

        BindingResult result = new BeanPropertyBindingResult(form, "orderForm");

        String view = controller.addressInputCheck(form, result);

        assertEquals("redirect:/client/order/payment/input", view);
        assertNull(result.getFieldError("deliveryDate"));
    }

    @Test
    void addressInputCheck_DeliveryDate_Null_Valid() {
        OrderForm lastForm = new OrderForm();
        session.setAttribute("orderForm", lastForm);

        OrderForm form = new OrderForm();
        form.setDeliveryDate(null); // Not specified (valid)

        BindingResult result = new BeanPropertyBindingResult(form, "orderForm");

        String view = controller.addressInputCheck(form, result);

        assertEquals("redirect:/client/order/payment/input", view);
        assertNull(result.getFieldError("deliveryDate"));
    }
}
