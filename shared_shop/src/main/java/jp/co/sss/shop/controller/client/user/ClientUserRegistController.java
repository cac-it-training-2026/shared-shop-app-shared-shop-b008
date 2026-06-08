package jp.co.sss.shop.controller.client.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員登録機能(一般会員用)のコントローラクラスです。
 *
 * @author SystemShared
 */
//@Controller
@Controller
public class ClientUserRegistController {

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

	// ===== 担当: コグレ / 登録入力（入力チェック） =====
	/**
	 * 新規会員登録フォームを初期化します。
	 *
	 * @return "redirect:/client/user/regist/input" 登録入力画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/user/regist/input/init", method = RequestMethod.GET)
	public String registInputInit() {
		// TODO コグレ担当: 新規UserFormを作成し、セッションへ保存する。
		System.out.println("qwertyu");
		
		session.setAttribute("userForm",new UserForm());
		
	// 新規登録の初期化なので、前回の残ったエラー情報があれば削除しておく
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
		// TODO コグレ担当: セッションの登録フォームを維持し、登録入力画面へ戻す。
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
		// TODO コグレ担当: セッションのUserFormと入力エラー情報を画面へ渡す。
		return "client/user/regist_input";
	}
//今日は天気が悪いです
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
		// TODO コグレ担当: 入力フォームと入力チェック結果をセッションへ保存し、遷移先を判定する。
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
		// TODO コグレ担当: セッションのUserFormを画面へ渡す。
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
		// TODO コグレ担当: UserFormからUserエンティティを作成し、DB登録とログイン状態の設定を行う。
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
		// TODO コグレ担当: 登録完了画面を表示するための後処理が必要な場合はここに実装する。
		return "client/user/regist_complete";
	}
}

