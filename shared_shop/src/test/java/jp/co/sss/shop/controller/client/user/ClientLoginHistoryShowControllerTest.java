package jp.co.sss.shop.controller.client.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.LoginHistory;
import jp.co.sss.shop.repository.LoginHistoryRepository;

import jakarta.servlet.http.HttpSession;

class ClientLoginHistoryShowControllerTest {

	@InjectMocks
	private ClientLoginHistoryShowController controller;

	@Mock
	private LoginHistoryRepository loginHistoryRepository;

	@Mock
	private HttpSession session;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void showLoginHistoryAddsPastYearHistoriesToModel() {
		UserBean loginUser = new UserBean();
		loginUser.setId(1);
		when(session.getAttribute("user")).thenReturn(loginUser);

		List<LoginHistory> histories = new ArrayList<>();
		histories.add(new LoginHistory());
		when(loginHistoryRepository.findByUserIdAndLoginDateTimeGreaterThanEqualOrderByLoginDateTimeDesc(eq(1), any(Timestamp.class))).thenReturn(histories);

		Model model = new ConcurrentModel();
		String view = controller.showLoginHistory(model);

		assertEquals("client/user/login_history_list", view);
		assertNotNull(model.getAttribute("loginHistories"));
		assertEquals(histories, model.getAttribute("loginHistories"));
	}
}
