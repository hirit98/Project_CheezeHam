package com.cafe.cheezeHam.cafeSuggest;

import com.cafe.cheezeHam.cafeHamBuy.HamBuy;
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

public interface SuggestRepository extends JpaRepository<Suggest, Integer> {

    @Query("select distinct s from Suggest s order by s.no desc limit 5")
    List<Suggest> findRecentSuggests();

    @Query("select count(s) from Suggest s where s.author.id = :id")
    long countByAuthorId(@Param("id") String id);

    @Query("select distinct s from Suggest s "
            + "left outer join CafeUser u1 on s.author = u1 "
            + "left outer join SuggestComment c on c.suggest = s "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "s.title like %:keyword% "
            + "or s.content like %:keyword% "
            + "or s.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    Page<Suggest> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct s from Suggest s "
            + "left outer join CafeUser u1 on s.author = u1 "
            + "left outer join SuggestComment c on c.suggest = s "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "s.title like %:keyword%")
    Page<Suggest> findTitleByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct s from Suggest s "
            + "left outer join CafeUser u1 on s.author = u1 "
            + "left outer join SuggestComment c on c.suggest = s "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "s.author.id like %:keyword%")
    Page<Suggest> findAuthorByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct s from Suggest s "
            + "left outer join CafeUser u1 on s.author = u1 "
            + "left outer join SuggestComment c on c.suggest = s "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "s.title like %:keyword% "
            + "or s.content like %:keyword% "
            + "or c.content like %:keyword%")
    Page<Suggest> findTitleAndContentByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct s from Suggest s where s.no = :no")
    Optional<Suggest> findByNo(@Param("no") int no);

    @Transactional
    @Modifying
    @Query("update Suggest s set s.hit = s.hit + 1 where s.no = :no")
    void increaseViewCount(int no);

    @Query("select max(s.no) from Suggest s")
    int findMaxNo();

    @Query("select distinct s from Suggest s "
            + "left outer join CafeUser u1 on s.author = u1 "
            + "left outer join SuggestComment c on c.suggest = s "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "s.title like %:keyword% "
            + "or s.content like %:keyword% "
            + "or s.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    List<Suggest> findAllByKeyword(@Param("keyword") String keyword);

    @Query("select distinct s from Suggest s "
            + "left outer join CafeUser u1 on s.author = u1 "
            + "left outer join SuggestComment c on c.suggest = s "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "s.title like %:keyword%")
    List<Suggest> findTitleByKeyword(@Param("keyword") String keyword);

    @Query("select distinct s from Suggest s "
            + "left outer join CafeUser u1 on s.author = u1 "
            + "left outer join SuggestComment c on c.suggest = s "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "s.author.id like %:keyword%")
    List<Suggest> findAuthorByKeyword(@Param("keyword") String keyword);

    @Query("select distinct s from Suggest s "
            + "left outer join CafeUser u1 on s.author = u1 "
            + "left outer join SuggestComment c on c.suggest = s "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "s.title like %:keyword% "
            + "or s.content like %:keyword% "
            + "or c.content like %:keyword%")
    List<Suggest> findTitleAndContentByKeyword(@Param("keyword") String keyword);

    void deleteByAuthor(CafeUser user);
}
