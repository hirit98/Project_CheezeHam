package com.cafe.cheezeHam.cafeBoast;

import com.cafe.cheezeHam.cafeEvent.Event;
import com.cafe.cheezeHam.cafeEvent.EventComment;
import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoastService {

    private final BoastRepository boastRepository;
    private final BoastCommentRepository boastCommentRepository;

    public void create(String title, String content, String type, String file, CafeUser user){
        Boast boast = new Boast();

        boast.setTitle(title);
        boast.setContent(content);
        boast.setType(type);
        boast.setReg_date(LocalDateTime.now());
        boast.setFile_path(file);
        boast.setAuthor(user);

        this.boastRepository.save(boast);

    }

    public Page<Boast> getBoasts(int page, int pageSize, int field, String keyword){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("reg_date"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        if(field == 1){
            return this.boastRepository.findTitleByKeyword(keyword,pageable);
        }else if(field == 2){
            return this.boastRepository.findAuthorByKeyword(keyword, pageable);
        }else if(field == 3){
            return this.boastRepository.findTitleAndContentByKeyword(keyword, pageable);
        }
        return this.boastRepository.findAllByKeyword(keyword, pageable);
    }

    public List<Boast> getboastList() {
        return this.boastRepository.findRecentBoast();
    }

    @Transactional
    public void increaseViewCount(int no) {boastRepository.increaseViewCount(no);}

    public Boast getBoast(Integer no) {
        Optional<Boast> boast = this.boastRepository.findByNo(no);
        if (boast.isPresent()) {
            return boast.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("Event not found for no: " + no);
        }
    }
    public void boastModify(Boast boast, String title, String content, String file){
        boast.setContent(content);
        boast.setTitle(title);
        boast.setUpdate_date(LocalDateTime.now());
        boast.setFile_path(file);
        this.boastRepository.save(boast);
    }
    public void boastDelete(Boast boast){
        this.boastRepository.delete(boast);
    }

    public void createBoastComment(Boast boast, String content, CafeUser user){
        BoastComment BoastComment = new BoastComment();
        BoastComment.setBoast(boast);
        BoastComment.setContent(content);
        BoastComment.setRegDate(LocalDateTime.now());
        BoastComment.setCafeUser(user);

        this.boastCommentRepository.save(BoastComment);
    }

    public Page<Boast> getBoastsAsc(int page, int pageSize, int field, String keyword, String type) {
        List<Boast> lists;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.boastRepository.findTitleByKeyword(keyword);
            case 2 -> this.boastRepository.findAuthorByKeyword(keyword);
            case 3 -> this.boastRepository.findTitleAndContentByKeyword(keyword);
            default -> this.boastRepository.findAllByKeyword(keyword);
        };
        if (type.equals("전체")) {
            lists.sort(Comparator.comparingInt((Boast b) -> b.getVoter().size()));
        } else {
            lists = lists.stream()
                    .filter(b -> type.equals(b.getType()))
                    .sorted(Comparator.comparingInt((Boast b) -> b.getVoter().size()))
                    .collect(Collectors.toList());
        }

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Boast> paging = lists.subList(start, end);

        return new PageImpl<>(paging, pageable, lists.size());
    }

    public Page<Boast> getBoastsDesc(int page, int pageSize, int field, String keyword, String type) {
        List<Boast> lists;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.boastRepository.findTitleByKeyword(keyword);
            case 2 -> this.boastRepository.findAuthorByKeyword(keyword);
            case 3 -> this.boastRepository.findTitleAndContentByKeyword(keyword);
            default -> this.boastRepository.findAllByKeyword(keyword);
        };
        if (type.equals("전체")) {
            lists.sort(Comparator.comparingInt((Boast b) -> b.getVoter().size()).reversed());
        } else {
            lists = lists.stream()
                    .filter(b -> type.equals(b.getType()))
                    .sorted(Comparator.comparingInt((Boast b) -> b.getVoter().size()).reversed())
                    .collect(Collectors.toList());
        }

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Boast> paging = lists.subList(start, end);

        return new PageImpl<>(paging, pageable, lists.size());
    }
    /*public Page<Boast> getPagedBoastsByTypeCat(int page, int pageSize, int field, String keyword) {
        List<Boast> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);

        // 데이터 조회
        switch (field) {
            case 1 -> lists = this.boastRepository.findTitleByKeyword(keyword);
            case 2 -> lists = this.boastRepository.findAuthorByKeyword(keyword);
            case 3 -> lists = this.boastRepository.findTitleAndContentByKeyword(keyword);
            default -> lists = this.boastRepository.findAllByKeyword(keyword);
        }

        // '고양이' 타입만 필터링
        lists = lists.stream()
                .filter(b -> "고양이".equals(b.getType()))
                .collect(Collectors.toList());

        // 페이징 처리
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Boast> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public Page<Boast> getPagedBoastsByTypeHam(int page, int pageSize, int field, String keyword) {
        List<Boast> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);

        // 데이터 조회
        switch (field) {
            case 1 -> lists = this.boastRepository.findTitleByKeyword(keyword);
            case 2 -> lists = this.boastRepository.findAuthorByKeyword(keyword);
            case 3 -> lists = this.boastRepository.findTitleAndContentByKeyword(keyword);
            default -> lists = this.boastRepository.findAllByKeyword(keyword);
        }

        // '고양이' 타입만 필터링
        lists = lists.stream()
                .filter(b -> "햄스터".equals(b.getType()))
                .collect(Collectors.toList());

        // 페이징 처리
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Boast> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }*/


    public List<BoastComment> getBoastComments (int no) {
        return boastCommentRepository.findAllById(no);
    }

    //오름차순 정렬
    public List<BoastComment> findAllByNoOrderByRegDateAsc(int no) {
        return boastCommentRepository.findAllByNoOrderByRegDateAsc(no);
    }

    // 내림차순 정렬
    public List<BoastComment> findAllByNoOrderByRegDateDesc(int no) {
        return boastCommentRepository.findAllByNoOrderByRegDateDesc(no);
    }

    public void boastCommentModify(BoastComment boastComment, String content){
        boastComment.setContent(content);
        boastComment.setUpDate(LocalDateTime.now());
        this.boastCommentRepository.save(boastComment);
    }

    public void boastCommentDelete(BoastComment boastComment) {
        this.boastCommentRepository.delete(boastComment);
    }


    public BoastComment getBoastComment(int no) {
        Optional<BoastComment> boastComment = this.boastCommentRepository.findById(no);
        if (boastComment.isPresent()) {
            return boastComment.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("EventComment with no " + no + " not found");
        }
    }
    public void addVote(Boast boast, CafeUser user){
        boast.getVoter().add(user);
        this.boastRepository.save(boast);
    }

    public void removeVote(Boast boast, CafeUser user){
        boast.getVoter().remove(user);
        this.boastRepository.save(boast);
    }

    public boolean isChecked(Boast boast, CafeUser user){
        return boast.getVoter().contains(user);

    }
    public boolean hasBoast(int no){
        Optional<Boast> boast = this.boastRepository.findById(no);
        return boast.isPresent();
    }

    public int getMaxNo() {return this.boastRepository.findMaxNo();}

}
