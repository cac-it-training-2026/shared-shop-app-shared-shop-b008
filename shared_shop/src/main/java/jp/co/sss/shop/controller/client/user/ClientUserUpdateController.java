package jp.co.sss.shop.controller.client.user;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員変更機能(一般会員用)のコントローラクラスです。
 *
 * @author SystemShared
 */
@Controller
public class ClientUserUpdateController {

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

	// ===== 担当: 金宮 永茉 / 変更入力（入力チェック） =====
	/**
	 * 変更入力画面表示用の会員フォームを初期化します。
	 *
	 * @return "redirect:/client/user/update/input" 変更入力画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateInputInit() {
		// TODO 金宮 永茉担当: セッションのログイン会員IDを条件に会員情報を取得し、UserFormをセッションへ保存する。
		return "redirect:/client/user/update/input";
	}

	// ===== 担当: 金宮 永茉 / 変更入力（入力チェック） =====
	/**
	 * 変更入力画面を表示します。
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/update_input" 変更入力画面
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.GET)
	public String updateInput(Model model) {
		// TODO 金宮 永茉担当: セッションのUserFormと入力エラー情報を画面へ渡す。
		return "client/user/update_input";
	}

	// ===== 担当: 金宮 永茉 / 変更確認 =====
	/**
	 * 変更入力値をチェックし、変更確認画面へ遷移します。
	 *
	 * @param form 会員入力フォーム
	 * @param result 入力チェック結果
	 * @return 入力エラーあり: "redirect:/client/user/update/input"、なし: "redirect:/client/user/update/check"
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateInputCheck(@Valid @ModelAttribute UserForm form, BindingResult result) {
		// TODO 金宮 永茉担当: 入力フォームと入力チェック結果をセッションへ保存し、遷移先を判定する。
		return "redirect:/client/user/update/check";
	}

	// ===== 担当: 金宮 永茉 / 変更確認 =====
	/**
	 * 変更確認画面を表示します。
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/update_check" 変更確認画面
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.GET)
	public String updateCheck(Model model) {
		// TODO 金宮 永茉担当: セッションのUserFormを画面へ渡す。
		return "client/user/update_check";
	}

	// ===== 担当: 金宮 永茉 / 変更完了 =====
	/**
	 * 会員情報を更新します。
	 *
	 * @return "redirect:/client/user/update/complete" 変更完了画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete() {
		// TODO 金宮 永茉担当: UserFormからUserエンティティを更新し、セッションのログイン会員情報も更新する。
		return "redirect:/client/user/update/complete";
	}

	// ===== 担当: 金宮 永茉 / 変更完了 =====
	/**
	 * 変更完了画面を表示します。
	 *
	 * @return "client/user/update_complete" 変更完了画面
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteFinish() {
		// TODO 金宮 永茉担当: 変更完了画面を表示するための後処理が必要な場合はここに実装する。
		return "client/user/update_complete";
	}
}
