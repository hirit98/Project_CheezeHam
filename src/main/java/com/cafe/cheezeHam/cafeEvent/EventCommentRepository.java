package com.cafe.cheezeHam.cafeEvent;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventCommentRepository extends JpaRepository<EventComment, Integer> {

    @Query("select ec from EventComment ec where ec.event.no = :no order by ec.regDate desc ")
    List<EventComment> findAllById(@Param("no") int no);


    @Query("select ec from EventComment ec where ec.event.no = :no order by ec.regDate asc")
    List<EventComment> findAllByEventNoOrderByRegDateAsc(@Param("no") int no);

    @Query("select ec from EventComment ec where ec.event.no = :no order by ec.regDate desc")
    List<EventComment> findAllByEventNoOrderByRegDateDesc(@Param("no") int no);

    @Query("SELECT COUNT(ec) FROM EventComment ec WHERE ec.cafeUser.id = :id")
    long countByAuthorId(@Param("id") String id);

    void deleteByCafeUser(CafeUser user);
}
