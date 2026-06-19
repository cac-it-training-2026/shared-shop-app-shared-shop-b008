package jp.co.sss.shop.controller.login;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BindingResult;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.LoginHistory;
import jp.co.sss.shop.form.LoginForm;
import jp.co.sss.shop.repository.LoginHistoryRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

import jakarta.servlet.http.HttpSession;

class LoginControllerHistoryTest {

	@InjectMocks
	private LoginController controller;

	@Mock
	private UserRepository userRepository;

	@Mock
	private LoginHistoryRepository loginHistoryRepository;

	@Mock
	private HttpSession session;

	@Mock
	private BindingResult result;

	private MockHttpServletRequest request;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		request = new MockHttpServletRequest();
	}

	@Test
	void doLoginRecordsHistoryOnSuccess() {
		LoginForm form = new LoginForm();
		form.setEmail("test@example.com");
		form.setPassword("password");

		UserBean userBean = new UserBean();
		userBean.setId(1);
		userBean.setAuthority(Constant.AUTH_CLIENT);

		when(session.getAttribute("user")).thenReturn(userBean);
		when(result.hasErrors()).thenReturn(false);
		request.setRemoteAddr("127.0.0.1");

		String view = controller.doLogin(form, result, request);

		assertEquals("redirect:/", view);

		ArgumentCaptor<LoginHistory> captor = ArgumentCaptor.forClass(LoginHistory.class);
		verify(loginHistoryRepository).save(captor.capture());

		LoginHistory savedHistory = captor.getValue();
		assertEquals(1, savedHistory.getUser().getId());
		assertEquals("127.0.0.1", savedHistory.getIpAddress());
	}
}
