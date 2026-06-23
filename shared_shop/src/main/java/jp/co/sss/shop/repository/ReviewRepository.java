package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Review;

/**
 * reviewsテーブル用リポジトリ
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	/**
	 * 商品IDを条件に、投稿日時の降順でレビューを検索
	 * @param itemId 商品ID
	 * @return レビューエンティティのリスト
	 */
	List<Review> findByItemIdOrderByInsertDateDesc(Integer itemId);

	/**
	 * 商品IDを条件に、評価の降順（高評価順）でレビューを検索
	 * @param itemId 商品ID
	 * @return レビューエンティティのリスト
	 */
	List<Review> findByItemIdOrderByRatingDescInsertDateDesc(Integer itemId);

	/**
	 * 商品IDを条件に、評価の昇順（低評価順）でレビューを検索
	 * @param itemId 商品ID
	 * @return レビューエンティティのリスト
	 */
	List<Review> findByItemIdOrderByRatingAscInsertDateDesc(Integer itemId);

	/**
	 * 注文商品IDを条件にレビューを検索
	 * @param orderItemId 注文商品ID
	 * @return レビューエンティティ
	 */
	Review findByOrderItemId(Integer orderItemId);
}
