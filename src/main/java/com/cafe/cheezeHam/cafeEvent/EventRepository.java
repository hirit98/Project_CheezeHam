package com.cafe.cheezeHam.cafeEvent;

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

public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("select distinct e from Event e order by e.no desc limit 5")
    List<Event> findRecentEvent();

    @Query("select count(e) from Event e where e.author.id = :id")
    long countByAuthorId(@Param("id") String id);

    @Query("select distinct e from Event e "
            + "left outer join CafeUser u1 on e.author = u1 "
            + "left outer join EventComment c on c.event = e "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "e.title like %:keyword% "
            + "or e.content like %:keyword% "
            + "or e.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    Page<Event> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct e from Event e "
            + "left outer join CafeUser u1 on e.author = u1 "
            + "left outer join EventComment c on c.event = e "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "e.title like %:keyword%")
    Page<Event> findTitleByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct e from Event e "
            + "left outer join CafeUser u1 on e.author = u1 "
            + "left outer join EventComment c on c.event = e "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "e.author.id like %:keyword%")
    Page<Event> findAuthorByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct e from Event e "
            + "left outer join CafeUser u1 on e.author = u1 "
            + "left outer join EventComment c on c.event = e "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "e.title like %:keyword% "
            + "or e.content like %:keyword% "
            + "or c.content like %:keyword%")
    Page<Event> findTitleAndContentByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct e from Event e where e.no = :no")
    Optional<Event> findByNo(@Param("no") int no);

    @Query("select max(e.no) from Event e")
    int findMaxNo();

    @Transactional
    @Modifying
    @Query("update Event e set e.hit = e.hit + 1 where e.no = :no")
    void increaseViewCount(int no);

    // 페이징 없는 List 반환
    @Query("select distinct e from Event e "
            + "left outer join CafeUser u1 on e.author = u1 "
            + "left outer join EventComment c on c.event = e "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "e.title like %:keyword% "
            + "or e.content like %:keyword% "
            + "or e.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    List<Event> findAllByKeyword(@Param("keyword") String keyword);

    @Query("select distinct e from Event e "
            + "left outer join CafeUser u1 on e.author = u1 "
            + "left outer join EventComment c on c.event = e "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "e.title like %:keyword%")
    List<Event> findTitleByKeyword(@Param("keyword") String keyword);

    @Query("select distinct e from Event e "
            + "left outer join CafeUser u1 on e.author = u1 "
            + "left outer join EventComment c on c.event = e "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "e.author.id like %:keyword%")
    List<Event> findAuthorByKeyword(@Param("keyword") String keyword);

    @Query("select distinct e from Event e "
            + "left outer join CafeUser u1 on e.author = u1 "
            + "left outer join EventComment c on c.event = e "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "e.title like %:keyword% "
            + "or e.content like %:keyword% "
            + "or c.content like %:keyword%")
    List<Event> findTitleAndContentByKeyword(@Param("keyword") String keyword);

    void deleteByAuthor(CafeUser user);

}
