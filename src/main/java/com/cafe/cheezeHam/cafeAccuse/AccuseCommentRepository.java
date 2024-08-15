package com.cafe.cheezeHam.cafeAccuse;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccuseCommentRepository extends JpaRepository<AccuseComment, Integer> {
    @Query("select ac from AccuseComment ac where ac.accuse.no = :no order by ac.regDate desc ")
    List<AccuseComment> findAllById(@Param("no") int no);


    @Query("select ac from AccuseComment ac where ac.accuse.no = :no order by ac.regDate asc")
    List<AccuseComment> findAllByAccuseNoOrderByRegDateAsc(@Param("no") int no);

    @Query("select ac from AccuseComment ac where ac.accuse.no = :no order by ac.regDate desc")
    List<AccuseComment> findAllByAccuseNoOrderByRegDateDesc(@Param("no") int no);

    @Query("SELECT COUNT(ac) FROM AccuseComment ac WHERE ac.cafeUser.id = :id")
    long countByAuthorId(@Param("id") String id);

    void deleteByCafeUser(CafeUser user);
}
