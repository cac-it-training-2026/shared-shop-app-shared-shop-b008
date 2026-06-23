package jp.co.sss.shop.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * レビュー投稿用のフォームクラス
 */
public class ReviewForm {

	/** 商品ID */
	private Integer itemId;

	/** 注文商品ID */
	private Integer orderItemId;

	/** 評価 */
	@NotNull
	@Min(1)
	@Max(5)
	private Integer rating;

	/** レビュー本文 */
	@Size(max = 2000)
	private String reviewComment;

	/** 商品IDの取得 */
	public Integer getItemId() {
		return itemId;
	}

	/** 商品IDのセット */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	/** 注文商品IDの取得 */
	public Integer getOrderItemId() {
		return orderItemId;
	}

	/** 注文商品IDのセット */
	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
	}

	/** 評価の取得 */
	public Integer getRating() {
		return rating;
	}

	/** 評価のセット */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/** レビュー本文の取得 */
	public String getReviewComment() {
		return reviewComment;
	}

	/** レビュー本文のセット */
	public void setReviewComment(String reviewComment) {
		this.reviewComment = reviewComment;
	}
}
