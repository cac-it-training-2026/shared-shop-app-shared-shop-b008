package jp.co.sss.shop.controller.client.item;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
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
	@RequestMapping(path = "/")
	public String top(Model model) {

		// トップ画面は売れ筋順を初期表示にする。
		int sortType = SORT_HOT_SELL;

		// 注文商品情報から売れ筋順の商品情報を取得する。
		List<Item> itemList = itemRepository.findHotSellItems(Constant.NOT_DELETED);

		// 売れ筋商品が存在しない場合は新着順を表示
		if (itemList.isEmpty()) {
			itemList = itemRepository
					.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED);

			sortType = SORT_LATEST;
		}

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);
		model.addAttribute("sortType", sortType);

		return "index";
	}

	// ===== 担当: 切通 隆晟 / 商品一覧（新着） =====
	// ===== 担当: シュエ ジーハン / 商品一覧（売れ筋） =====
	// ===== 担当: 切通 隆晟 / 商品検索（カテゴリ） =====
	/**
	 * 商品詳細画面の並び替え及びカテゴリ検索
	 *
	 * @param sortType 表示順種別(1:新着順、2:売れ筋順)
	 * @param categoryId カテゴリID
	 * @param model Viewとの値受渡し
	 * @return "client/item/list" 商品一覧画面表示
	 */
	@Autowired
	CategoryRepository categoryRepository;

	@RequestMapping(path = "/client/item/list/{sortType}")
	public String showItemList(
			// 	/client/item/list/1にアクセスされるとsortType = 1（新着順）が入る
			@PathVariable Integer sortType,
			@RequestParam(required = false) Integer categoryId,
			@RequestHeader(value = "Referer", required = false) String referer,
			HttpServletRequest request,
			Model model) {
		// カテゴリが指定されているかを判定　（null,0=false 1,2=true）
		boolean hasCategory = categoryId != null && categoryId != 0;

		// 商品一覧画面以外のサイドバーからカテゴリ検索した場合は、設計どおり新着順を初期表示にする。
		// 商品一覧画面内でカテゴリ検索した場合だけは、現在の表示順(sortType)を維持する。
		// categoryId=0(指定なし)もカテゴリ検索フォームから送信された値のため、補正対象に含める。
		// TODO 切通 隆晟: 買い物カゴ画面・注文一覧画面にカテゴリ検索欄を実装する場合も、
		// categoryIdを付けて商品一覧へ遷移させれば、この補正処理で新着順表示になります。
		if (isCategorySearchFromOutsideItemList(sortType, categoryId, referer, request.getContextPath())) {
			if (sortType != SORT_LATEST) {
				return "redirect:/client/item/list/" + SORT_LATEST + "?categoryId=" + categoryId;
			}
			sortType = SORT_LATEST;
		}

		List<Item> itemList;

		//　if文で、新着順か売れ筋順かを選定
		// 売れ筋順か判定
		if (sortType != null && sortType == SORT_HOT_SELL) {

			if (hasCategory) {
				// 選択したカテゴリの商品だけ売れ筋順で取得
				itemList = itemRepository.findHotSellItemsByCategoryId(
						categoryId,
						Constant.NOT_DELETED);
			} else {
				// 注文商品情報をもとに全商品を売れ筋順で取得
				itemList = itemRepository.findHotSellItems(
						Constant.NOT_DELETED);
			}
		}

		else {
			// 新着順の場合は商品登録日の降順で取得する
			if (hasCategory) {
				//選択したカテゴリの商品だけ新着順で取得
				itemList = itemRepository
						.findByCategoryIdAndDeleteFlagOrderByInsertDateDesc(
								categoryId,
								Constant.NOT_DELETED);
			} else {
				// 全商品を新着順で取得する。
				itemList = itemRepository
						.findByDeleteFlagOrderByInsertDateDesc(
								Constant.NOT_DELETED);
			}
		}

		// 商品情報を画面表示用Beanにコピーする。
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		model.addAttribute("items", itemBeanList);
		model.addAttribute("sortType", sortType);
		model.addAttribute("categoryId", categoryId);

		return "client/item/list";
	}

	/**
	 * 商品一覧画面以外から送信されたカテゴリ検索かどうかを判定します。
	 *
	 * @param sortType 表示順種別
	 * @param categoryId カテゴリID
	 * @param referer 遷移元URL
	 * @param contextPath アプリケーションのコンテキストパス
	 * @return 商品一覧画面以外からのカテゴリ検索の場合true
	 */
	private boolean isCategorySearchFromOutsideItemList(
			Integer sortType,
			Integer categoryId,
			String referer,
			String contextPath) {

		if (categoryId == null || sortType == null || referer == null) {
			return false;
		}

		try {
			String refererPath = URI.create(referer).getPath();
			String itemListPath = contextPath + "/client/item/list/";

			// 遷移元が商品一覧画面の場合は、カテゴリ検索後も現在の表示順を維持する。
			// それ以外の画面からのカテゴリ検索は、設計どおり新着順へ補正する。
			return !refererPath.startsWith(itemListPath);
		} catch (IllegalArgumentException e) {
			return false;
		}
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
