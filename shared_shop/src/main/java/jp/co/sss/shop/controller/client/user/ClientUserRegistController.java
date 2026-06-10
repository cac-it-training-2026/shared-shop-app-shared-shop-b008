package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.BeanTools;

/**
 * 会員登録機能(一般会員用)のコントローラクラスです。
 *
 * @author SystemShared
 */
@Controller
public class ClientUserRegistController {

	private static final String[] USER_FORM_FIELD_ORDER = {
			"email", "password", "name", "postalCode", "address", "phoneNumber"
	};

	/**
	 * 会員情報リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	// ===== 担当: コグレ / 登録入力（入力チェック） =====
	/**
	 * 新規会員登録フォームを初期化します。
	 *
	 * @return "redirect:/client/user/regist/input" 登録入力画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/user/regist/input/init", method = RequestMethod.GET)
	public String registInputInit() {

		// 新規UserFormを作成し、セッションへ保存する
		session.setAttribute("userForm", new UserForm());

		// 前回の残ったエラー情報があればクリアする
		session.removeAttribute("result");

		return "redirect:/client/user/regist/input";
	}

	// ===== 担当: コグレ / 登録入力（入力チェック） =====
	/**
	 * 登録入力画面へ戻ります。
	 *
	 * @return "redirect:/client/user/regist/input" 登録入力画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.POST)
	public String registInputBack() {
		return "redirect:/client/user/regist/input";
	}

	// ===== 担当: コグレ / 登録入力（入力チェック） =====
	/**
	 * 登録入力画面を表示します。
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/regist_input" 登録入力画面
	 */
	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.GET)
	public String registInput(Model model) {

		// セッションからUserFormを取得しモデルに設定
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		model.addAttribute("userForm", userForm);

		// セッションに入力エラー情報（BindingResult）があればモデルに設定
		if (session.getAttribute("result") != null) {
			model.addAttribute("org.springframework.validation.BindingResult.userForm", session.getAttribute("result"));
		}

		return "client/user/regist_input";
	}

	// ===== 担当: コグレ / 登録確認 =====
	/**
	 * 登録入力値をチェックし、登録確認画面へ遷移します。
	 *
	 * @param form 会員入力フォーム
	 * @param result 入力チェック結果
	 * @return 入力エラーあり: "redirect:/client/user/regist/input"、なし: "redirect:/client/user/regist/check"
	 */
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.POST)
	public String registInputCheck(@Valid @ModelAttribute UserForm form, BindingResult result) {

		// 入力フォームとエラーチェック結果をセッションに保存
		session.setAttribute("userForm", form);

		if (result.hasErrors()) {
			session.setAttribute("result", createSortedBindingResult(form, result));
			return "redirect:/client/user/regist/input";
		}

		// エラーがない場合はセッションのエラー情報をクリア
		session.removeAttribute("result");

		return "redirect:/client/user/regist/check";
	}

	// ===== 担当: コグレ / 登録確認 =====
	/**
	 * 登録確認画面を表示します。
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/regist_check" 登録確認画面
	 */
	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.GET)
	public String registCheck(Model model) {

		// セッションのUserFormを画面へ渡す
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		model.addAttribute("userForm", userForm);

		return "client/user/regist_check";
	}

	// ===== 担当: コグレ / 登録完了 =====
	/**
	 * 会員情報を登録します。
	 *
	 * @return "redirect:/client/user/regist/complete" 登録完了画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.POST)
	public String registComplete() {

		// セッションから入力フォームを取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		// Userエンティティを作成し、フォームのデータをコピーしてDBに保存
		User user = new User();
		//beanTools.copyProperties(user, userBean);
		
		// 権限などの初期設定が必要であればここで設定（一般会員: 2など）
		user.setAuthority(2); 
		userRepository.save(user);

		// 登録したユーザー情報で自動ログイン状態（セッションにUserBeanを保存）にする
		UserBean userBean = new UserBean();
		//beanTools.copyProperties(user, userBean);
		session.setAttribute("user", userBean);

		// 使い終わった登録用フォームのセッション情報をクリア
		session.removeAttribute("userForm");

		return "redirect:/client/user/regist/complete";
	}

	// ===== 担当: コグレ / 登録完了 =====
	/**
	 * 登録完了画面を表示します。
	 *
	 * @return "client/user/regist_complete" 登録完了画面
	 */
	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.GET)
	public String registCompleteFinish() {
		return "client/user/regist_complete";
	}

	private BindingResult createSortedBindingResult(UserForm form, BindingResult result) {
		BindingResult sortedResult = new BeanPropertyBindingResult(form, result.getObjectName());
		List<ObjectError> errors = new ArrayList<ObjectError>(result.getAllErrors());
		errors.sort(Comparator.comparingInt(this::userFormFieldOrder));
		for (ObjectError error : errors) {
			sortedResult.addError(error);
		}
		return sortedResult;
	}

	private int userFormFieldOrder(ObjectError error) {
		if (!(error instanceof FieldError fieldError)) {
			return 0;
		}
		for (int i = 0; i < USER_FORM_FIELD_ORDER.length; i++) {
			if (USER_FORM_FIELD_ORDER[i].equals(fieldError.getField())) {
				return i;
			}
		}
		return USER_FORM_FIELD_ORDER.length;
	}
}
