package com.cafe.cheezeHam.cafeCatBuy;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CatBuyCommentRepository extends JpaRepository<CatBuyComment, Integer> {

    @Query("select cc from CatBuyComment cc where cc.catBuy.no = :no order by cc.regDate desc ")
    List<CatBuyComment> findAllById(@Param("no") int no);


    @Query("select cc from CatBuyComment cc where cc.catBuy.no = :no order by cc.regDate asc")
    List<CatBuyComment> findAllByNoOrderByRegDateAsc(@Param("no") int no);

    @Query("select cc from CatBuyComment cc where cc.catBuy.no = :no order by cc.regDate desc")
    List<CatBuyComment> findAllByNoOrderByRegDateDesc(@Param("no") int no);

    @Query("SELECT COUNT(cc) FROM CatBuyComment cc WHERE cc.cafeUser.id = :id")
    long countByAuthorId(@Param("id") String id);

    void deleteByCafeUser(CafeUser user);
}
