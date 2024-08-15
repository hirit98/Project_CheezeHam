package com.cafe.cheezeHam.cafeRegGreeting;

import com.cafe.cheezeHam.cafeEvent.Event;
import com.cafe.cheezeHam.cafeEvent.EventComment;
import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegGreetingService {

    private final RegGreetingRepository regGreetingRepository;
    private final RegCommentRepository regCommentRepository;


    public void create(String title, /*MultipartFile file,*/ String content, CafeUser user) throws IOException {
        RegGreeting regGreeting = new RegGreeting();

        regGreeting.setTitle(title);
        regGreeting.setContent(content);
        regGreeting.setAuthor(user);
        regGreeting.setReg_date(LocalDateTime.now());
        regGreeting.setType("가입인사");
        this.regGreetingRepository.save(regGreeting);
    }

    public Page<RegGreeting> getRegGreetings(int page, int pageSize, int field, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("reg_date"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        if (field == 1) {
            return this.regGreetingRepository.findTitleByKeyword(keyword, pageable);
        } else if (field == 2) {
            return this.regGreetingRepository.findAuthorByKeyword(keyword, pageable);
        } else if (field == 3) {
            return this.regGreetingRepository.findTitleAndContentByKeyword(keyword, pageable);
        }
        return this.regGreetingRepository.findAllByKeyword(keyword, pageable);

    }

    public Page<RegGreeting> getRegGreetingAsc(int page, int pageSize, int field, String keyword) {
        List<RegGreeting> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.regGreetingRepository.findTitleByKeyword(keyword);
            case 2 -> this.regGreetingRepository.findAuthorByKeyword(keyword);
            case 3 -> this.regGreetingRepository.findTitleAndContentByKeyword(keyword);
            default -> this.regGreetingRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((RegGreeting r) -> r.getVoter().size()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<RegGreeting> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public Page<RegGreeting> getRegGreetingDesc(int page, int pageSize, int field, String keyword) {
        List<RegGreeting> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.regGreetingRepository.findTitleByKeyword(keyword);
            case 2 -> this.regGreetingRepository.findAuthorByKeyword(keyword);
            case 3 -> this.regGreetingRepository.findTitleAndContentByKeyword(keyword);
            default -> this.regGreetingRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((RegGreeting r) -> r.getVoter().size()).reversed());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<RegGreeting> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public RegGreeting getRegGreeting(int no) {
        Optional<RegGreeting> regGreeting = this.regGreetingRepository.findById(no);
        if (regGreeting.isPresent()) {
            return regGreeting.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("Notice not found for no: " + no);
        }

    }

    @Transactional
    public void increaseViewCount(int no) {
        regGreetingRepository.increaseViewCount(no);
    }

    public boolean hasRegGreeting(int no) {
        Optional<RegGreeting> event = this.regGreetingRepository.findById(no);
        return event.isPresent();
    }

    public void addVote(RegGreeting regGreeting, CafeUser user) {
        regGreeting.getVoter().add(user);
        this.regGreetingRepository.save(regGreeting);
    }

    public void removeVote(RegGreeting regGreeting, CafeUser user) {
        regGreeting.getVoter().remove(user);
        this.regGreetingRepository.save(regGreeting);
    }

    public boolean isChecked(RegGreeting regGreeting, CafeUser user) {
        return regGreeting.getVoter().contains(user);

    }

    public void regGreetingmodify(RegGreeting regGreeting, String title, String content) {
        regGreeting.setTitle(title);
        regGreeting.setContent(content);
        regGreeting.setUpdate_date(LocalDateTime.now());
        this.regGreetingRepository.save(regGreeting);
    }

    public void regGreetingDelete(RegGreeting regGreeting) {
        this.regGreetingRepository.delete(regGreeting);
    }


    public void createRegGreetingComment(RegGreeting regGreeting, String content, CafeUser user) {
        RegComment regComment = new RegComment();
        regComment.setRegGreeting(regGreeting);
        regComment.setContent(content);
        regComment.setRegDate(LocalDateTime.now());
        regComment.setCafeUser(user);
        this.regCommentRepository.save(regComment);
    }

    public List<RegComment> getRegComments (int no) {
        return regCommentRepository.findAllById(no);
    }

    public List<RegComment> findAllByNoOrderByRegDateAsc(int no) {
        return regCommentRepository.findAllByNoOrderByRegDateAsc(no);
    }

    // 내림차순 정렬
    public List<RegComment> findAllByNoOrderByRegDateDesc(int no) {
        return regCommentRepository.findAllByNoOrderByRegDateDesc(no);
    }

    public RegComment getRegComment(int no) {
        Optional<RegComment> regComment = this.regCommentRepository.findById(no);
        if (regComment.isPresent()) {
            return regComment.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("Notice not found for no: " + no);
        }
    }

    public void regCommentModify(RegComment regComment, String content){
        regComment.setContent(content);
        regComment.setUpDate(LocalDateTime.now());
        this.regCommentRepository.save(regComment);
    }

    public void regCommentDelete(RegComment regComment){
        this.regCommentRepository.delete(regComment);
    }

}

