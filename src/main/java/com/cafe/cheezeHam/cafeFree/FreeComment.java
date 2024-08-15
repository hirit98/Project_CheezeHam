package com.cafe.cheezeHam.cafeFree;

import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class FreeComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int comment_no;

    @ManyToOne
    private CafeUser cafeUser;

    @ManyToOne
    private Free free;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column()
    private LocalDateTime regDate;

    @Column()
    private LocalDateTime upDate;
}
