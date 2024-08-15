package com.cafe.cheezeHam.cafeRegGreeting;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegCommentRepository extends JpaRepository<RegComment, Integer> {
    @Query("select rc from RegComment rc where rc.regGreeting.no = :no order by rc.regDate desc ")
    List<RegComment> findAllById(@Param("no") int no);


    @Query("select rc from RegComment rc where rc.regGreeting.no = :no order by rc.regDate asc")
    List<RegComment> findAllByNoOrderByRegDateAsc(@Param("no") int no);

    @Query("select rc from RegComment rc where rc.regGreeting.no = :no order by rc.regDate desc")
    List<RegComment> findAllByNoOrderByRegDateDesc(@Param("no") int no);

    @Query("SELECT COUNT(rc) FROM RegComment rc WHERE rc.cafeUser.id = :id")
    long countByAuthorId(@Param("id") String id);

    void deleteByCafeUser(CafeUser user);
}
