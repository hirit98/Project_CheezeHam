package com.cafe.cheezeHam.cafeSuggest;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SuggestCommentRepository extends JpaRepository<SuggestComment, Integer> {
    @Query("select sc from SuggestComment sc where sc.suggest.no = :no order by sc.regDate desc ")
    List<SuggestComment> findAllById(@Param("no") int no);

    @Query("select sc from SuggestComment sc where sc.suggest.no = :no order by sc.regDate asc")
    List<SuggestComment> findAllBySuggestNoOrderByRegDateAsc(@Param("no") int no);

    @Query("select sc from SuggestComment sc where sc.suggest.no = :no order by sc.regDate desc")
    List<SuggestComment> findAllBySuggestNoOrderByRegDateDesc(@Param("no") int no);

    @Query("SELECT COUNT(sc) FROM SuggestComment sc WHERE sc.cafeUser.id = :id")
    long countByAuthorId(@Param("id") String id);

    void deleteByCafeUser(CafeUser user);
}
