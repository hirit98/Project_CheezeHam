package com.cafe.cheezeHam.cafeTotalBoard;

import com.cafe.cheezeHam.cafeUser.CafeUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TotalBoard {
    int getNo();
    String getTitle();
    LocalDateTime getRegDate();
    String getType();
    LocalDateTime getUpdateDate();
    int getHit();
    CafeUser getAuthor();
    Set<CafeUser> getVoter();
    List<?> getCommentList();
    String getFile_path();
}
