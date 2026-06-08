package jp.co.sss.shop.controller.client.user;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員詳細表示機能(一般会員用)のコントローラクラスです。
 *
 * @author SystemShared
 */
@Controller
public class ClientUserShowController {

	/**
	 * 会員情報リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	// ===== 担当: シュエ ジーハン / 会員詳細表示 =====
	/**
	 * ログイン会員の詳細画面を表示します。
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/detail" 会員詳細画面
	 */
	@RequestMapping(path = "/client/user/detail", method = { RequestMethod.GET, RequestMethod.POST })
	public String showUser(Model model) {
		// TODO シュエ ジーハン担当: セッションのログイン会員IDを条件に会員情報を取得し、userBeanとして画面へ渡す。
		return "client/user/detail";
	}
}
