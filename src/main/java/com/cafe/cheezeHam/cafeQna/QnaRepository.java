package com.cafe.cheezeHam.cafeQna;

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

public interface QnaRepository  extends JpaRepository<Qna, Integer> {

    @Query("select distinct q from Qna q order by q.no desc limit 5")
    List<Qna> findRecentQnas();

    @Query("select count(q) from Qna q where q.author.id = :id")
    long countByAuthorId(@Param("id") String id);

    @Query("select distinct q from Qna q "
            + "left outer join CafeUser u1 on q.author = u1 "
            + "left outer join QnaComment c on c.qna = q "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "q.title like %:keyword% "
            + "or q.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    Page<Qna> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct q from Qna q "
            + "left outer join CafeUser u1 on q.author = u1 "
            + "left outer join QnaComment c on c.qna = q "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "q.title like %:keyword%")
    Page<Qna> findTitleByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct q from Qna q "
            + "left outer join CafeUser u1 on q.author = u1 "
            + "left outer join QnaComment c on c.qna = q "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "q.author.id like %:keyword%")
    Page<Qna> findAuthorByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct q from Qna q "
            + "left outer join CafeUser u1 on q.author = u1 "
            + "left outer join QnaComment c on c.qna = q "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "q.title like %:keyword% "
            + "or q.content like %:keyword% "
            + "or c.content like %:keyword%")
    Page<Qna> findTitleAndContentByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct q from Qna q where q.no = :no")
    Optional<Qna> findByNo(@Param("no") int no);

    @Transactional
    @Modifying
    @Query("update Qna q set q.hit = q.hit + 1 where q.no = :no")
    void increaseViewCount(@Param("no") int no);

    @Query("select max(q.no) from Qna q")
    int findMaxNo();

    @Query("select distinct q from Qna q "
            + "left outer join CafeUser u1 on q.author = u1 "
            + "left outer join QnaComment c on c.qna = q "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "q.title like %:keyword% "
            + "or q.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    List<Qna> findAllByKeyword(@Param("keyword") String keyword);

    @Query("select distinct q from Qna q "
            + "left outer join CafeUser u1 on q.author = u1 "
            + "left outer join QnaComment c on c.qna = q "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "q.title like %:keyword%")
    List<Qna> findTitleByKeyword(@Param("keyword") String keyword);

    @Query("select distinct q from Qna q "
            + "left outer join CafeUser u1 on q.author = u1 "
            + "left outer join QnaComment c on c.qna = q "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "q.author.id like %:keyword%")
    List<Qna> findAuthorByKeyword(@Param("keyword") String keyword);

    @Query("select distinct q from Qna q "
            + "left outer join CafeUser u1 on q.author = u1 "
            + "left outer join QnaComment c on c.qna = q "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "q.title like %:keyword% "
            + "or q.content like %:keyword% "
            + "or c.content like %:keyword%")
    List<Qna> findTitleAndContentByKeyword(@Param("keyword") String keyword);

    void deleteByAuthor(CafeUser user);
}
