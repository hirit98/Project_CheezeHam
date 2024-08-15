package com.cafe.cheezeHam.cafeNotice;

import com.cafe.cheezeHam.cafeUser.CafeUser;
import com.sbb.demo.DataNotFoundException;
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
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeCommentRepository noticeCommentRepository;

    public void create(String title, String content, String file, CafeUser user) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setType("공지");
        notice.setReg_date(LocalDateTime.now());
        notice.setAuthor(user);
        notice.setFile_path(file);

        this.noticeRepository.save(notice);
    }

    public void createImportant(String title, String content, String file, CafeUser user) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setType("중요");
        notice.setReg_date(LocalDateTime.now());
        notice.setAuthor(user);
        notice.setFile_path(file);

        this.noticeRepository.save(notice);
    }

    public Page<Notice> getNotices(int page, int pageSize, int field, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("reg_date"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        if(field == 1) {
            return this.noticeRepository.findTitleByKeyword(keyword, pageable);
        } else if (field == 2) {
            return this.noticeRepository.findAuthorByKeyword(keyword, pageable);
        } else if (field == 3) {
            return this.noticeRepository.findTitleAndContentByKeyword(keyword, pageable);
        }
        return this.noticeRepository.findAllByKeyword(keyword, pageable);
    }

    public Page<Notice> getNoticesAsc(int page, int pageSize, int field, String keyword) {
        List<Notice> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.noticeRepository.findTitleByKeyword(keyword);
            case 2 -> this.noticeRepository.findAuthorByKeyword(keyword);
            case 3 -> this.noticeRepository.findTitleAndContentByKeyword(keyword);
            default -> this.noticeRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Notice n) -> n.getVoter().size()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Notice> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public Page<Notice> getNoticesDesc(int page, int pageSize, int field, String keyword) {
        List<Notice> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.noticeRepository.findTitleByKeyword(keyword);
            case 2 -> this.noticeRepository.findAuthorByKeyword(keyword);
            case 3 -> this.noticeRepository.findTitleAndContentByKeyword(keyword);
            default -> this.noticeRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Notice n) -> n.getVoter().size()).reversed());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Notice> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public List<Notice> getImportantNotices() {
        List<Notice> notices = this.noticeRepository.findImportantNoticeList();
        return notices;
    }

    @Transactional
    public void increaseViewCount(int no) {
        noticeRepository.increaseViewCount(no);
    }

    public Notice getNotice(int no) {
        Optional<Notice> notice = this.noticeRepository.findById(no);
        if(notice.isPresent()) {
            return notice.get();
        } else {
            throw new DataNotFoundException("Notice not found for no: " + no);
        }
    }

    public void createNoticeComment(Notice notice, String content, CafeUser user) {
        NoticeComment noticeComment = new NoticeComment();
        noticeComment.setContent(content);
        noticeComment.setNotice(notice);
        noticeComment.setRegDate(LocalDateTime.now());
        noticeComment.setCafeUser(user);

        this.noticeCommentRepository.save(noticeComment);
    }

    public List<NoticeComment> getNoticeComments(int no) { return this.noticeCommentRepository.findAllByNo(no);}

    public List<NoticeComment> findAllByNoOrderByRegDateDesc(int no) { return this.noticeCommentRepository.findAllByRegNoOrderByRegDateDesc(no);}

    public List<NoticeComment> findAllByNoOrderByRegDateAsc(int no) { return this.noticeCommentRepository.findAllByRegNoOrderByRegDateAsc(no);}

    public void noticeModify(Notice notice, String title, String content, String file) {
        notice.setTitle(title);
        notice.setContent(content);
        notice.setUpdate_date(LocalDateTime.now());
        notice.setFile_path(file);
        this.noticeRepository.save(notice);
    }

    public void noticeDelete(Notice notice) {this.noticeRepository.delete(notice);}

    public NoticeComment getNoticeComment(int no) {
        Optional<NoticeComment> noticeComment = this.noticeCommentRepository.findById(no);
        if(noticeComment.isPresent()) {
            return noticeComment.get();
        } else {
            throw new DataNotFoundException("NoticeComment not found for no: " + no);
        }
    }

    public void noticeCommentModify(NoticeComment noticeComment, String content) {
        noticeComment.setContent(content);
        noticeComment.setUpDate(LocalDateTime.now());
        this.noticeCommentRepository.save(noticeComment);
    }

    public void noticeCommentDelete(NoticeComment noticeComment) {this.noticeCommentRepository.delete(noticeComment);}

    public boolean hasNotice(int no) {
        return this.noticeRepository.findById(no).isPresent();
    }

    public void addVote(Notice notice, CafeUser user) {
        notice.getVoter().add(user);
        this.noticeRepository.save(notice);
    }

    public void removeVote(Notice notice, CafeUser user) {
        notice.getVoter().remove(user);
        this.noticeRepository.save(notice);
    }

    public boolean isChecked(Notice notice, CafeUser user) {
        return notice.getVoter().contains(user);
    }

    public int getMaxNo() {return this.noticeRepository.findMaxNo();}

    public int getNoticeRuleNo() {
        Optional<Notice> notice = this.noticeRepository.findByTitle();
        if (notice.isPresent()) {
            return notice.get().getNo();
        } else {
            throw new DataNotFoundException("Notice not found for title: ");
        }
    }
}
