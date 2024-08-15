package com.cafe.cheezeHam.cafeAccuse;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class AccuseComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int comment_no;

    @ManyToOne
    private CafeUser cafeUser;

    @ManyToOne
    private Accuse accuse;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column()
    private LocalDateTime regDate;

    @Column()
    private LocalDateTime upDate;
}
