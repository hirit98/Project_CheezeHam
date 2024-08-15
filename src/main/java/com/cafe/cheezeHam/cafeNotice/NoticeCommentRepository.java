package com.cafe.cheezeHam.cafeNotice;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Integer> {
    @Query("select nc from NoticeComment nc where nc.notice.no = :no order by nc.regDate desc")
    List<NoticeComment> findAllByNo(@Param("no") int no);

    @Query("select nc from NoticeComment nc where nc.notice.no = :no order by nc.regDate asc")
    List<NoticeComment> findAllByRegNoOrderByRegDateAsc(@Param("no") int no);

    @Query("select nc from NoticeComment nc where nc.notice.no = :no order by nc.regDate desc")
    List<NoticeComment>findAllByRegNoOrderByRegDateDesc(@Param("no") int no);

    @Query("SELECT COUNT(nc) FROM NoticeComment nc WHERE nc.cafeUser.id = :id")
    long countByAuthorId(@Param("id") String id);

    void deleteByCafeUser(CafeUser user);
}
