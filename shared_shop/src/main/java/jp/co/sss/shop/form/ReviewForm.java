package jp.co.sss.shop.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * レビュー投稿用のフォームクラス
 *
 * @author SystemShared
 */
public class ReviewForm {

	/**
	 * 注文ID
	 */
	@NotNull
	private Integer orderId;

	/**
	 * 商品ID
	 */
	@NotNull
	private Integer itemId;

	/**
	 * 評価（1-5）
	 */
	@NotNull
	@Min(1)
	@Max(5)
	private Integer rating;

	/**
	 * コメント（任意、最大2000文字）
	 */
	@Size(max = 2000)
	private String comment;

	/**
	 * 注文IDの取得
	 * @return 注文ID
	 */
	public Integer getOrderId() {
		return orderId;
	}

	/**
	 * 注文IDのセット
	 * @param orderId 注文ID
	 */
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	/**
	 * 商品IDの取得
	 * @return 商品ID
	 */
	public Integer getItemId() {
		return itemId;
	}

	/**
	 * 商品IDのセット
	 * @param itemId 商品ID
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	/**
	 * 評価の取得
	 * @return 評価
	 */
	public Integer getRating() {
		return rating;
	}

	/**
	 * 評価のセット
	 * @param rating 評価
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/**
	 * コメントの取得
	 * @return コメント
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * コメントのセット
	 * @param comment コメント
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
}
