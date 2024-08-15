package com.cafe.cheezeHam.cafeSuggest;

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
public class SuggestService {

    private final SuggestRepository suggestRepository;
    private final SuggestCommentRepository suggestCommentRepository;

    public void create(String title, String content, String file, CafeUser user){
        Suggest suggest = new Suggest();

        suggest.setTitle(title);
        suggest.setContent(content);
        suggest.setType("건의");
        suggest.setReg_date(LocalDateTime.now());
        suggest.setAuthor(user);
        suggest.setFile_path(file);

        this.suggestRepository.save(suggest);

    }
    public int getMaxNo() {return this.suggestRepository.findMaxNo();}

    public Page<Suggest> getSuggests(int page, int pageSize, int field, String keyword){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("reg_date"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        if(field == 1){
            return this.suggestRepository.findTitleByKeyword(keyword,pageable);
        }else if(field == 2){
            return this.suggestRepository.findAuthorByKeyword(keyword, pageable);
        }else if(field == 3){
            return this.suggestRepository.findTitleAndContentByKeyword(keyword, pageable);
        }
        return this.suggestRepository.findAllByKeyword(keyword, pageable);
    }


    @Transactional
    public void increaseViewCount(int no) {

        suggestRepository.increaseViewCount(no);
    }

    public Suggest getSuggest(Integer no) {
        Optional<Suggest> suggest = this.suggestRepository.findByNo(no);
        if (suggest.isPresent()) {
            return suggest.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("Event not found for no: " + no);
        }
    }
    public void suggestModify(Suggest suggest, String title, String content, String file){
        suggest.setContent(content);
        suggest.setTitle(title);
        suggest.setUpdate_date(LocalDateTime.now());
        suggest.setFile_path(file);
        this.suggestRepository.save(suggest);
    }
    public void suggestDelete(Suggest suggest){
        this.suggestRepository.delete(suggest);
    }

    public void createSuggestComment(Suggest suggest, String content, CafeUser user){
        SuggestComment suggestComment = new SuggestComment();
        suggestComment.setSuggest(suggest);
        suggestComment.setContent(content);
        suggestComment.setRegDate(LocalDateTime.now());
        suggestComment.setCafeUser(user);

        this.suggestCommentRepository.save(suggestComment);
    }

    public Page<Suggest> getSuggestsAsc(int page, int pageSize, int field, String keyword) {
        List<Suggest> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.suggestRepository.findTitleByKeyword(keyword);
            case 2 -> this.suggestRepository.findAuthorByKeyword(keyword);
            case 3 -> this.suggestRepository.findTitleAndContentByKeyword(keyword);
            default -> this.suggestRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Suggest s) -> s.getVoter().size()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Suggest> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public Page<Suggest> getSuggestsDesc(int page, int pageSize, int field, String keyword) {
        List<Suggest> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.suggestRepository.findTitleByKeyword(keyword);
            case 2 -> this.suggestRepository.findAuthorByKeyword(keyword);
            case 3 -> this.suggestRepository.findTitleAndContentByKeyword(keyword);
            default -> this.suggestRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Suggest s) -> s.getVoter().size()).reversed());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Suggest> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }


    public List<SuggestComment> getSuggestComments (int no) {
        return suggestCommentRepository.findAllById(no);
    }

    //오름차순 정렬
    public List<SuggestComment> findAllByNoOrderByRegDateAsc(int no) {
        return suggestCommentRepository.findAllBySuggestNoOrderByRegDateAsc(no);
    }

    // 내림차순 정렬
    public List<SuggestComment> findAllByNoOrderByRegDateDesc(int no) {
        return suggestCommentRepository.findAllBySuggestNoOrderByRegDateDesc(no);
    }

    public void suggestCommentModify(SuggestComment suggestComment, String content){
        suggestComment.setContent(content);
        suggestComment.setUpDate(LocalDateTime.now());
        this.suggestCommentRepository.save(suggestComment);
    }

    public void suggestCommentDelete(SuggestComment suggestComment) {
        this.suggestCommentRepository.delete(suggestComment);
    }


    public SuggestComment getSuggestComment(int no) {
        Optional<SuggestComment> suggestComment = this.suggestCommentRepository.findById(no);
        if (suggestComment.isPresent()) {
            return suggestComment.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("EventComment with no " + no + " not found");
        }
    }
    public void addVote(Suggest suggest, CafeUser user){
        suggest.getVoter().add(user);
        this.suggestRepository.save(suggest);
    }

    public void removeVote(Suggest suggest, CafeUser user){
        suggest.getVoter().remove(user);
        this.suggestRepository.save(suggest);
    }

    public boolean isChecked(Suggest suggest, CafeUser user){
        return suggest.getVoter().contains(user);

    }
    public boolean hasSuggest(int no){
        Optional<Suggest> accuse = this.suggestRepository.findById(no);
        return accuse.isPresent();
    }

}
