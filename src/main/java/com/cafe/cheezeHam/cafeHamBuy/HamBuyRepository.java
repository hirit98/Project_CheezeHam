package com.cafe.cheezeHam.cafeHamBuy;

import com.cafe.cheezeHam.cafeCatBuy.CatBuy;
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

public interface HamBuyRepository extends JpaRepository<HamBuy, Integer> {

    @Query("select distinct hb from HamBuy hb order by hb.no desc limit 5")
    List<HamBuy> findRecentHamBuys();

    @Query("select count(hb) from HamBuy hb where hb.author.id = :id")
    long countByAuthorId(@Param("id") String id);

    @Query("select distinct hb from HamBuy hb "
            + "left outer join CafeUser u1 on hb.author = u1 "
            + "left outer join HamBuyComment c on c.hamBuy = hb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "hb.title like %:keyword% "
            + "or hb.content like %:keyword% "
            + "or hb.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    Page<HamBuy> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct hb from HamBuy hb "
            + "left outer join CafeUser u1 on hb.author = u1 "
            + "left outer join HamBuyComment c on c.hamBuy = hb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "hb.title like %:keyword%")
    Page<HamBuy> findTitleByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct hb from HamBuy hb "
            + "left outer join CafeUser u1 on hb.author = u1 "
            + "left outer join HamBuyComment c on c.hamBuy = hb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "hb.author.id like %:keyword%")
    Page<HamBuy> findAuthorByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct hb from HamBuy hb "
            + "left outer join CafeUser u1 on hb.author = u1 "
            + "left outer join HamBuyComment c on c.hamBuy = hb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "hb.title like %:keyword% "
            + "or hb.content like %:keyword% "
            + "or c.content like %:keyword%")
    Page<HamBuy> findTitleAndContentByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct hb from HamBuy hb where hb.no = :no")
    Optional<HamBuy> findByNo(@Param("no") int no);

    @Query("select max(hb.no) from HamBuy hb")
    int findMaxNo();

    @Transactional
    @Modifying
    @Query("update HamBuy hb set hb.hit = hb.hit + 1 where hb.no = :no")
    void increaseViewCount(int no);


    @Query("select distinct hb from HamBuy hb "
            + "left outer join CafeUser u1 on hb.author = u1 "
            + "left outer join HamBuyComment c on c.hamBuy = hb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "hb.title like %:keyword% "
            + "or hb.content like %:keyword% "
            + "or hb.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    List<HamBuy> findAllByKeyword(@Param("keyword") String keyword);

    @Query("select distinct hb from HamBuy hb "
            + "left outer join CafeUser u1 on hb.author = u1 "
            + "left outer join HamBuyComment c on c.hamBuy = hb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "hb.title like %:keyword%")
    List<HamBuy> findTitleByKeyword(@Param("keyword") String keyword);

    @Query("select distinct hb from HamBuy hb "
            + "left outer join CafeUser u1 on hb.author = u1 "
            + "left outer join HamBuyComment c on c.hamBuy = hb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "hb.author.id like %:keyword%")
    List<HamBuy> findAuthorByKeyword(@Param("keyword") String keyword);

    @Query("select distinct hb from HamBuy hb "
            + "left outer join CafeUser u1 on hb.author = u1 "
            + "left outer join HamBuyComment c on c.hamBuy = hb "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "hb.title like %:keyword% "
            + "or hb.content like %:keyword% "
            + "or c.content like %:keyword%")
    List<HamBuy> findTitleAndContentByKeyword(@Param("keyword") String keyword);

    void deleteByAuthor(CafeUser user);

}
