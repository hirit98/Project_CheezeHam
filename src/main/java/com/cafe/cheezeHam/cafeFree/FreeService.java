package com.cafe.cheezeHam.cafeFree;

import com.cafe.cheezeHam.cafeFree.Free;
import com.cafe.cheezeHam.cafeNotice.Notice;
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
public class FreeService {
    private final FreeRepository freeRepository;
    private final FreeCommentRepository freeCommentRepository;

    public void create(String title, String content, String file, CafeUser user) {
        Free free = new Free();
        free.setTitle(title);
        free.setContent(content);
        free.setType("자유");
        free.setReg_date(LocalDateTime.now());
        free.setAuthor(user);
        free.setFile_path(file);

        this.freeRepository.save(free);
    }

    public Page<Free> getFreeBoards(int page, int pageSize, int field, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("reg_date"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        if(field == 1) {
            return this.freeRepository.findTitleByKeyword(keyword, pageable);
        } else if (field == 2) {
            return this.freeRepository.findAuthorByKeyword(keyword, pageable);
        } else if (field == 3) {
            return this.freeRepository.findTitleAndContentByKeyword(keyword, pageable);
        }
        return this.freeRepository.findAllByKeyword(keyword, pageable);
    }

    public Page<Free> getFreesAsc(int page, int pageSize, int field, String keyword) {
        List<Free> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.freeRepository.findTitleByKeyword(keyword);
            case 2 -> this.freeRepository.findAuthorByKeyword(keyword);
            case 3 -> this.freeRepository.findTitleAndContentByKeyword(keyword);
            default -> this.freeRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Free n) -> n.getVoter().size()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Free> paging = lists.subList(start, end);

        return new PageImpl<>(paging, pageable, lists.size());
    }

    public Page<Free> getFreesDesc(int page, int pageSize, int field, String keyword) {
        List<Free> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.freeRepository.findTitleByKeyword(keyword);
            case 2 -> this.freeRepository.findAuthorByKeyword(keyword);
            case 3 -> this.freeRepository.findTitleAndContentByKeyword(keyword);
            default -> this.freeRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Free n) -> n.getVoter().size()).reversed());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Free> paging = lists.subList(start, end);

        return new PageImpl<>(paging, pageable, lists.size());
    }

    @Transactional
    public void increaseViewCount(int no) { freeRepository.increaseViewCount(no);}

    public Free getFree(int no) {
        Optional<Free> free = this.freeRepository.findById(no);
        if(free.isPresent()) {
            return free.get();
        } else {
            throw new DataNotFoundException("FreeBoard not found for no: " + no);
        }
    }

    public void createFreeComment(Free free, String content, CafeUser user) {
        FreeComment freeComment = new FreeComment();
        freeComment.setContent(content);
        freeComment.setFree(free);
        freeComment.setRegDate(LocalDateTime.now());
        freeComment.setCafeUser(user);

        this.freeCommentRepository.save(freeComment);
    }

    public List<FreeComment> getFreeComments(int no) { return this.freeCommentRepository.findAllByNo(no); }

    public List<FreeComment> findAllByNoOrderByRegDateDesc(int no) {
        return this.freeCommentRepository.findAllByRegNoOrderByRegDateDesc(no);
    }

    public List<FreeComment> findAllByNoOrderByRegDateAsc(int no) {
        return this.freeCommentRepository.findAllByRegNoOrderByRegDateAsc(no);
    }

    public void freeModify(Free free, String title, String content, String file) {
        free.setTitle(title);
        free.setContent(content);
        free.setFile_path(file);
        free.setUpdate_date(LocalDateTime.now());
        this.freeRepository.save(free);
    }

    public void freeDelete(Free free) { this.freeRepository.delete(free); }

    public FreeComment getFreeComment(int no) {
        Optional<FreeComment> freeComment = this.freeCommentRepository.findById(no);
        if(freeComment.isPresent()) {
            return freeComment.get();
        } else {
            throw new DataNotFoundException("FreeComment not found for no: " + no);
        }
    }

    public void freeCommentModify(FreeComment freeComment, String content) {
        freeComment.setContent(content);
        freeComment.setUpDate(LocalDateTime.now());
        this.freeCommentRepository.save(freeComment);
    }

    public void freeCommentDelete(FreeComment freeComment) { this.freeCommentRepository.delete(freeComment); }

    public boolean hasFree(int no) {
        return this.freeRepository.findById(no).isPresent();
    }

    public void addVote(Free free, CafeUser user) {
        free.getVoter().add(user);
        this.freeRepository.save(free);
    }

    public void removeVote(Free free, CafeUser user) {
        free.getVoter().remove(user);
        this.freeRepository.save(free);
    }

    public boolean isChecked(Free free, CafeUser user) { return free.getVoter().contains(user); }

    public int getMaxNo() { return this.freeRepository.findMaxNo(); }
}
