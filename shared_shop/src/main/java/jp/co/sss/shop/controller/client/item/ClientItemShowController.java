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

		/*TODO 現在は全件表示を行っている
		 * これを売れ筋（注文回数が多い順）に改修する*/

		// 注文情報の商品情報を全件表示
		List<Item> itemList = itemRepository.findAll();

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);

		return "index";
	}

	// ===== 担当: 切通 隆晟 / 商品一覧（新着） =====
	// ===== 担当: シュエ ジーハン / 商品一覧（売れ筋） =====
	// ===== 担当: コグレ / 商品検索（カテゴリ） =====
	// ===== 担当: 切通 隆晟 / 商品一覧（新着） =====
		// ===== 担当: シュエ ジーハン / 商品一覧（売れ筋） =====
		// ===== 担当: コグレ / 商品検索（カテゴリ） =====
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
				@PathVariable Integer sortType,
				@RequestParam(required = false) Integer categoryId,
				Model model) {

			// 表示順とカテゴリ条件に応じて商品情報を取得する。
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
			boolean hasCategory = categoryId != null && categoryId != 0;

			if (sortType != null && sortType ==2) {
				// 売れ筋順の場合は注文商品情報をもとに並び替える。
				if (hasCategory) {
					return itemRepository.findHotSellItemsByCategoryId(categoryId, Constant.NOT_DELETED);
				}
				return itemRepository.findHotSellItems(Constant.NOT_DELETED);
			}

			// 新着順の場合は商品登録日の降順で取得する。
			if (hasCategory) {
				return itemRepository.findByCategoryIdAndDeleteFlagOrderByInsertDateDesc(categoryId, Constant.NOT_DELETED);
			}
			return itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED);
		}}