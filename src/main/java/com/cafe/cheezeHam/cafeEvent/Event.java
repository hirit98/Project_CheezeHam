package com.cafe.cheezeHam.cafeEvent;


import com.cafe.cheezeHam.cafeTotalBoard.TotalBoard;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Event implements TotalBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int no;

    private String type;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne
    private CafeUser author;

    private LocalDateTime reg_date;

    private LocalDateTime update_date;

    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
    private List<EventComment> commentList;

    @Column(columnDefinition = "INT DEFAULT 0")
    private  int hit;

    private String file_path;

    @ManyToMany
    Set<CafeUser> voter;

    @Override
    public int getNo() {
        return no;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public LocalDateTime getRegDate() {
        return reg_date;
    }

    @Override
    public LocalDateTime getUpdateDate() {
        return update_date;
    }

    @Override
    public int getHit() {
        return hit;
    }

    @Override
    public CafeUser getAuthor() {
        return author;
    }

    @Override
    public Set<CafeUser> getVoter() {
        return voter;
    }

    @Override
    public List<EventComment> getCommentList() {
        return commentList;
    }

    @Override
    public String getFile_path() {return file_path;}
}
