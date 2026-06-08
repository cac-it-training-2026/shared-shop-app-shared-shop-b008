package jp.co.sss.shop.controller.client.basket;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.repository.ItemRepository;

/**
 * 買い物かご機能(一般会員用)のコントローラクラスです。
 *
 * @author SystemShared
 */
@Controller
public class ClientBasketController {

	/**
	 * 商品情報リポジトリ
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	// ===== 担当: シュエ ジーハン / 買い物かご内の商品一覧表示 =====
	/**
	 * 買い物かご画面を表示します。
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/basket/list" 買い物かご画面
	 */
	@RequestMapping(path = "/client/basket/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String showBasket(Model model) {
		// TODO シュエ ジーハン担当: セッションの買い物かご情報を取得し、在庫状況を確認して画面へ渡す。
		return "client/basket/list";
	}

	// ===== 担当: シュエ ジーハン / 商品追加 =====
	/**
	 * 商品を買い物かごに追加します。
	 *
	 * @param id 商品ID
	 * @return "redirect:/client/basket/list" 買い物かご画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addItem(@RequestParam Integer id) {
		// TODO シュエ ジーハン担当: 商品情報を取得し、在庫数を確認してセッションの買い物かごへ追加する。
		return "redirect:/client/basket/list";
	}

	// ===== 担当: シュエ ジーハン / 商品削除 =====
	/**
	 * 買い物かご内の商品を削除します。
	 *
	 * @param id 商品ID
	 * @return "redirect:/client/basket/list" 買い物かご画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String deleteItem(@RequestParam Integer id) {
		// TODO シュエ ジーハン担当: セッションの買い物かごから指定商品を削除する。
		return "redirect:/client/basket/list";
	}

	// ===== 担当: シュエ ジーハン / 商品全削除 =====
	/**
	 * 買い物かご内の商品をすべて削除します。
	 *
	 * @return "redirect:/client/basket/list" 買い物かご画面表示処理へリダイレクト
	 */
	@RequestMapping(path = "/client/basket/allDelete", method = RequestMethod.POST)
	public String deleteAllItems() {
		// TODO シュエ ジーハン担当: セッションの買い物かご情報を削除する。
		return "redirect:/client/basket/list";
	}
}
