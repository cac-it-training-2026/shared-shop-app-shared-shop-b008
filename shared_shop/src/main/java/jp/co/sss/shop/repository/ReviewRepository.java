package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Review;

/**
 * レビュー情報のリポジトリ
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	/**
	 * 商品ごとの平均評価を取得
	 */
	@Query("SELECT AVG(CAST(r.rating AS double)) FROM Review r WHERE r.item.id = :itemId")
	Double getAvgRatingByItemId(@Param("itemId") Integer itemId);

	/**
	 * 商品ごとのレビュー件数を取得
	 */
	@Query("SELECT COUNT(r) FROM Review r WHERE r.item.id = :itemId")
	Long getReviewCountByItemId(@Param("itemId") Integer itemId);

	/**
	 * 商品ごとのレビュー一覧を取得（ソート・制限用）
	 */
	List<Review> findByItemIdOrderByInsertDateDesc(Integer itemId, Pageable pageable);

	/**
	 * 商品ごとのレビュー一覧を全件取得（新しい順）
	 */
	List<Review> findByItemIdOrderByInsertDateDesc(Integer itemId);

	/**
	 * 商品ごとのレビュー一覧を全件取得（評価が高い順）
	 */
	List<Review> findByItemIdOrderByRatingDescInsertDateDesc(Integer itemId);

	/**
	 * 商品ごとのレビュー一覧を全件取得（評価が低い順）
	 */
	List<Review> findByItemIdOrderByRatingAscInsertDateDesc(Integer itemId);

	/**
	 * ユーザーと注文商品に紐づくレビューが存在するか確認
	 */
	boolean existsByUserIdAndOrderItemId(Integer userId, Integer orderItemId);
}
