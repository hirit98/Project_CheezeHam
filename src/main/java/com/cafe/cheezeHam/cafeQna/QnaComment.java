package com.cafe.cheezeHam.cafeQna;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
public class QnaComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int comment_no;

    @ManyToOne
    private CafeUser cafeUser;

    @ManyToOne
    private Qna qna;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column()
    private LocalDateTime regDate;

    @Column()
    private LocalDateTime upDate;
}
