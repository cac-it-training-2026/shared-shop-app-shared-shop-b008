package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 新着順
	 */
	private static final int SORT_LATEST = 1;

	/**
	 * 売れ筋順
	 */
	private static final int SORT_HOT_SELL = 2;

	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	// ===== 担当: 切通 隆晟 / トップ画面（売れ筋順改修） =====
	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {

		// トップ画面は売れ筋順を初期表示にする。
		int sortType = SORT_HOT_SELL;

		// 注文商品情報から売れ筋順の商品情報を取得する。
		List<Item> itemList = itemRepository.findHotSellItems(Constant.NOT_DELETED);

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		//		商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);
		model.addAttribute("sortType", sortType);
		//
		return "index";
	}

	// ===== 担当: 切通 隆晟 / 商品一覧（新着） =====
	// ===== 担当: シュエ ジーハン / 商品一覧（売れ筋） =====
	// ===== 担当: 切通 隆晟 / 商品検索（カテゴリ） =====
	/**
	 * 商品一覧画面を表示します。
	 *
	 * @param sortType 表示順種別(1:新着順、2:売れ筋順)
	 * @param categoryId カテゴリID
	 * @param model Viewとの値受渡し
	 * @return "client/item/list" 商品一覧画面
	 */
	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET, RequestMethod.POST })
	public String showItemList(
			// 	/client/item/list/1にアクセスされるとsortType = 1が入る
			@PathVariable Integer sortType,
			@RequestParam(required = false) Integer categoryId,
			Model model) {

		// 表示順(新着順、売れ筋順)とカテゴリ条件に応じて商品情報を取得する。
		List<Item> itemList = findItems(sortType, categoryId);

		// 商品情報を画面表示用Beanにコピーする。
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		model.addAttribute("items", itemBeanList);
		model.addAttribute("sortType", sortType);
		model.addAttribute("categoryId", categoryId);

		return "client/item/list";
	}

	/**
	 * 表示順とカテゴリ条件に応じた商品一覧を取得します。
	 *
	 * @param sortType 表示順種別
	 * @param categoryId カテゴリID
	 * @return 商品エンティティのリスト
	 */
	private List<Item> findItems(Integer sortType, Integer categoryId) {
		// カテゴリが指定されているかを判定（null,0=false 1,2=true）
		boolean hasCategory = categoryId != null && categoryId != 0;

		//　if文で、新着順か売れ筋順かを選定
		// 売れ筋順か判定
		if (sortType != null && sortType == SORT_HOT_SELL) {

			if (hasCategory) {
				// 選択したカテゴリの商品だけ売れ筋順で取得
				return itemRepository.findHotSellItemsByCategoryId(categoryId, Constant.NOT_DELETED);
			}
			// 注文商品情報をもとに全商品を売れ筋順で取得
			return itemRepository.findHotSellItems(Constant.NOT_DELETED);
		}

		// 新着順の場合は商品登録日の降順で取得する
		if (hasCategory) {
			// //選択したカテゴリの商品だけ新着順で取得
			return itemRepository.findByCategoryIdAndDeleteFlagOrderByInsertDateDesc(categoryId, Constant.NOT_DELETED);
		}
		// 全商品を新着順で取得する。
		return itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED);
	}

	/**
	 * 詳細表示処理
	 *
	 * @param id      表示対象ID
	 * @param model   Viewとの値受渡し
	 * @return "client/item/detail" 詳細画面 表示
	 */
	@RequestMapping(path = "/client/item/detail/{id}")
	public String showItem(@PathVariable int id, Model model) {

		// 商品IDに該当する商品情報を取得する
		Item item = itemRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/syserror";
		}

		// Itemエンティティの各フィールドの値をItemBeanにコピー
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);

		// 商品情報をViewへ渡す
		model.addAttribute("item", itemBean);

		return "client/item/detail";
	}

}
