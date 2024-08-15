package com.cafe.cheezeHam.cafeBoast;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoastCommentRepository extends JpaRepository<BoastComment, Integer> {
    @Query("select bc from BoastComment bc where bc.boast.no = :no order by bc.regDate desc ")
    List<BoastComment> findAllById(@Param("no") int no);


    @Query("select bc from BoastComment bc where bc.boast.no = :no order by bc.regDate asc")
    List<BoastComment> findAllByNoOrderByRegDateAsc(@Param("no") int no);

    @Query("select bc from BoastComment bc where bc.boast.no = :no order by bc.regDate desc")
    List<BoastComment> findAllByNoOrderByRegDateDesc(@Param("no") int no);

    @Query("SELECT COUNT(bc) FROM BoastComment bc WHERE bc.cafeUser.id = :id")
    long countByAuthorId(@Param("id") String id);

    void deleteByCafeUser(CafeUser user);
}
