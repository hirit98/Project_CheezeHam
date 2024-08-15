package com.cafe.cheezeHam.cafeBoast;

import com.cafe.cheezeHam.cafeEvent.Event;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
public class BoastComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int comment_no;

    @ManyToOne
    private CafeUser cafeUser;

    @ManyToOne
    private Boast boast;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column()
    private LocalDateTime regDate;

    @Column()
    private LocalDateTime upDate;
}
