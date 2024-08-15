package com.cafe.cheezeHam.cafeAccuse;

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
public class AccuseService {

    private final AccuseRepository accuseRepository;
    private final AccuseCommentRepository accuseCommentRepository;

    public void create(String title, String content, String file, CafeUser user){
        Accuse accuse = new Accuse();

        accuse.setTitle(title);
        accuse.setContent(content);
        accuse.setType("신고");
        accuse.setReg_date(LocalDateTime.now());
        accuse.setAuthor(user);
        accuse.setFile_path(file);

        this.accuseRepository.save(accuse);

    }
    public int getMaxNo() {return this.accuseRepository.findMaxNo();}

    public Page<Accuse> getAccuses(int page, int pageSize, int field, String keyword){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("reg_date"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        if(field == 1){
            return this.accuseRepository.findTitleByKeyword(keyword,pageable);
        }else if(field == 2){
            return this.accuseRepository.findAuthorByKeyword(keyword, pageable);
        }else if(field == 3){
            return this.accuseRepository.findTitleAndContentByKeyword(keyword, pageable);
        }
        return this.accuseRepository.findAllByKeyword(keyword, pageable);
    }


    @Transactional
    public void increaseViewCount(int no) {

        accuseRepository.increaseViewCount(no);
    }

    public Accuse getAccuse(Integer no) {
        Optional<Accuse> accuse = this.accuseRepository.findByNo(no);
        if (accuse.isPresent()) {
            return accuse.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("Event not found for no: " + no);
        }
    }
    public void accuseModify(Accuse accuse, String title, String content,  String file){
        accuse.setContent(content);
        accuse.setTitle(title);
        accuse.setUpdate_date(LocalDateTime.now());
        accuse.setFile_path(file);
        this.accuseRepository.save(accuse);
    }
    public void accuseDelete(Accuse accuse){
        this.accuseRepository.delete(accuse);
    }

    public void createAccuseComment(Accuse accuse, String content, CafeUser user){
        AccuseComment accuseComment = new AccuseComment();
        accuseComment.setAccuse(accuse);
        accuseComment.setContent(content);
        accuseComment.setRegDate(LocalDateTime.now());
        accuseComment.setCafeUser(user);

        this.accuseCommentRepository.save(accuseComment);
    }

    public Page<Accuse> getAccusesAsc(int page, int pageSize, int field, String keyword) {
        List<Accuse> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.accuseRepository.findTitleByKeyword(keyword);
            case 2 -> this.accuseRepository.findAuthorByKeyword(keyword);
            case 3 -> this.accuseRepository.findTitleAndContentByKeyword(keyword);
            default -> this.accuseRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Accuse a) -> a.getVoter().size()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Accuse> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public Page<Accuse> getAccusesDesc(int page, int pageSize, int field, String keyword) {
        List<Accuse> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.accuseRepository.findTitleByKeyword(keyword);
            case 2 -> this.accuseRepository.findAuthorByKeyword(keyword);
            case 3 -> this.accuseRepository.findTitleAndContentByKeyword(keyword);
            default -> this.accuseRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Accuse a) -> a.getVoter().size()).reversed());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Accuse> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }


    public List<AccuseComment> getAccuseComments (int no) {
        return accuseCommentRepository.findAllById(no);
    }

    //오름차순 정렬
    public List<AccuseComment> findAllByNoOrderByRegDateAsc(int no) {
        return accuseCommentRepository.findAllByAccuseNoOrderByRegDateAsc(no);
    }

    // 내림차순 정렬
    public List<AccuseComment> findAllByNoOrderByRegDateDesc(int no) {
        return accuseCommentRepository.findAllByAccuseNoOrderByRegDateDesc(no);
    }

    public void accuseCommentModify(AccuseComment accuseComment, String content){
        accuseComment.setContent(content);
        accuseComment.setUpDate(LocalDateTime.now());
        this.accuseCommentRepository.save(accuseComment);
    }

    public void accuseCommentDelete(AccuseComment accuseComment) {
        this.accuseCommentRepository.delete(accuseComment);
    }


    public AccuseComment getAccuseComment(int no) {
        Optional<AccuseComment> accuseComment = this.accuseCommentRepository.findById(no);
        if (accuseComment.isPresent()) {
            return accuseComment.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("EventComment with no " + no + " not found");
        }
    }
    public void addVote(Accuse accuse, CafeUser user){
        accuse.getVoter().add(user);
        this.accuseRepository.save(accuse);
    }

    public void removeVote(Accuse accuse, CafeUser user){
        accuse.getVoter().remove(user);
        this.accuseRepository.save(accuse);
    }

    public boolean isChecked(Accuse accuse, CafeUser user){
        return accuse.getVoter().contains(user);

    }
    public boolean hasAccuse(int no){
        Optional<Accuse> accuse = this.accuseRepository.findById(no);
        return accuse.isPresent();
    }

}
