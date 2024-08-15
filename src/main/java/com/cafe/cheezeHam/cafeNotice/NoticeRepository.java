package com.cafe.cheezeHam.cafeNotice;

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

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    @Query("select distinct n from Notice n where n.type = '중요' order by n.no desc limit 5")
    List<Notice> findImportantNotice();

    @Query("select distinct n from Notice n where n.type = '중요' order by n.no desc limit 10")
    List<Notice> findImportantNoticeList();

    @Query("select distinct n from Notice n order by n.no desc limit 5")
    List<Notice> findRecentNotice();

    @Query("select count(n) from Notice n where n.author.id = :id")
    long countByAuthorId(@Param("id") String id);

    @Query("select distinct n from Notice n "
            + "left outer join CafeUser u1 on n.author = u1 "
            + "left outer join NoticeComment c on c.notice = n "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "n.title like %:keyword% "
            + "or n.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    Page<Notice> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct n from Notice n "
            + "left outer join CafeUser u1 on n.author = u1 "
            + "left outer join NoticeComment c on c.notice = n "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "n.title like %:keyword%")
    Page<Notice> findTitleByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct n from Notice n "
            + "left outer join CafeUser u1 on n.author = u1 "
            + "left outer join NoticeComment c on c.notice = n "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "n.author.id like %:keyword%")
    Page<Notice> findAuthorByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct n from Notice n "
            + "left outer join CafeUser u1 on n.author = u1 "
            + "left outer join NoticeComment c on c.notice = n "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "n.title like %:keyword% "
            + "or n.content like %:keyword% "
            + "or c.content like %:keyword%")
    Page<Notice> findTitleAndContentByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Notice n set n.hit = n.hit + 1 where n.no = :no")
    void increaseViewCount(@Param("no") int no);

    @Query("select max(n.no) from Notice n")
    int findMaxNo();

    // 페이징 없는 List 반환
    @Query("select distinct n from Notice n "
            + "left outer join CafeUser u1 on n.author = u1 "
            + "left outer join NoticeComment c on c.notice = n "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "n.title like %:keyword% "
            + "or n.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    List<Notice> findAllByKeyword(@Param("keyword") String keyword);

    @Query("select distinct n from Notice n "
            + "left outer join CafeUser u1 on n.author = u1 "
            + "left outer join NoticeComment c on c.notice = n "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "n.title like %:keyword%")
    List<Notice> findTitleByKeyword(@Param("keyword") String keyword);

    @Query("select distinct n from Notice n "
            + "left outer join CafeUser u1 on n.author = u1 "
            + "left outer join NoticeComment c on c.notice = n "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "n.author.id like %:keyword%")
    List<Notice> findAuthorByKeyword(@Param("keyword") String keyword);

    @Query("select distinct n from Notice n "
            + "left outer join CafeUser u1 on n.author = u1 "
            + "left outer join NoticeComment c on c.notice = n "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "n.title like %:keyword% "
            + "or n.content like %:keyword% "
            + "or c.content like %:keyword%")
    List<Notice> findTitleAndContentByKeyword(@Param("keyword") String keyword);

    @Query("select distinct n from Notice n where n.title = '카페규칙사항'")
    Optional<Notice> findByTitle();

    void deleteByAuthor(CafeUser user);
}
