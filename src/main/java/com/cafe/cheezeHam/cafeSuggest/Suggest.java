package com.cafe.cheezeHam.cafeSuggest;

import com.cafe.cheezeHam.cafeTotalBoard.TotalBoard;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Suggest implements TotalBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int no;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private String type;

    @ManyToOne
    private CafeUser author;

    private LocalDateTime reg_date;

    private LocalDateTime update_date;

    @OneToMany(mappedBy = "suggest", cascade = CascadeType.REMOVE)
    private List<SuggestComment> commentList;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int hit;

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
    public List<SuggestComment> getCommentList() {
        return commentList;
    }

    @Override
    public String getFile_path() {return file_path;}
}
