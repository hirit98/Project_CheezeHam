package com.cafe.cheezeHam.cafeQna;


import com.cafe.cheezeHam.cafeNotice.NoticeCommentRepository;
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
public class QnaService {

    private final QnaRepository qnaRepository;
    private final NoticeCommentRepository noticeCommentRepository;
    private final QnaCommentRepository qnaCommentRepository;

    public void create(String title, String content, String file, CafeUser user) {
        Qna qna = new Qna();
        qna.setTitle(title);
        qna.setContent(content);
        qna.setType("질문답변");
        qna.setReg_date(LocalDateTime.now());
        qna.setAuthor(user);
        qna.setFile_path(file);

        this.qnaRepository.save(qna);
    }

    public Page<Qna> getQna_s(int page, int pageSize, int field, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("reg_date"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        if(field == 1) {
            return this.qnaRepository.findTitleByKeyword(keyword, pageable);
        } else if (field == 2) {
            return this.qnaRepository.findAuthorByKeyword(keyword, pageable);
        } else if (field == 3) {
            return this.qnaRepository.findTitleAndContentByKeyword(keyword, pageable);
        }
        return this.qnaRepository.findAllByKeyword(keyword, pageable);
    }

    public Page<Qna> getQna_sAsc(int page, int pageSize, int field, String keyword) {
        List<Qna> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.qnaRepository.findTitleByKeyword(keyword);
            case 2 -> this.qnaRepository.findAuthorByKeyword(keyword);
            case 3 -> this.qnaRepository.findTitleAndContentByKeyword(keyword);
            default -> this.qnaRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Qna q) -> q.getVoter().size()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Qna> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public Page<Qna> getQna_sDesc(int page, int pageSize, int field, String keyword) {
        List<Qna> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.qnaRepository.findTitleByKeyword(keyword);
            case 2 -> this.qnaRepository.findAuthorByKeyword(keyword);
            case 3 -> this.qnaRepository.findTitleAndContentByKeyword(keyword);
            default -> this.qnaRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Qna q) -> q.getVoter().size()).reversed());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Qna> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    @Transactional
    public void increaseViewCount(int no) {
        qnaRepository.increaseViewCount(no);
    }

    public Qna getQna(int no) {
        Optional<Qna> qna = this.qnaRepository.findByNo(no);
        if(qna.isPresent()) {
            return qna.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("Notice not found for no: " + no);
        }
    }

    public boolean hasQna(int no) {
        Optional<Qna> notice = this.qnaRepository.findById(no);
        return notice.isPresent();
    }

        public void addVote(Qna qna, CafeUser user) {
            qna.getVoter().add(user);
        this.qnaRepository.save(qna);
    }

    public void removeVote(Qna qna, CafeUser user) {
        qna.getVoter().remove(user);
        this.qnaRepository.save(qna);
    }

    public boolean isChecked(Qna qna, CafeUser user) {
        return qna.getVoter().contains(user);
    }

    public int getMaxNo() {return this.qnaRepository.findMaxNo();}

    public void qnaModify(Qna qna, String title, String content, String file){
        qna.setContent(content);
        qna.setTitle(title);
        qna.setUpdate_date(LocalDateTime.now());
        qna.setFile_path(file);
        this.qnaRepository.save(qna);
    }

    public void qnaDelete(Qna qna){
        this.qnaRepository.delete(qna);
    }

    public void createQnaComment(Qna qna, String content, CafeUser user){
        QnaComment qnaComment = new QnaComment();
        qnaComment.setQna(qna);
        qnaComment.setContent(content);
        qnaComment.setRegDate(LocalDateTime.now());
        qnaComment.setCafeUser(user);
        this.qnaCommentRepository.save(qnaComment);
    }

    public QnaComment getQnaComment(int no) {
        Optional<QnaComment> qnaComment = this.qnaCommentRepository.findById(no);
        if (qnaComment.isPresent()) {
            return qnaComment.get();
        } else {
            throw new com.sbb.demo.DataNotFoundException("EventComment with no " + no + " not found");
        }
    }

    public List<QnaComment> getQnaComments (int no) {
        return qnaCommentRepository.findAllById(no);
    }

    public List<QnaComment> findAllByNoOrderByRegDateAsc(int no) {
        return qnaCommentRepository.findAllByNoOrderByRegDateAsc(no);
    }

    // 내림차순 정렬
    public List<QnaComment> findAllByNoOrderByRegDateDesc(int no) {
        return qnaCommentRepository.findAllByNoOrderByRegDateDesc(no);
    }

    public void qnaCommentModify(QnaComment qnaComment, String content){
        qnaComment.setContent(content);
        qnaComment.setUpDate(LocalDateTime.now());
        this.qnaCommentRepository.save(qnaComment);
    }

    public void qnaCommentDelete(QnaComment qnaComment) {
        this.qnaCommentRepository.delete(qnaComment);
    }

}
