package com.cafe.cheezeHam.cafeEvent;

import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import com.sbb.demo.DataNotFoundException;
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
public class EventService {

    private  final EventCommentRepository eventCommentRepository;
    private final EventRepository eventRepository;

    public void create(String title, String content,  String file, CafeUser user) throws IOException {
        Event event = new Event();

        event.setTitle(title);
        event.setContent(content);
        event.setReg_date(LocalDateTime.now());
        event.setType("이벤트");
        event.setAuthor(user);
        event.setFile_path(file);

        this.eventRepository.save(event);
    }

    public Page<Event> getEvents(int page, int pageSize, int field, String keyword){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("reg_date"));
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
        if(field == 1){
            return this.eventRepository.findTitleByKeyword(keyword,pageable);
        }else if(field == 2){
            return this.eventRepository.findAuthorByKeyword(keyword, pageable);
        }else if(field == 3){
            return this.eventRepository.findTitleAndContentByKeyword(keyword, pageable);
        }
        return this.eventRepository.findAllByKeyword(keyword, pageable);
    }

    @Transactional
    public void increaseViewCount(int no) {

        eventRepository.increaseViewCount(no);
    }

    public Event getEvent(Integer no) {
        Optional<Event> event = this.eventRepository.findByNo(no);
        if (event.isPresent()) {
            return event.get();
        } else {
            throw new DataNotFoundException("Event not found for no: " + no);
        }
    }

    public Page<Event> getEventsAsc(int page, int pageSize, int field, String keyword) {
        List<Event> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.eventRepository.findTitleByKeyword(keyword);
            case 2 -> this.eventRepository.findAuthorByKeyword(keyword);
            case 3 -> this.eventRepository.findTitleAndContentByKeyword(keyword);
            default -> this.eventRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Event b) -> b.getVoter().size()));

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Event> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public Page<Event> getEventsDesc(int page, int pageSize, int field, String keyword) {
        List<Event> lists = null;
        Pageable pageable = PageRequest.of(page, pageSize);
        lists = switch (field) {
            case 1 -> this.eventRepository.findTitleByKeyword(keyword);
            case 2 -> this.eventRepository.findAuthorByKeyword(keyword);
            case 3 -> this.eventRepository.findTitleAndContentByKeyword(keyword);
            default -> this.eventRepository.findAllByKeyword(keyword);
        };
        lists.sort(Comparator.comparingInt((Event b) -> b.getVoter().size()).reversed());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());

        List<Event> paging = lists.subList(start, end);

        // 새로운 PageImpl 객체 반환
        return new PageImpl<>(paging, pageable, lists.size());
    }

    public void createEventComment(Event event, String content, CafeUser user){
        EventComment eventComment = new EventComment();
        eventComment.setEvent(event);
        eventComment.setContent(content);
        eventComment.setRegDate(LocalDateTime.now());
        eventComment.setCafeUser(user);

        this.eventCommentRepository.save(eventComment);
    }

    public void eventModify(Event event, String title, String content, String file){
        event.setContent(content);
        event.setTitle(title);
        event.setUpdate_date(LocalDateTime.now());
        event.setFile_path(file);
        this.eventRepository.save(event);
    }

    public void eventDelete(Event event){
        this.eventRepository.delete(event);
    }


    public EventComment getEventComment(int no) {
        Optional<EventComment> eventComment = this.eventCommentRepository.findById(no);
        if (eventComment.isPresent()) {
            return eventComment.get();
        } else {
            throw new DataNotFoundException("EventComment with no " + no + " not found");
        }
    }

    public List<EventComment> getEventComments (int no) {
        return eventCommentRepository.findAllById(no);
    }

    public List<EventComment> findAllByEventNoOrderByRegDateAsc(int no) {
        return eventCommentRepository.findAllByEventNoOrderByRegDateAsc(no);
    }

    // 내림차순 정렬
    public List<EventComment> findAllByEventNoOrderByRegDateDesc(int no) {
        return eventCommentRepository.findAllByEventNoOrderByRegDateDesc(no);
    }

    public void eventCommentModify(EventComment eventComment, String content){
        eventComment.setContent(content);
        eventComment.setUpDate(LocalDateTime.now());
        this.eventCommentRepository.save(eventComment);
    }

    public void eventCommentDelete(EventComment eventComment) {
        this.eventCommentRepository.delete(eventComment);
    }

    public boolean hasEvent(int no){
        Optional<Event> event = this.eventRepository.findById(no);
        return event.isPresent();
    }

    public void addVote(Event event, CafeUser user){
        event.getVoter().add(user);
        this.eventRepository.save(event);
    }

    public void removeVote(Event event, CafeUser user){
        event.getVoter().remove(user);
        this.eventRepository.save(event);
    }

    public boolean isChecked(Event event, CafeUser user){
        return event.getVoter().contains(user);

    }

    public int getMaxNo() {return this.eventRepository.findMaxNo();}
}
