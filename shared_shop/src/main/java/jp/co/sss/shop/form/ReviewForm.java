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
	 * 注文商品ID
	 */
	@NotNull
	private Integer orderItemId;

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
	private String reviewComment;

	/**
	 * 注文商品IDの取得
	 * @return 注文商品ID
	 */
	public Integer getOrderItemId() {
		return orderItemId;
	}

	/**
	 * 注文商品IDのセット
	 * @param orderItemId 注文商品ID
	 */
	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
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
	public String getReviewComment() {
		return reviewComment;
	}

	/**
	 * コメントのセット
	 * @param reviewComment コメント
	 */
	public void setReviewComment(String reviewComment) {
		this.reviewComment = reviewComment;
	}
}
