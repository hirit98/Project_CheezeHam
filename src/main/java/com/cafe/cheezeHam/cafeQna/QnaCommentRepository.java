package com.cafe.cheezeHam.cafeQna;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QnaCommentRepository extends JpaRepository<QnaComment, Integer> {

    @Query("select qc from QnaComment qc where qc.qna.no = :no order by qc.regDate desc ")
    List<QnaComment> findAllById(@Param("no") int no);


    @Query("select qc from QnaComment qc where qc.qna.no = :no order by qc.regDate asc")
    List<QnaComment> findAllByNoOrderByRegDateAsc(@Param("no") int no);

    @Query("select qc from QnaComment qc where qc.qna.no = :no order by qc.regDate desc")
    List<QnaComment> findAllByNoOrderByRegDateDesc(@Param("no") int no);

    @Query("SELECT COUNT(qc) FROM QnaComment qc WHERE qc.cafeUser.id = :id")
    long countByAuthorId(@Param("id") String id);

    void deleteByCafeUser(CafeUser user);
}
