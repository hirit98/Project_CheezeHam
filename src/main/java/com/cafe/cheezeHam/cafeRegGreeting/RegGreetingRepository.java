package com.cafe.cheezeHam.cafeRegGreeting;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegGreetingRepository extends JpaRepository<RegGreeting, Integer> {

    @Query("select distinct r from RegGreeting r order by r.no desc limit 5")
    List<RegGreeting> findRecentRegGreetings();

    @Query("select count(r) from RegGreeting r where r.author.id = :id")
    long countByAuthorId(@Param("id") String id);

    @Query("select distinct r from RegGreeting r "
            + "left outer join CafeUser u1 on r.author = u1 "
            + "left outer join RegComment c on c.regGreeting = r "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "r.title like %:keyword% "
            + "or r.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    Page<RegGreeting> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct r from RegGreeting r "
            + "left outer join CafeUser u1 on r.author = u1 "
            + "left outer join RegComment c on c.regGreeting = r "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "r.title like %:keyword%")
    Page<RegGreeting> findTitleByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct r from RegGreeting r "
            + "left outer join CafeUser u1 on r.author = u1 "
            + "left outer join RegComment c on c.regGreeting = r "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "r.author.id like %:keyword%")
    Page<RegGreeting> findAuthorByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct r from RegGreeting r "
            + "left outer join CafeUser u1 on r.author = u1 "
            + "left outer join RegComment c on c.regGreeting = r "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "r.title like %:keyword% "
            + "or r.content like %:keyword% "
            + "or c.content like %:keyword%")
    Page<RegGreeting> findTitleAndContentByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update RegGreeting r set r.hit = r.hit + 1 where r.no = :no")
    void increaseViewCount(@Param("no") int no);

    @Query("select distinct r from RegGreeting r "
            + "left outer join CafeUser u1 on r.author = u1 "
            + "left outer join RegComment c on c.regGreeting = r "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "r.title like %:keyword% "
            + "or r.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    List<RegGreeting> findAllByKeyword(@Param("keyword") String keyword);

    @Query("select distinct r from RegGreeting r "
            + "left outer join CafeUser u1 on r.author = u1 "
            + "left outer join RegComment c on c.regGreeting = r "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "r.title like %:keyword%")
    List<RegGreeting> findTitleByKeyword(@Param("keyword") String keyword);

    @Query("select distinct r from RegGreeting r "
            + "left outer join CafeUser u1 on r.author = u1 "
            + "left outer join RegComment c on c.regGreeting = r "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "r.author.id like %:keyword%")
    List<RegGreeting> findAuthorByKeyword(@Param("keyword") String keyword);

    @Query("select distinct r from RegGreeting r "
            + "left outer join CafeUser u1 on r.author = u1 "
            + "left outer join RegComment c on c.regGreeting = r "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "r.title like %:keyword% "
            + "or r.content like %:keyword% "
            + "or c.content like %:keyword%")
    List<RegGreeting> findTitleAndContentByKeyword(@Param("keyword") String keyword);

    void deleteByAuthor(CafeUser user);
}
