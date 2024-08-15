package com.cafe.cheezeHam.cafeCatBuy;

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

@RequiredArgsConstructor
@Service
public class CatBuyService {

    private final CatBuyRepository catBuyRepository;
    private final CatBuyCommentRepository catBuyCommentRepository;

    public void create(String title, String content, String file, CafeUser user){
        CatBuy catBuy = new CatBuy();

        catBuy.setTitle(title);
        catBuy.setContent(content);
        catBuy.setType("고양이장터");
        catBuy.setReg_date(LocalDateTime.now());
        catBuy.setFile_path(file);
        catBuy.setAuthor(user);

        this.catBuyRepository.save(catBuy);

    }
    public int getMaxNo() {return this.catBuyRepository.findMaxNo();}

    public Page<CatBuy> getCatBuys(int page, int pageSize, int field, String keyword){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("reg_date"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        if(field == 1){
            return this.catBuyRepository.findTitleByKeyword(keyword,pageable);
        }else if(field == 2){
            return this.catBuyRepository.findAuthorByKeyword(keyword, pageable);
        }else if(field == 3){
            return this.catBuyRepository.findTitleAndContentByKeyword(keyword, pageable);
        }
        return this.catBuyRepository.findAllByKeyword(keyword, pageable);
    }


    @Transactional
    public void increaseViewCount(int no) {

        catBuyRepository.increaseViewCount(no);
    }

    public CatBuy getCatBuy(Integer no) {
        Optional<CatBuy> catBuy = this.catBuyRepository.findByNo(no);
        if (catBuy.isPresent()) {
            return catBuy.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("Event not found for no: " + no);
        }
    }
    public void catBuyModify(CatBuy catBuy, String title, String content, String file){
        catBuy.setContent(content);
        catBuy.setTitle(title);
        catBuy.setUpdate_date(LocalDateTime.now());
        catBuy.setFile_path(file);

        this.catBuyRepository.save(catBuy);
    }
    public void catBuyDelete(CatBuy catBuy){
        this.catBuyRepository.delete(catBuy);
    }

    public void createCatBuyComment(CatBuy catBuy, String content, CafeUser user){
        CatBuyComment catBuyComment = new CatBuyComment();
        catBuyComment.setCatBuy(catBuy);
        catBuyComment.setContent(content);
        catBuyComment.setRegDate(LocalDateTime.now());
        catBuyComment.setCafeUser(user);

        this.catBuyCommentRepository.save(catBuyComment);
    }

    public Page<CatBuy> getCatBuysAsc(int page, int pageSize, int field, String keyword) {
        List<CatBuy> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.catBuyRepository.findTitleByKeyword(keyword);
            case 2 -> this.catBuyRepository.findAuthorByKeyword(keyword);
            case 3 -> this.catBuyRepository.findTitleAndContentByKeyword(keyword);
            default -> this.catBuyRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((CatBuy c) -> c.getVoter().size()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<CatBuy> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public Page<CatBuy> getCatBuysDesc(int page, int pageSize, int field, String keyword) {
        List<CatBuy> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.catBuyRepository.findTitleByKeyword(keyword);
            case 2 -> this.catBuyRepository.findAuthorByKeyword(keyword);
            case 3 -> this.catBuyRepository.findTitleAndContentByKeyword(keyword);
            default -> this.catBuyRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((CatBuy c) -> c.getVoter().size()).reversed());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<CatBuy> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }


    public List<CatBuyComment> getCatBuyComments (int no) {
        return catBuyCommentRepository.findAllById(no);
    }

    //오름차순 정렬
    public List<CatBuyComment> findAllByNoOrderByRegDateAsc(int no) {
        return catBuyCommentRepository.findAllByNoOrderByRegDateAsc(no);
    }

    // 내림차순 정렬
    public List<CatBuyComment> findAllByNoOrderByRegDateDesc(int no) {
        return catBuyCommentRepository.findAllByNoOrderByRegDateDesc(no);
    }

    public void catBuyCommentModify(CatBuyComment catBuyComment, String content){
        catBuyComment.setContent(content);
        catBuyComment.setUpDate(LocalDateTime.now());
        this.catBuyCommentRepository.save(catBuyComment);
    }

    public void catBuyCommentDelete(CatBuyComment catBuyComment) {
        this.catBuyCommentRepository.delete(catBuyComment);
    }


    public CatBuyComment getCatBuyComment(int no) {
        Optional<CatBuyComment> catBuyComment = this.catBuyCommentRepository.findById(no);
        if (catBuyComment.isPresent()) {
            return catBuyComment.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("EventComment with no " + no + " not found");
        }
    }
    public void addVote(CatBuy catBuy, CafeUser user){
        catBuy.getVoter().add(user);
        this.catBuyRepository.save(catBuy);
    }

    public void removeVote(CatBuy catBuy, CafeUser user){
        catBuy.getVoter().remove(user);
        this.catBuyRepository.save(catBuy);
    }

    public boolean isChecked(CatBuy catBuy, CafeUser user){
        return catBuy.getVoter().contains(user);

    }
    public boolean hasCatBuy(int no){
        Optional<CatBuy> catBuy = this.catBuyRepository.findById(no);
        return catBuy.isPresent();
    }

}
