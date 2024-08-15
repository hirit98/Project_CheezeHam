package com.cafe.cheezeHam.cafeHamBuy;

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

@Service
@RequiredArgsConstructor
public class HamBuyService {

    private final HamBuyRepository hamBuyRepository;
    private final HamBuyCommentRepository hamBuyCommentRepository;

    public void create(String title, String content, String file, CafeUser user){
        HamBuy hamBuy = new HamBuy();

        hamBuy.setTitle(title);
        hamBuy.setContent(content);
        hamBuy.setType("햄스터장터");
        hamBuy.setReg_date(LocalDateTime.now());
        hamBuy.setAuthor(user);
        hamBuy.setFile_path(file);

        this.hamBuyRepository.save(hamBuy);

    }

    public int getMaxNo() {return this.hamBuyRepository.findMaxNo();}

    public Page<HamBuy> getHamBuys(int page, int pageSize, int field, String keyword){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("reg_date"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        if(field == 1){
            return this.hamBuyRepository.findTitleByKeyword(keyword,pageable);
        }else if(field == 2){
            return this.hamBuyRepository.findAuthorByKeyword(keyword, pageable);
        }else if(field == 3){
            return this.hamBuyRepository.findTitleAndContentByKeyword(keyword, pageable);
        }
        return this.hamBuyRepository.findAllByKeyword(keyword, pageable);
    }


    @Transactional
    public void increaseViewCount(int no) {

        hamBuyRepository.increaseViewCount(no);
    }

    public HamBuy getHamBuy(Integer no) {
        Optional<HamBuy> hamBuy = this.hamBuyRepository.findByNo(no);
        if (hamBuy.isPresent()) {
            return hamBuy.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("Event not found for no: " + no);
        }
    }

    public void hamBuyModify(HamBuy hamBuy, String title, String content, String file){
        hamBuy.setContent(content);
        hamBuy.setTitle(title);
        hamBuy.setUpdate_date(LocalDateTime.now());
        hamBuy.setFile_path(file);
        this.hamBuyRepository.save(hamBuy);
    }
    public void hamBuyDelete(HamBuy hamBuy){
        this.hamBuyRepository.delete(hamBuy);
    }

    public void createHamBuyComment(HamBuy hamBuy, String content, CafeUser user){
        HamBuyComment hamBuyComment = new HamBuyComment();
        hamBuyComment.setHamBuy(hamBuy);
        hamBuyComment.setContent(content);
        hamBuyComment.setRegDate(LocalDateTime.now());
        hamBuyComment.setCafeUser(user);

        this.hamBuyCommentRepository.save(hamBuyComment);
    }

    public Page<HamBuy> getHamBuysAsc(int page, int pageSize, int field, String keyword) {
        List<HamBuy> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.hamBuyRepository.findTitleByKeyword(keyword);
            case 2 -> this.hamBuyRepository.findAuthorByKeyword(keyword);
            case 3 -> this.hamBuyRepository.findTitleAndContentByKeyword(keyword);
            default -> this.hamBuyRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((HamBuy h) -> h.getVoter().size()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<HamBuy> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public Page<HamBuy> getHamBuysDesc(int page, int pageSize, int field, String keyword) {
        List<HamBuy> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.hamBuyRepository.findTitleByKeyword(keyword);
            case 2 -> this.hamBuyRepository.findAuthorByKeyword(keyword);
            case 3 -> this.hamBuyRepository.findTitleAndContentByKeyword(keyword);
            default -> this.hamBuyRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((HamBuy h) -> h.getVoter().size()).reversed());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<HamBuy> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }



    //오름차순 정렬
    public List<HamBuyComment> findAllByNoOrderByRegDateAsc(int no) {
        return hamBuyCommentRepository.findAllByNoOrderByRegDateAsc(no);
    }

    // 내림차순 정렬
    public List<HamBuyComment> findAllByNoOrderByRegDateDesc(int no) {
        return hamBuyCommentRepository.findAllByNoOrderByRegDateDesc(no);
    }

    public void hamBuyCommentModify(HamBuyComment hamBuyComment, String content){
        hamBuyComment.setContent(content);
        hamBuyComment.setUpDate(LocalDateTime.now());
        this.hamBuyCommentRepository.save(hamBuyComment);
    }

    public void hamBuyCommentDelete(HamBuyComment hamBuyComment) {
        this.hamBuyCommentRepository.delete(hamBuyComment);
    }

    public List<HamBuyComment> getHamBuyComments (int no) {
        return hamBuyCommentRepository.findAllById(no);
    }

    public HamBuyComment getHamBuyComment(int no) {
        Optional<HamBuyComment> hamBuyComment = this.hamBuyCommentRepository.findById(no);
        if (hamBuyComment.isPresent()) {
            return hamBuyComment.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("EventComment with no " + no + " not found");
        }
    }

    public void addVote(HamBuy hamBuy, CafeUser user){
        hamBuy.getVoter().add(user);
        this.hamBuyRepository.save(hamBuy);
    }

    public void removeVote(HamBuy hamBuy, CafeUser user){
        hamBuy.getVoter().remove(user);
        this.hamBuyRepository.save(hamBuy);
    }

    public boolean isChecked(HamBuy hamBuy, CafeUser user){
        return hamBuy.getVoter().contains(user);

    }
    public boolean hasHamBuy(int no){
        Optional<HamBuy> hamBuy = this.hamBuyRepository.findById(no);
        return hamBuy.isPresent();
    }
}
