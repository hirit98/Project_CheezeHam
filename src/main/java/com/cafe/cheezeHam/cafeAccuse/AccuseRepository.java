package com.cafe.cheezeHam.cafeAccuse;

import com.cafe.cheezeHam.cafeFree.Free;
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

public interface AccuseRepository extends JpaRepository<Accuse, Integer> {

    @Query("select distinct a from Accuse a order by a.no desc limit 5")
    List<Accuse> findRecentAccuses();

    @Query("select count(a) from Accuse a where a.author.id = :id")
    long countByAuthorId(@Param("id") String id);

    @Query("select distinct a from Accuse a "
            + "left outer join CafeUser u1 on a.author = u1 "
            + "left outer join AccuseComment c on c.accuse = a "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "a.title like %:keyword% "
            + "or a.content like %:keyword% "
            + "or a.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    Page<Accuse> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct a from Accuse a "
            + "left outer join CafeUser u1 on a.author = u1 "
            + "left outer join AccuseComment c on c.accuse = a "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "a.title like %:keyword%")
    Page<Accuse> findTitleByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct a from Accuse a "
            + "left outer join CafeUser u1 on a.author = u1 "
            + "left outer join AccuseComment c on c.accuse = a "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "a.author.id like %:keyword%")
    Page<Accuse> findAuthorByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct a from Accuse a "
            + "left outer join CafeUser u1 on a.author = u1 "
            + "left outer join AccuseComment c on c.accuse = a "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "a.title like %:keyword% "
            + "or a.content like %:keyword% "
            + "or c.content like %:keyword%")
    Page<Accuse> findTitleAndContentByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct a from Accuse a where a.no = :no")
    Optional<Accuse> findByNo(@Param("no") int no);

    @Query("select max(a.no) from Accuse a")
    int findMaxNo();

    @Transactional
    @Modifying
    @Query("update Accuse a set a.hit = a.hit + 1 where a.no = :no")
    void increaseViewCount(int no);

    @Query("select distinct a from Accuse a "
            + "left outer join CafeUser u1 on a.author = u1 "
            + "left outer join AccuseComment c on c.accuse = a "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "a.title like %:keyword% "
            + "or a.content like %:keyword% "
            + "or a.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    List<Accuse> findAllByKeyword(@Param("keyword") String keyword);

    @Query("select distinct a from Accuse a "
            + "left outer join CafeUser u1 on a.author = u1 "
            + "left outer join AccuseComment c on c.accuse = a "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "a.title like %:keyword%")
    List<Accuse> findTitleByKeyword(@Param("keyword") String keyword);

    @Query("select distinct a from Accuse a "
            + "left outer join CafeUser u1 on a.author = u1 "
            + "left outer join AccuseComment c on c.accuse = a "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "a.author.id like %:keyword%")
    List<Accuse> findAuthorByKeyword(@Param("keyword") String keyword);

    @Query("select distinct a from Accuse a "
            + "left outer join CafeUser u1 on a.author = u1 "
            + "left outer join AccuseComment c on c.accuse = a "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "a.title like %:keyword% "
            + "or a.content like %:keyword% "
            + "or c.content like %:keyword%")
    List<Accuse> findTitleAndContentByKeyword(@Param("keyword") String keyword);

    void deleteByAuthor(CafeUser user);
}
