package com.cafe.cheezeHam.cafeHamBuy;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HamBuyCommentRepository extends JpaRepository<HamBuyComment, Integer> {
    @Query("select hc from HamBuyComment hc where hc.hamBuy.no = :no order by hc.regDate desc ")
    List<HamBuyComment> findAllById(@Param("no") int no);


    @Query("select hc from HamBuyComment hc where hc.hamBuy.no = :no order by hc.regDate asc")
    List<HamBuyComment> findAllByNoOrderByRegDateAsc(@Param("no") int no);

    @Query("select hc from HamBuyComment hc where hc.hamBuy.no = :no order by hc.regDate desc")
    List<HamBuyComment> findAllByNoOrderByRegDateDesc(@Param("no") int no);

    @Query("SELECT COUNT(hc) FROM HamBuyComment hc WHERE hc.cafeUser.id = :id")
    long countByAuthorId(@Param("id") String id);

    void deleteByCafeUser(CafeUser user);
}
