package com.cafe.cheezeHam.cafeUser;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CafeUserRepository extends JpaRepository<CafeUser, Long> {
    @Query("select distinct cf from CafeUser cf where cf.no = :no")
    Optional<CafeUser> findByno(@Param("no") int no);
    Optional<CafeUser> findByid(String id);

    @Query("select distinct cf from CafeUser cf where cf.id = :id")
    CafeUser findById(@Param("id") String id);

    @Query("SELECT cf.id FROM CafeUser cf WHERE cf.username = :id")
    Optional<Long> findUserIdByUsername(@Param("id") String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM CafeUser u WHERE u.username = :username")
    void deleteByUsername(@Param("username") String username);

    @Query("select distinct u from CafeUser u where " +
            "(u.username like %:keyword% or " +
            "u.id like %:keyword% or " +
            "u.email like %:keyword% or " +
            "u.phone like %:keyword% or " +
            "u.grade like %:keyword%) and " +
            "u.ROLE = 'user' " +
            "order by u.no desc")
    Page<CafeUser> findUserBykeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct u from CafeUser u where " +
            "(u.username like %:keyword% or " +
            "u.id like %:keyword% or " +
            "u.email like %:keyword% or " +
            "u.phone like %:keyword% or " +
            "u.grade like %:keyword%) and " +
            "u.ROLE = 'user'" +
            "order by u.no desc")
    Page<CafeUser> findRoleBykeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select distinct u from CafeUser u where " +
            "(u.username like %:keyword% or " +
            "u.id like %:keyword% or " +
            "u.email like %:keyword% or " +
            "u.phone like %:keyword% or " +
            "u.grade like %:keyword%) and " +
            "u.ROLE = 'admin' " +
            "order by u.no desc")
    Page<CafeUser> findAdminBykeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<CafeUser> findUserByid(@Param("id") String id, Pageable pageable);

    Page<CafeUser> findUserByusername(@Param("username") String username, Pageable pageable);

    Page<CafeUser> findUserByphone(@Param("phone") String phone, Pageable pageable);

    Page<CafeUser> findUserByemail(@Param("email") String email, Pageable pageable);

}
