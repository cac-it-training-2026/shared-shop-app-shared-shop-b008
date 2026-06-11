package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員退会機能(一般会員用)のコントローラクラスです。
 *
 * @author SystemShared
 */
@Controller
public class ClientUserDeleteController {

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

	// ===== 担当: 金宮 永茉 / 削除確認 =====
	/**
	 * 退会確認画面表示用の会員フォームを初期化します。
	 *
	 * @return "redirect:/client/user/delete/check" 退会確認画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)
	public String deleteCheckInit() {
		// TODO 金宮 永茉担当: セッションのログイン会員情報を元に退会確認用フォームを作成し、セッションへ保存する。
		UserBean userBean = (UserBean) session.getAttribute("user");
		UserForm userForm = new UserForm();
		BeanUtils.copyProperties(userBean, userForm);
		session.setAttribute("userForm", userForm);
		return "redirect:/client/user/delete/check";
	}

	// ===== 担当: 金宮 永茉 / 削除確認 =====
	/**
	 * 退会確認画面を表示します。
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/delete_check" 退会確認画面
	 */
	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.GET)
	public String deleteCheck(Model model) {
		// TODO 金宮 永茉担当: セッションの退会確認用フォームを画面へ渡す。
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		model.addAttribute("userForm", userForm);
		return "client/user/delete_check";
	}

	// ===== 担当: 金宮 永茉 / 削除完了 =====
	/**
	 * ログイン会員の退会処理を行います。
	 *
	 * @return "redirect:/client/user/delete/complete" 退会完了画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.POST)
	public String deleteComplete() {
		// TODO 金宮 永茉担当: 会員の削除フラグ更新、セッション破棄を行う。
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		User user = userRepository.getReferenceById(userForm.getId());
		user.setDeleteFlag(1);
		userRepository.save(user);
		session.invalidate();
		return "redirect:/client/user/delete/complete";
	}

	// ===== 担当: 金宮 永茉 / 削除完了 =====
	/**
	 * 退会完了画面を表示します。
	 *
	 * @return "client/user/delete_complete" 退会完了画面
	 */
	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.GET)
	public String deleteCompleteFinish() {
		// TODO 金宮 永茉担当: 退会完了画面を表示するための後処理が必要な場合はここに実装する。
		return "client/user/delete_complete";
	}
}
