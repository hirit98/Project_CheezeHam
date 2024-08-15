package com.cafe.cheezeHam.cafeFree;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FreeRepository extends JpaRepository<Free, Integer> {

    @Query("select distinct f from Free f order by f.no desc limit 5")
    List<Free> findRecentFrees();

    @Query("select count(f) from Free f where f.author.id = :id")
    long countByAuthorId(@Param("id") String id);

    @Query("select distinct f from Free f "
            + "left outer join CafeUser u1 on f.author = u1 "
            + "left outer join FreeComment c on c.free = f "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "f.title like %:keyword% "
            + "or f.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    Page<Free> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct f from Free f "
            + "left outer join CafeUser u1 on f.author = u1 "
            + "left outer join FreeComment c on c.free = f "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "f.title like %:keyword%")
    Page<Free> findTitleByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct f from Free f "
            + "left outer join CafeUser u1 on f.author = u1 "
            + "left outer join FreeComment c on c.free = f "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "f.author.id like %:keyword%")
    Page<Free> findAuthorByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct f from Free f "
            + "left outer join CafeUser u1 on f.author = u1 "
            + "left outer join FreeComment c on c.free = f "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "f.title like %:keyword% "
            + "or f.content like %:keyword% "
            + "or c.content like %:keyword%")
    Page<Free> findTitleAndContentByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Free f set f.hit = f.hit + 1 where f.no = :no")
    void increaseViewCount(@Param("no") int no);

    @Query("select max(f.no) from Free f")
    int findMaxNo();

    // 페이징 없는 List 반환
    @Query("select distinct f from Free f "
            + "left outer join CafeUser u1 on f.author = u1 "
            + "left outer join FreeComment c on c.free = f "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "f.title like %:keyword% "
            + "or f.content like %:keyword% "
            + "or u1.id like %:keyword% "
            + "or c.content like %:keyword% "
            + "or u2.id like %:keyword%")
    List<Free> findAllByKeyword(@Param("keyword") String keyword);

    @Query("select distinct f from Free f "
            + "left outer join CafeUser u1 on f.author = u1 "
            + "left outer join FreeComment c on c.free = f "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "f.title like %:keyword%")
    List<Free> findTitleByKeyword(@Param("keyword") String keyword);

    @Query("select distinct f from Free f "
            + "left outer join CafeUser u1 on f.author = u1 "
            + "left outer join FreeComment c on c.free = f "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "f.author.id like %:keyword%")
    List<Free> findAuthorByKeyword(@Param("keyword") String keyword);

    @Query("select distinct f from Free f "
            + "left outer join CafeUser u1 on f.author = u1 "
            + "left outer join FreeComment c on c.free = f "
            + "left outer join CafeUser u2 on c.cafeUser = u2 "
            + "where "
            + "f.title like %:keyword% "
            + "or f.content like %:keyword% "
            + "or c.content like %:keyword%")
    List<Free> findTitleAndContentByKeyword(@Param("keyword") String keyword);

    void deleteByAuthor(CafeUser user);
}
