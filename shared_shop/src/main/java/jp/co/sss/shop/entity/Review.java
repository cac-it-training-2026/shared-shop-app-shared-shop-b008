package jp.co.sss.shop.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * レビュー情報のエンティティクラス
 */
@Entity
@Table(name = "reviews")
public class Review {
	/**
	 * レビューID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reviews_gen")
	@SequenceGenerator(name = "seq_reviews_gen", sequenceName = "seq_reviews", allocationSize = 1)
	private Integer id;

	/**
	 * 評価
	 */
	@Column(name = "RATING")
	private Integer rating;

	/**
	 * レビュー本文
	 */
	@Column(name = "REVIEW_COMMENT")
	private String reviewComment;

	/**
	 * 投稿日
	 */
	@Column(name = "INSERT_DATE", insertable = false, updatable = false)
	private Timestamp insertDate;

	/**
	 * 会員情報
	 */
	@ManyToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "id")
	private User user;

	/**
	 * 商品情報
	 */
	@ManyToOne
	@JoinColumn(name = "ITEM_ID", referencedColumnName = "id")
	private Item item;

	/**
	 * 注文商品情報
	 */
	@ManyToOne
	@JoinColumn(name = "ORDER_ITEM_ID", referencedColumnName = "id")
	private OrderItem orderItem;

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
	 * レビュー本文の取得
	 * @return レビュー本文
	 */
	public String getReviewComment() {
		return reviewComment;
	}

	/**
	 * レビュー本文のセット
	 * @param reviewComment レビュー本文
	 */
	public void setReviewComment(String reviewComment) {
		this.reviewComment = reviewComment;
	}

	/**
	 * 投稿日の取得
	 * @return 投稿日
	 */
	public Timestamp getInsertDate() {
		return insertDate;
	}

	/**
	 * 投稿日のセット
	 * @param insertDate 投稿日
	 */
	public void setInsertDate(Timestamp insertDate) {
		this.insertDate = insertDate;
	}

	/**
	 * 会員エンティティの取得
	 * @return 会員エンティティ
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 会員エンティティのセット
	 * @param user 会員エンティティ
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * 商品エンティティの取得
	 * @return 商品エンティティ
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * 商品エンティティのセット
	 * @param item 商品エンティティ
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * 注文商品エンティティの取得
	 * @return 注文商品エンティティ
	 */
	public OrderItem getOrderItem() {
		return orderItem;
	}

	/**
	 * 注文商品エンティティのセット
	 * @param orderItem 注文商品エンティティ
	 */
	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}

}
