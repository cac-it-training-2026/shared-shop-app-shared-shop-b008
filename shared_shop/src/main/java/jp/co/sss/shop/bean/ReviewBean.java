package jp.co.sss.shop.bean;

/**
 * レビュー情報クラス
 */
public class ReviewBean {

	/**
	 * レビューID
	 */
	private Integer id;

	/**
	 * ユーザーID
	 */
	private Integer userId;

	/**
	 * ユーザー名
	 */
	private String userName;

	/**
	 * 商品ID
	 */
	private Integer itemId;

	/**
	 * 注文商品ID
	 */
	private Integer orderItemId;

	/**
	 * 評価
	 */
	private Integer rating;

	/**
	 * レビューコメント
	 */
	private String reviewComment;

	/**
	 * 登録日付
	 */
	private String insertDate;

	/**
	 * レビューIDの取得
	 * @return レビューID
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * レビューIDのセット
	 * @param id レビューID
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * ユーザーIDの取得
	 * @return ユーザーID
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * ユーザーIDのセット
	 * @param userId ユーザーID
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * ユーザー名の取得
	 * @return ユーザー名
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * ユーザー名のセット
	 * @param userName ユーザー名
	 */
	public void setUserName(String userName) {
		this.userName = userName;
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
	 * レビューコメントの取得
	 * @return レビューコメント
	 */
	public String getReviewComment() {
		return reviewComment;
	}

	/**
	 * レビューコメントのセット
	 * @param reviewComment レビューコメント
	 */
	public void setReviewComment(String reviewComment) {
		this.reviewComment = reviewComment;
	}

	/**
	 * 登録日付の取得
	 * @return 登録日付
	 */
	public String getInsertDate() {
		return insertDate;
	}

	/**
	 * 登録日付のセット
	 * @param insertDate 登録日付
	 */
	public void setInsertDate(String insertDate) {
		this.insertDate = insertDate;
	}

}
