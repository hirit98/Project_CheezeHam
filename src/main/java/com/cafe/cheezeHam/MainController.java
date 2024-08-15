package com.cafe.cheezeHam;

import com.cafe.cheezeHam.cafeBoast.Boast;
import com.cafe.cheezeHam.cafeBoast.BoastRepository;
import com.cafe.cheezeHam.cafeEvent.Event;
import com.cafe.cheezeHam.cafeEvent.EventRepository;
import com.cafe.cheezeHam.cafeTotalBoard.TotalBoard;
import com.cafe.cheezeHam.cafeTotalBoard.TotalBoardService;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import com.cafe.cheezeHam.cafeUser.CafeUserRepository;
import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeNotice.NoticeRepository;
import com.cafe.cheezeHam.cafeUser.CafeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@SessionAttributes("username")
public class MainController {

    private final CafeUserService cafeUserService;
    private final NoticeRepository noticeRepository;
    private final EventRepository eventRepository;
    private final CafeUserRepository cafeUserRepository;
    private final BoastRepository boastRepository;
    private final TotalBoardService totalBoardService;

    @GetMapping("/")
    public String root(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();     // 현재 사용자의 인증 정보를 가져옴.
        if(authentication != null && authentication.isAuthenticated()){     // 인증정보가 null이 아니고 사용자 인증이 되어있는지 확인.
            String id = authentication.getName();                           // id 값에 현재 인증된 사용자의 Name(unique 값)을 저장. hong
            Optional<CafeUser> user = cafeUserRepository.findByid(id);      // Optional 타입으로 CafeUser타입의 객체에 저장했던 id값으로 해당 사용자의 정보를 DB에서 조회.
            if(user.isPresent()){
                CafeUser profile = this.cafeUserService.getUser(id);        // 해당 user의 객체가 존재한다면 if 문 실행 -> 존재하지 않으면 바로 main 페이지로 이동.
                String username = user.get().getUsername();                          // user.get().getUsername() 메서드로 해당 유저의 UserName을 가져와 저장.
                LocalDate userReg = LocalDate.from(profile.getRegdate());
                String images = profile.getProfile();

                model.addAttribute("username", username);
                model.addAttribute("RegDate", userReg);         // 프론트에서 접근 가능하게 addAttribute로 "username" 으로 저장.
                model.addAttribute("images", images);
            }
        }
        List<Boast> recentBoast = boastRepository.findRecentBoast();
        List<Notice> importantNotice = noticeRepository.findImportantNotice();
        List<Notice> recentNotice = noticeRepository.findRecentNotice();
        List<Event> recentEvent = eventRepository.findRecentEvent();
        List<TotalBoard> recentTotal = totalBoardService.getRecentTotalBoards();

        model.addAttribute("importantNotice", importantNotice);
        model.addAttribute("recentNotice", recentNotice);
        model.addAttribute("recentEvent", recentEvent);
        model.addAttribute("recentBoast", recentBoast);
        model.addAttribute("recentTotal", recentTotal);

        return "main_content";

    }


}
