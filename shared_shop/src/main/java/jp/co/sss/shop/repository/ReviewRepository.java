package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Review;

/**
 * reviewsテーブル用リポジトリ
 *
 * @author SystemShared
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	/**
	 * 商品IDを条件にレビューを検索（投稿日降順）
	 * @param itemId 商品ID
	 * @return レビューエンティティのリスト
	 */
	List<Review> findByItemIdOrderByInsertDateDesc(Integer itemId);

	/**
	 * 商品IDを条件にレビューを検索（投稿日降順、件数制限付き）
	 * @param itemId 商品ID
	 * @param pageable ページング情報
	 * @return レビューエンティティのリスト
	 */
	List<Review> findByItemIdOrderByInsertDateDesc(Integer itemId, Pageable pageable);

	/**
	 * 商品IDを条件にレビューを検索（評価高い順）
	 * @param itemId 商品ID
	 * @return レビューエンティティのリスト
	 */
	List<Review> findByItemIdOrderByRatingDescInsertDateDesc(Integer itemId);

	/**
	 * 商品IDを条件にレビューを検索（評価低い順）
	 * @param itemId 商品ID
	 * @return レビューエンティティのリスト
	 */
	List<Review> findByItemIdOrderByRatingAscInsertDateDesc(Integer itemId);

	/**
	 * 商品IDを条件に平均評価を取得
	 * @param itemId 商品ID
	 * @return 平均評価（平均値が取得できない場合はnull）
	 */
	@Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.item.id = :itemId")
	Double getAverageRatingByItemId(@Param("itemId") Integer itemId);

	/**
	 * 商品IDを条件にレビュー件数を取得
	 * @param itemId 商品ID
	 * @return レビュー件数
	 */
	Long countByItemId(Integer itemId);
}
