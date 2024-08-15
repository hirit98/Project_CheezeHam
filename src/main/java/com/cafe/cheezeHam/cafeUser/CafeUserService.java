package com.cafe.cheezeHam.cafeUser;

import com.cafe.cheezeHam.cafeAccuse.AccuseCommentRepository;
import com.cafe.cheezeHam.cafeAccuse.AccuseRepository;
import com.cafe.cheezeHam.cafeBoast.BoastCommentRepository;
import com.cafe.cheezeHam.cafeBoast.BoastRepository;
import com.cafe.cheezeHam.cafeCatBuy.CatBuyCommentRepository;
import com.cafe.cheezeHam.cafeCatBuy.CatBuyRepository;
import com.cafe.cheezeHam.cafeEvent.EventCommentRepository;
import com.cafe.cheezeHam.cafeEvent.EventRepository;
import com.cafe.cheezeHam.cafeFree.FreeCommentRepository;
import com.cafe.cheezeHam.cafeFree.FreeRepository;
import com.cafe.cheezeHam.cafeHamBuy.HamBuyCommentRepository;
import com.cafe.cheezeHam.cafeHamBuy.HamBuyRepository;
import com.cafe.cheezeHam.cafeNotice.NoticeCommentRepository;
import com.cafe.cheezeHam.cafeNotice.NoticeRepository;
import com.cafe.cheezeHam.cafeQna.QnaCommentRepository;
import com.cafe.cheezeHam.cafeQna.QnaRepository;
import com.cafe.cheezeHam.cafeRegGreeting.RegCommentRepository;
import com.cafe.cheezeHam.cafeRegGreeting.RegGreetingRepository;
import com.cafe.cheezeHam.cafeSuggest.SuggestCommentRepository;
import com.cafe.cheezeHam.cafeSuggest.SuggestRepository;
import com.sbb.demo.DataNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CafeUserService {
    private final CafeUserRepository cafeUserRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeCommentRepository noticeCommentRepository;
    private final EventRepository eventRepository;
    private final EventCommentRepository eventCommentRepository;
    private final AccuseRepository accuseRepository;
    private final AccuseCommentRepository accuseCommentRepository;
    private final BoastRepository boastRepository;
    private final BoastCommentRepository boastCommentRepository;
    private final CatBuyRepository catBuyRepository;
    private final CatBuyCommentRepository catBuyCommentRepository;
    private final FreeRepository freeRepository;
    private final FreeCommentRepository freeCommentRepository;
    private final HamBuyRepository hamBuyRepository;
    private final HamBuyCommentRepository hamBuyCommentRepository;
    private final QnaRepository qnaRepository;
    private final QnaCommentRepository qnaCommentRepository;
    private final RegGreetingRepository regGreetingRepository;
    private final RegCommentRepository regCommentRepository;
    private final SuggestRepository suggestRepository;
    private final SuggestCommentRepository suggestCommentRepository;

    public CafeUser create(CafeUser user){
        this.cafeUserRepository.save(user);
        return user;
    }

    public Long getUserNo(String username) {
        Optional<Long> userId = cafeUserRepository.findUserIdByUsername(username);
        return userId.orElseThrow(() -> new DataNotFoundException("CafeUser NOT FOUND"));
    }

    public void createADMIN(CafeUser user){this.cafeUserRepository.save(user);}

    public CafeUser getUser(String id) {
        Optional<CafeUser> cafeUser = this.cafeUserRepository.findByid(id);
        if(cafeUser.isPresent()) {
            return cafeUser.get();
        } else {
            throw new DataNotFoundException("CafeUser NOT FOUND");
        }
    }

    public CafeUser getUserByNo(int no) {
        Optional<CafeUser> cafeUser = this.cafeUserRepository.findByno(no);
        if(cafeUser.isPresent()) {
            return cafeUser.get();
        } else {
            throw new DataNotFoundException("CafeUser NOT FOUND");
        }
    }

    @Transactional
    public void deleteCafeUser(String id){
        CafeUser user = this.cafeUserRepository.findById(id);
        if(user != null) {
            noticeRepository.deleteByAuthor(user);
            noticeCommentRepository.deleteByCafeUser(user);
            eventRepository.deleteByAuthor(user);
            eventCommentRepository.deleteByCafeUser(user);
            accuseRepository.deleteByAuthor(user);
            accuseCommentRepository.deleteByCafeUser(user);
            boastRepository.deleteByAuthor(user);
            boastCommentRepository.deleteByCafeUser(user);
            catBuyRepository.deleteByAuthor(user);
            catBuyCommentRepository.deleteByCafeUser(user);
            freeRepository.deleteByAuthor(user);
            freeCommentRepository.deleteByCafeUser(user);
            hamBuyRepository.deleteByAuthor(user);
            hamBuyCommentRepository.deleteByCafeUser(user);
            qnaRepository.deleteByAuthor(user);
            qnaCommentRepository.deleteByCafeUser(user);
            regGreetingRepository.deleteByAuthor(user);
            regCommentRepository.deleteByCafeUser(user);
            suggestRepository.deleteByAuthor(user);
            suggestCommentRepository.deleteByCafeUser(user);

            this.cafeUserRepository.delete(user);
        } else {
            throw new DataNotFoundException("CafeUser NOT FOUND");
        }
    }

    public void modifyCafeUser(CafeUser user){ this.cafeUserRepository.save(user); }

    public Page<CafeUser> getAllUsers(int page, int pageSize, String keyword){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("no"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        return this.cafeUserRepository.findUserBykeyword(keyword, pageable);
    }

    public Page<CafeUser> getCafeUsers(int page, int pageSize, String keyword){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("no"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        return this.cafeUserRepository.findRoleBykeyword(keyword, pageable);
    }

    public Page<CafeUser> getCafeAdmin(int page, int pageSize, String keyword){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("no"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        return this.cafeUserRepository.findAdminBykeyword(keyword, pageable);
    }
}
