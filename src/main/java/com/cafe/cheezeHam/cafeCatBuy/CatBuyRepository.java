package com.cafe.cheezeHam.cafeCatBuy;

import com.cafe.cheezeHam.cafeAccuse.Accuse;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CatBuyRepository extends JpaRepository<CatBuy, Integer> {

    @Query("select distinct cb from CatBuy cb order by cb.no desc limit 5")
    List<CatBuy> findRecentCatBuys();

    @Query("select count(cb) from CatBuy cb where cb.author.id = :id")
    long countByAuthorId(@Param("id") String id);

    @Query("select distinct cb from CatBuy cb "
            + "left outer join CafeUser u1 on cb.author = u1 "
            + "left outer join CatBuyComment c on c.catBuy = cb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "cb.title like %:keyword% "
            + "or cb.content like %:keyword% "
            + "or cb.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    Page<CatBuy> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct cb from CatBuy cb "
            + "left outer join CafeUser u1 on cb.author = u1 "
            + "left outer join CatBuyComment c on c.catBuy = cb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "cb.title like %:keyword%")
    Page<CatBuy> findTitleByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct cb from CatBuy cb "
            + "left outer join CafeUser u1 on cb.author = u1 "
            + "left outer join CatBuyComment c on c.catBuy = cb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "cb.author.id like %:keyword%")
    Page<CatBuy> findAuthorByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct cb from CatBuy cb "
            + "left outer join CafeUser u1 on cb.author = u1 "
            + "left outer join CatBuyComment c on c.catBuy = cb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "cb.title like %:keyword% "
            + "or cb.content like %:keyword% "
            + "or c.content like %:keyword%")
    Page<CatBuy> findTitleAndContentByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct cb from CatBuy cb where cb.no = :no")
    Optional<CatBuy> findByNo(@Param("no") int no);

    @Transactional
    @Modifying
    @Query("update CatBuy cb set cb.hit = cb.hit + 1 where cb.no = :no")
    void increaseViewCount(int no);

    @Query("select max(cb.no) from CatBuy cb")
    int findMaxNo();


    @Query("select distinct cb from CatBuy cb "
            + "left outer join CafeUser u1 on cb.author = u1 "
            + "left outer join CatBuyComment c on c.catBuy = cb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "cb.title like %:keyword% "
            + "or cb.content like %:keyword% "
            + "or cb.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    List<CatBuy> findAllByKeyword(@Param("keyword") String keyword);

    @Query("select distinct cb from CatBuy cb "
            + "left outer join CafeUser u1 on cb.author = u1 "
            + "left outer join CatBuyComment c on c.catBuy = cb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "cb.title like %:keyword%")
    List<CatBuy> findTitleByKeyword(@Param("keyword") String keyword);

    @Query("select distinct cb from CatBuy cb "
            + "left outer join CafeUser u1 on cb.author = u1 "
            + "left outer join CatBuyComment c on c.catBuy = cb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "cb.author.id like %:keyword%")
    List<CatBuy> findAuthorByKeyword(@Param("keyword") String keyword);

    @Query("select distinct cb from CatBuy cb "
            + "left outer join CafeUser u1 on cb.author = u1 "
            + "left outer join CatBuyComment c on c.catBuy = cb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "cb.title like %:keyword% "
            + "or cb.content like %:keyword% "
            + "or c.content like %:keyword%")
    List<CatBuy> findTitleAndContentByKeyword(@Param("keyword") String keyword);

    void deleteByAuthor(CafeUser user);
}
