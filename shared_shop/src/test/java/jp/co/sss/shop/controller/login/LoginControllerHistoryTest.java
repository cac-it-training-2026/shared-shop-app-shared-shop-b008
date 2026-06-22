package jp.co.sss.shop.controller.login;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.LoginHistory;
import jp.co.sss.shop.form.LoginForm;
import jp.co.sss.shop.repository.LoginHistoryRepository;
import jp.co.sss.shop.repository.UserRepository;

public class LoginControllerHistoryTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoginHistoryRepository loginHistoryRepository;

    @Mock
    private HttpSession session;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    public void testDoLogin_RecordsHistoryOnSuccess() throws Exception {
        // Arrange
        LoginForm form = new LoginForm();
        form.setEmail("test@example.com");
        form.setPassword("password");

        UserBean userBean = new UserBean();
        userBean.setId(1);
        userBean.setAuthority(2); // Client

        when(session.getAttribute("user")).thenReturn(userBean);

        // Act & Assert
        mockMvc.perform(post("/login")
                .flashAttr("loginForm", form))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(loginHistoryRepository, times(1)).save(any(LoginHistory.class));
    }
}
