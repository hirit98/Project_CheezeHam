package com.cafe.cheezeHam.cafeFree;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FreeCommentRepository extends JpaRepository<FreeComment, Integer> {
    @Query("select fc from FreeComment fc where fc.free.no = :no order by fc.regDate desc")
    List<FreeComment> findAllByNo(@Param("no") int no);

    @Query("select fc from FreeComment fc where fc.free.no = :no order by fc.regDate asc")
    List<FreeComment> findAllByRegNoOrderByRegDateAsc(@Param("no") int no);

    @Query("select fc from FreeComment fc where fc.free.no = :no order by fc.regDate desc")
    List<FreeComment>findAllByRegNoOrderByRegDateDesc(@Param("no") int no);

    @Query("SELECT COUNT(fc) FROM FreeComment fc WHERE fc.cafeUser.id = :id")
    long countByAuthorId(@Param("id") String id);

    void deleteByCafeUser(CafeUser user);
}
