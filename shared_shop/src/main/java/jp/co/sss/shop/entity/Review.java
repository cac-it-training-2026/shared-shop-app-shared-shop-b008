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
 *
 * @author SystemShared
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
	 * 会員情報
	 */
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	/**
	 * 商品情報
	 */
	@ManyToOne
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	private Item item;

	/**
	 * 注文情報
	 */
	@ManyToOne
	@JoinColumn(name = "order_id", referencedColumnName = "id")
	private Order order;

	/**
	 * 評価
	 */
	@Column
	private Integer rating;

	/**
	 * コメント
	 */
	@Column
	private String comment;

	/**
	 * 登録日付
	 */
	@Column(name = "created_date", insertable = false, updatable = false)
	private Timestamp createdDate;

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
	 * 注文エンティティの取得
	 * @return 注文エンティティ
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * 注文エンティティのセット
	 * @param order 注文エンティティ
	 */
	public void setOrder(Order order) {
		this.order = order;
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

	/**
	 * 登録日付の取得
	 * @return 登録日付
	 */
	public Timestamp getCreatedDate() {
		return createdDate;
	}

	/**
	 * 登録日付のセット
	 * @param createdDate 登録日付
	 */
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}
}
