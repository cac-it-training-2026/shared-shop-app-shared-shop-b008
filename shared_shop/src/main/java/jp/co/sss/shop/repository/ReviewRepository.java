package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Review;

/**
 * reviewsテーブル用リポジトリ
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	/**
	 * 商品IDに紐づくレビューを登録日付の降順で取得
	 * @param itemId 商品ID
	 * @return レビューエンティティのリスト
	 */
	List<Review> findByItemIdOrderByInsertDateDesc(Integer itemId);

	/**
	 * 商品IDに紐づくレビューを評価の降順で取得
	 * @param itemId 商品ID
	 * @return レビューエンティティのリスト
	 */
	List<Review> findByItemIdOrderByRatingDescInsertDateDesc(Integer itemId);

	/**
	 * 商品IDに紐づくレビューを評価の昇順で取得
	 * @param itemId 商品ID
	 * @return レビューエンティティのリスト
	 */
	List<Review> findByItemIdOrderByRatingAscInsertDateDesc(Integer itemId);

	/**
	 * 注文商品IDに紐づくレビューを取得
	 * @param orderItemId 注文商品ID
	 * @return レビューエンティティ
	 */
	Review findByOrderItemId(Integer orderItemId);

	/**
	 * 商品IDごとの平均評価を取得
	 * @param itemId 商品ID
	 * @return 平均評価
	 */
	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.item.id = :itemId")
	Double getAvgRating(@Param("itemId") Integer itemId);

	/**
	 * 商品IDごとのレビュー件数を取得
	 * @param itemId 商品ID
	 * @return レビュー件数
	 */
	Long countByItemId(Integer itemId);
}
