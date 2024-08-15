package com.cafe.cheezeHam.cafeTotalBoard;

import com.cafe.cheezeHam.cafeAccuse.Accuse;
import com.cafe.cheezeHam.cafeAccuse.AccuseCommentRepository;
import com.cafe.cheezeHam.cafeAccuse.AccuseRepository;
import com.cafe.cheezeHam.cafeBoast.Boast;
import com.cafe.cheezeHam.cafeBoast.BoastCommentRepository;
import com.cafe.cheezeHam.cafeBoast.BoastRepository;
import com.cafe.cheezeHam.cafeCatBuy.CatBuy;
import com.cafe.cheezeHam.cafeCatBuy.CatBuyCommentRepository;
import com.cafe.cheezeHam.cafeCatBuy.CatBuyRepository;
import com.cafe.cheezeHam.cafeEvent.Event;
import com.cafe.cheezeHam.cafeEvent.EventCommentRepository;
import com.cafe.cheezeHam.cafeEvent.EventRepository;
import com.cafe.cheezeHam.cafeFree.Free;
import com.cafe.cheezeHam.cafeFree.FreeCommentRepository;
import com.cafe.cheezeHam.cafeFree.FreeRepository;
import com.cafe.cheezeHam.cafeHamBuy.HamBuy;
import com.cafe.cheezeHam.cafeHamBuy.HamBuyCommentRepository;
import com.cafe.cheezeHam.cafeHamBuy.HamBuyRepository;
import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeNotice.NoticeCommentRepository;
import com.cafe.cheezeHam.cafeNotice.NoticeRepository;
import com.cafe.cheezeHam.cafeQna.Qna;
import com.cafe.cheezeHam.cafeQna.QnaCommentRepository;
import com.cafe.cheezeHam.cafeQna.QnaRepository;
import com.cafe.cheezeHam.cafeRegGreeting.RegCommentRepository;
import com.cafe.cheezeHam.cafeRegGreeting.RegGreeting;
import com.cafe.cheezeHam.cafeRegGreeting.RegGreetingRepository;
import com.cafe.cheezeHam.cafeSuggest.Suggest;
import com.cafe.cheezeHam.cafeSuggest.SuggestCommentRepository;
import com.cafe.cheezeHam.cafeSuggest.SuggestRepository;
import com.cafe.cheezeHam.cafeUser.CafeUserRepository;
import com.cafe.cheezeHam.cafeUser.CafeUserService;
import com.sun.tools.jconsole.JConsoleContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class TotalBoardService {
    private final CafeUserRepository cafeUserRepository;
    private final NoticeRepository noticeRepository;
    private final EventRepository eventRepository;
    private final BoastRepository boastRepository;
    private final RegGreetingRepository regGreetingRepository;
    private final FreeRepository freeRepository;
    private final QnaRepository qnaRepository;
    private final AccuseRepository accuseRepository;
    private final SuggestRepository suggestRepository;
    private final CatBuyRepository catBuyRepository;
    private final HamBuyRepository hamBuyRepository;

    private final NoticeCommentRepository noticeCommentRepository;
    private final EventCommentRepository eventCommentRepository;
    private final BoastCommentRepository boastCommentRepository;
    private final RegCommentRepository regCommentRepository;
    private final FreeCommentRepository freeCommentRepository;
    private final QnaCommentRepository qnaCommentRepository;
    private final AccuseCommentRepository accuseCommentRepository;
    private final SuggestCommentRepository suggestCommentRepository;
    private final CatBuyCommentRepository catBuyCommentRepository;
    private final HamBuyCommentRepository hamBuyCommentRepository;

    // 게시글 종류 추가시 레포지토리 추가해야함.

    public Page<TotalBoard> searchTotalBoard(int page, int pageSize, int field, String keyword) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc("reg_date")));

        // Keyword 검색 필드에 따른 쿼리 선택
        List<Notice> notices = fetchNotices(field, keyword);
        List<Event> events = fetchEvents(field, keyword);
        List<Boast> boasts = fetchBoasts(field, keyword);
        List<RegGreeting> greetings = fetchRegGreetings(field, keyword);
        List<Free> frees = fetchFrees(field, keyword);
        List<Qna> qnas = fetchQnas(field, keyword);
        List<Accuse> accuses = fetchAccuses(field, keyword);
        List<Suggest> suggests = fetchSuggests(field, keyword);
        List<HamBuy> hamBuys = fetchHamBuys(field, keyword);
        List<CatBuy> catBuys = fetchCatBuys(field, keyword);

        List<TotalBoard> totalBoards = new ArrayList<>();

        if (!notices.isEmpty()) {
            totalBoards.addAll(notices);
        }
        if (!events.isEmpty()) {
            totalBoards.addAll(events);
        }
        if (!boasts.isEmpty()) {
            totalBoards.addAll(boasts);
        }
        if (!greetings.isEmpty()) {
            totalBoards.addAll(greetings);
        }
        if (!frees.isEmpty()) {
            totalBoards.addAll(frees);
        }
        if (!qnas.isEmpty()) {
            totalBoards.addAll(qnas);
        }
        if (!accuses.isEmpty()) {
            totalBoards.addAll(accuses);
        }
        if (!suggests.isEmpty()) {
            totalBoards.addAll(suggests);
        }
        if (!hamBuys.isEmpty()) {
            totalBoards.addAll(hamBuys);
        }
        if (!catBuys.isEmpty()) {
            totalBoards.addAll(catBuys);
        }

        List<TotalBoard> combined = totalBoards.stream()
                .sorted(Comparator.comparing(TotalBoard::getRegDate).reversed())
                .toList();

        // 페이지에 맞는 서브리스트 생성
        int start = Math.min(page * pageSize, combined.size());
        int end = Math.min(start + pageSize, combined.size());
        List<TotalBoard> pagedContent = combined.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(pagedContent, pageable, combined.size());
    }

    private List<Notice> fetchNotices(int field, String keyword) {
        switch (field) {
            case 1: return noticeRepository.findTitleByKeyword(keyword);
            case 2: return noticeRepository.findAuthorByKeyword(keyword);
            case 3: return noticeRepository.findTitleAndContentByKeyword(keyword);
            default: return noticeRepository.findAllByKeyword(keyword);
        }
    }

    private List<Event> fetchEvents(int field, String keyword) {
        switch (field) {
            case 1: return eventRepository.findTitleByKeyword(keyword);
            case 2: return eventRepository.findAuthorByKeyword(keyword);
            case 3: return eventRepository.findTitleAndContentByKeyword(keyword);
            default: return eventRepository.findAllByKeyword(keyword);
        }
    }

    private List<Boast> fetchBoasts(int field, String keyword) {
        switch (field) {
            case 1: return boastRepository.findTitleByKeyword(keyword);
            case 2: return boastRepository.findAuthorByKeyword(keyword);
            case 3: return boastRepository.findTitleAndContentByKeyword(keyword);
            default: return boastRepository.findAllByKeyword(keyword);
        }
    }

    private List<RegGreeting> fetchRegGreetings(int field, String keyword) {
        switch (field) {
            case 1: return regGreetingRepository.findTitleByKeyword(keyword);
            case 2: return regGreetingRepository.findAuthorByKeyword(keyword);
            case 3: return regGreetingRepository.findTitleAndContentByKeyword(keyword);
            default: return regGreetingRepository.findAllByKeyword(keyword);
        }
    }

    private List<Free> fetchFrees(int field, String keyword) {
        switch (field) {
            case 1: return freeRepository.findTitleByKeyword(keyword);
            case 2: return freeRepository.findAuthorByKeyword(keyword);
            case 3: return freeRepository.findTitleAndContentByKeyword(keyword);
            default: return freeRepository.findAllByKeyword(keyword);
        }
    }

    private List<Accuse> fetchAccuses(int field, String keyword) {
        switch (field) {
            case 1: return accuseRepository.findTitleByKeyword(keyword);
            case 2: return accuseRepository.findAuthorByKeyword(keyword);
            case 3: return accuseRepository.findTitleAndContentByKeyword(keyword);
            default: return accuseRepository.findAllByKeyword(keyword);
        }
    }
    private List<Suggest> fetchSuggests(int field, String keyword) {
        switch (field) {
            case 1: return suggestRepository.findTitleByKeyword(keyword);
            case 2: return suggestRepository.findAuthorByKeyword(keyword);
            case 3: return suggestRepository.findTitleAndContentByKeyword(keyword);
            default: return suggestRepository.findAllByKeyword(keyword);
        }
    }
    private List<CatBuy> fetchCatBuys(int field, String keyword) {
        switch (field) {
            case 1: return catBuyRepository.findTitleByKeyword(keyword);
            case 2: return catBuyRepository.findAuthorByKeyword(keyword);
            case 3: return catBuyRepository.findTitleAndContentByKeyword(keyword);
            default: return catBuyRepository.findAllByKeyword(keyword);
        }
    }
    private List<HamBuy> fetchHamBuys(int field, String keyword) {
        switch (field) {
            case 1: return hamBuyRepository.findTitleByKeyword(keyword);
            case 2: return hamBuyRepository.findAuthorByKeyword(keyword);
            case 3: return hamBuyRepository.findTitleAndContentByKeyword(keyword);
            default: return hamBuyRepository.findAllByKeyword(keyword);
        }
    }
    private List<Qna> fetchQnas(int field, String keyword) {
        switch (field) {
            case 1: return qnaRepository.findTitleByKeyword(keyword);
            case 2: return qnaRepository.findAuthorByKeyword(keyword);
            case 3: return qnaRepository.findTitleAndContentByKeyword(keyword);
            default: return qnaRepository.findAllByKeyword(keyword);
        }
    }

    public long getTotalCount() {
        long totalCount = 0;

        totalCount = noticeRepository.count() + eventRepository.count() + boastRepository.count() +
                    regGreetingRepository.count() + freeRepository.count() + qnaRepository.count() +
                    suggestRepository.count() + accuseRepository.count() + hamBuyRepository.count() +
                    catBuyRepository.count();

        return totalCount;
    }

    public long getAuthorTotalPostCount(String id){

        long noticeCount = noticeRepository.countByAuthorId(id);
        long eventCount = eventRepository.countByAuthorId(id);
        long boastCount = boastRepository.countByAuthorId(id);
        long regGreetingCount = regGreetingRepository.countByAuthorId(id);
        long freeCount = freeRepository.countByAuthorId(id);
        long qnaCount = qnaRepository.countByAuthorId(id);
        long accuseCount = accuseRepository.countByAuthorId(id);
        long suggestCount = suggestRepository.countByAuthorId(id);
        long catBuyCount = catBuyRepository.countByAuthorId(id);
        long hamBuyCount = hamBuyRepository.countByAuthorId(id);

        return noticeCount + eventCount + boastCount + regGreetingCount +
                freeCount + qnaCount + accuseCount + suggestCount +
                catBuyCount + hamBuyCount;
    }

    public long getAuthorTotalCommentCount(String id){
        long noticeCount = noticeCommentRepository.countByAuthorId(id);
        long eventCount = eventCommentRepository.countByAuthorId(id);
        long boastCount = boastCommentRepository.countByAuthorId(id);
        long regGreetingCount = regCommentRepository.countByAuthorId(id);
        long freeCount = freeCommentRepository.countByAuthorId(id);
        long qnaCount = qnaCommentRepository.countByAuthorId(id);
        long accuseCount = accuseCommentRepository.countByAuthorId(id);
        long suggestCount = suggestCommentRepository.countByAuthorId(id);
        long catBuyCount = catBuyCommentRepository.countByAuthorId(id);
        long hamBuyCount = hamBuyCommentRepository.countByAuthorId(id);

        return noticeCount + eventCount + boastCount + regGreetingCount +
                freeCount + qnaCount + accuseCount + suggestCount +
                catBuyCount + hamBuyCount;
    }

    public long getTotalUser() {
        long totalUser = 0;
        totalUser = this.cafeUserRepository.count();
        return totalUser;
    }

    public List<TotalBoard> getRecentTotalBoards() {
        List<TotalBoard> totalBoards = new ArrayList<>();

        List<Notice> notices = this.noticeRepository.findRecentNotice();
        List<Event> events = this.eventRepository.findRecentEvent();
        List<Boast> boasts = this.boastRepository.findRecentBoast();
        List<RegGreeting> greetings = this.regGreetingRepository.findRecentRegGreetings();
        List<Free> frees = this.freeRepository.findRecentFrees();
        List<Qna> qnas = this.qnaRepository.findRecentQnas();
        List<Accuse> accuses = this.accuseRepository.findRecentAccuses();
        List<Suggest> suggests = this.suggestRepository.findRecentSuggests();
        List<CatBuy> catBuys = this.catBuyRepository.findRecentCatBuys();
        List<HamBuy> hamBuys = this.hamBuyRepository.findRecentHamBuys();

        if (!notices.isEmpty()) {
            totalBoards.addAll(notices);
        }
        if (!events.isEmpty()) {
            totalBoards.addAll(events);
        }
        if (!boasts.isEmpty()) {
            totalBoards.addAll(boasts);
        }
        if (!greetings.isEmpty()) {
            totalBoards.addAll(greetings);
        }
        if (!frees.isEmpty()) {
            totalBoards.addAll(frees);
        }
        if (!qnas.isEmpty()) {
            totalBoards.addAll(qnas);
        }
        if (!accuses.isEmpty()) {
            totalBoards.addAll(accuses);
        }
        if (!catBuys.isEmpty()) {
            totalBoards.addAll(catBuys);
        }
        if (!hamBuys.isEmpty()) {
            totalBoards.addAll(hamBuys);
        }
        if (!suggests.isEmpty()) {
            totalBoards.addAll(suggests);
        }


        List<TotalBoard> recentTotals = totalBoards.stream()
                .sorted(Comparator.comparing(TotalBoard::getRegDate).reversed())
                .limit(5)
                .toList();

        return recentTotals;
    }
}
