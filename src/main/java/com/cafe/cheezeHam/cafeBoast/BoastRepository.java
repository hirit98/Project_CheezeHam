package com.cafe.cheezeHam.cafeBoast;

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

public interface BoastRepository extends JpaRepository<Boast, Integer> {

    @Query("select distinct b from Boast b order by b.no desc limit 5")
    List<Boast> findRecentBoast();

    @Query("select count(b) from Boast b where b.author.id = :id")
    long countByAuthorId(@Param("id") String id);

    @Query("select distinct b from Boast b "
            + "left outer join CafeUser u1 on b.author = u1 "
            + "left outer join BoastComment c on c.boast = b "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "b.title like %:keyword% "
            + "or b.content like %:keyword% "
            + "or b.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    Page<Boast> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct b from Boast b "
            + "left outer join CafeUser u1 on b.author = u1 "
            + "left outer join BoastComment c on c.boast = b "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "b.title like %:keyword%")
    Page<Boast> findTitleByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct b from Boast b "
            + "left outer join CafeUser u1 on b.author = u1 "
            + "left outer join BoastComment c on c.boast = b "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "b.author.id like %:keyword%")
    Page<Boast> findAuthorByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct b from Boast b "
            + "left outer join CafeUser u1 on b.author = u1 "
            + "left outer join BoastComment c on c.boast = b "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "b.title like %:keyword% "
            + "or b.content like %:keyword% "
            + "or c.content like %:keyword%")
    Page<Boast> findTitleAndContentByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct b from Boast b where b.no = :no")
    Optional<Boast> findByNo(@Param("no") int no);

    @Transactional
    @Modifying
    @Query("update Boast b set b.hit = b.hit + 1 where b.no = :no")
    void increaseViewCount(int no);

    @Query("select max(b.no) from Boast b")
    int findMaxNo();


    @Query("select distinct b from Boast b "
            + "left outer join CafeUser u1 on b.author = u1 "
            + "left outer join BoastComment c on c.boast = b "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "b.title like %:keyword% "
            + "or b.content like %:keyword% "
            + "or b.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    List<Boast> findAllByKeyword(@Param("keyword") String keyword);

    @Query("select distinct b from Boast b "
            + "left outer join CafeUser u1 on b.author = u1 "
            + "left outer join BoastComment c on c.boast = b "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "b.title like %:keyword%")
    List<Boast> findTitleByKeyword(@Param("keyword") String keyword);

    @Query("select distinct b from Boast b "
            + "left outer join CafeUser u1 on b.author = u1 "
            + "left outer join BoastComment c on c.boast = b "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "b.author.id like %:keyword%")
    List<Boast> findAuthorByKeyword(@Param("keyword") String keyword);

    @Query("select distinct b from Boast b "
            + "left outer join CafeUser u1 on b.author = u1 "
            + "left outer join BoastComment c on c.boast = b "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "b.title like %:keyword% "
            + "or b.content like %:keyword% "
            + "or c.content like %:keyword%")
    List<Boast> findTitleAndContentByKeyword(@Param("keyword") String keyword);

    void deleteByAuthor(CafeUser user);
}
