package com.cafe.cheezeHam.cafeHamBuy;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class HamBuyComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int comment_no;

    @ManyToOne
    private CafeUser cafeUser;

    @ManyToOne
    private HamBuy hamBuy;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column()
    private LocalDateTime regDate;

    @Column()
    private LocalDateTime upDate;
}
