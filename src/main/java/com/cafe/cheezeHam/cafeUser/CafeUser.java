package com.cafe.cheezeHam.cafeUser;


import com.cafe.cheezeHam.cafeBoast.Boast;
import com.cafe.cheezeHam.cafeBoast.BoastComment;
import com.cafe.cheezeHam.cafeEvent.Event;
import com.cafe.cheezeHam.cafeEvent.EventComment;
import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeNotice.NoticeComment;
import com.cafe.cheezeHam.cafeNotice.NoticeCommentRepository;
import com.cafe.cheezeHam.cafeNotice.NoticeRepository;
import com.cafe.cheezeHam.cafeRegGreeting.RegComment;
import com.cafe.cheezeHam.cafeRegGreeting.RegGreeting;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
public class CafeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(unique = true)
    private String id;

    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    private String birthday;

    private String phone;

    private String gender;

    private LocalDateTime regdate;

    private String ROLE;

    private String grade;

    private String profile;

}
