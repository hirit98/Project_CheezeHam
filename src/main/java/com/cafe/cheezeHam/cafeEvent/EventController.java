package com.cafe.cheezeHam.cafeEvent;

import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeNotice.NoticeRepository;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import com.cafe.cheezeHam.cafeUser.CafeUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {

    private final EventRepository eventRepository;

    private final EventCommentRepository eventCommentRepository;

    private final NoticeRepository noticeRepository;

    private final EventService eventService;

    private final CafeUserService cafeUserService;

    @Value("${upload.dir}/event/")
    private String uploadDir;

    private void createUploadDir() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String eventCreate(EventForm eventForm, Model model) {return "cafeEvent/event_form";}

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String eventCreate(@Valid EventForm eventForm, BindingResult bindingResult, @RequestParam("files") List<MultipartFile> files, Principal principal) throws IOException {
        if (bindingResult.hasErrors()) {
            return "cafeEvent/event_form";
        }
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        String file_path = null;

        if(files != null && !files.isEmpty()){
            try {
                createUploadDir();

                int max_no = this.eventService.getMaxNo() + 1;
                File path = new File(uploadDir + max_no);
                boolean fileUploaded = false;

                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        if (!path.exists()) {
                            path.mkdirs();
                        }
                        String fileName = file.getOriginalFilename();
                        Path filePath = Paths.get(path.getAbsolutePath(), fileName);
                        Files.write(filePath, file.getBytes());
                        fileUploaded = true;

                    }
                }if (fileUploaded) {
                    file_path = path.getAbsolutePath();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        this.eventService.create(eventForm.getTitle(), eventForm.getContent(), file_path, user);
        return "redirect:/event/list";
    }

    @GetMapping("/list")
    public String eventList(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                            @RequestParam(value = "pageSize", defaultValue = "15") int pageSize, @RequestParam(value = "hiddenNotice", defaultValue = "false") boolean hiddenNotice, @RequestParam(value = "field", defaultValue = "0") int field
            ,@RequestParam(value = "sort_value", defaultValue = "") String sort_value) {

        List<Notice> importantNotice = noticeRepository.findImportantNoticeList();
        Page<Event> paging = null;


        if (sort_value.equals("asc")) {
            paging = this.eventService.getEventsAsc(page, pageSize, field, keyword);
        } else if (sort_value.equals("desc")) {
            paging = this.eventService.getEventsDesc(page, pageSize, field, keyword);
        } else {
            paging = this.eventService.getEvents(page, pageSize, field, keyword);
        }

        int block = 10;
        int currentPage = paging.getNumber() + 1;
        int totalPage = paging.getTotalPages();

        int startBlock = (((currentPage - 1) / block) * block) + 1;
        int endBlock = startBlock + block - 1;
        if(endBlock > totalPage) {
            endBlock = totalPage;
        }
        model.addAttribute("field", field);
        model.addAttribute("keyword", keyword);
        model.addAttribute("hiddenNotice", hiddenNotice);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("paging", paging);
        model.addAttribute("importantNotice", importantNotice);
        model.addAttribute("startBlock", startBlock);
        model.addAttribute("endBlock", endBlock);
        model.addAttribute("sort_value", sort_value);

        return "cafeEvent/event_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/detail/{no}")
    public String detail(Model model, @PathVariable("no") Integer no, Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        Event event = this.eventService.getEvent(no);

        List<EventComment> commentList = this.eventService.getEventComments(no);

        boolean hasPrevious = false;
        boolean hasNext = false;
        boolean isChecked = false;

        // 이벤트 조회 및 조회수 증가
        if (event != null) {
            this.eventService.increaseViewCount(no);
            hasPrevious = this.eventService.hasEvent(no - 1);
            hasNext = this.eventService.hasEvent(no + 1);
            isChecked = this.eventService.isChecked(event, user);
        }

        if(event.getFile_path() !=null && !event.getFile_path().isEmpty()){
            File path = new File(event.getFile_path());
            File[] files = path.listFiles();
            List<String> fileNames = new ArrayList<>();
            if(files != null){
                for(File file : files){
                    fileNames.add(file.getName());
                }
            }
            model.addAttribute("files", fileNames);

        }

        model.addAttribute("hasPrevious", hasPrevious);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("isChecked", isChecked);
        model.addAttribute("event", event);
        model.addAttribute("commentList", commentList);
        return "cafeEvent/event_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("filename") String filename, @RequestParam("no") int no) {
        try {
            String encodedFilename = UriUtils.encode(filename, StandardCharsets.UTF_8);

            Path path = Paths.get(uploadDir + no + "/" + filename);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
                        .body(resource);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/comment/create/{no}")
    public String createComment(@PathVariable("no") Integer no,@RequestParam(value="content") String content, Principal principal){
        CafeUser user = this.cafeUserService.getUser(principal.getName());

        Event event = this.eventService.getEvent(no);
        this.eventService.createEventComment(event, content, user);

        return String.format("redirect:/event/detail/%s", no);
    }

    @GetMapping("/Comment_ASC_DESC/{no}")
    public String getEventComment_ASC_DESC(@PathVariable("no") int no, @RequestParam(value = "sort", defaultValue = "desc") String sort, Model model, Principal principal) {
        List<EventComment> commentList;

        CafeUser user = this.cafeUserService.getUser(principal.getName());
        Event event = this.eventService.getEvent(no);

        if (event != null) {
            this.eventService.increaseViewCount(no);
        }

        if ("asc".equals(sort)) {
            commentList = eventService.findAllByEventNoOrderByRegDateAsc(no);
        } else {
            commentList = eventService.findAllByEventNoOrderByRegDateDesc(no);
        }

        boolean hasPrevious = this.eventService.hasEvent(no - 1);
        boolean hasNext = this.eventService.hasEvent(no + 1);
        boolean isChecked = this.eventService.isChecked(event, user);

        model.addAttribute("hasPrevious", hasPrevious);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("isChecked", isChecked);
        model.addAttribute("event", event);
        model.addAttribute("commentList", commentList);
        return "cafeEvent/event_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/modify/{no}")
    public String eventModify(EventForm eventForm, @PathVariable("no") int no, Principal principal, Model model){
        Event event = this.eventService.getEvent(no);
        if(event != null) {
            if(!event.getAuthor().getId().equals(principal.getName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 게시글이 없습니다.");
        }

        model.addAttribute("event", event);
        eventForm.setContent(event.getContent());
        eventForm.setTitle(event.getTitle());
        return "/cafeEvent/event_modify_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/modify/{no}")
    public String eventModify(@Valid EventForm eventForm, Principal principal, @PathVariable("no") int no, @RequestParam("files") List<MultipartFile> files){
        Event event = this.eventService.getEvent(no);

        if(event != null) {
            if(!event.getAuthor().getId().equals(principal.getName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            }
            File path = new File(uploadDir  + no);
            String file_path = null;

            if(files != null && !files.isEmpty()) {
                try {
                    createUploadDir();
                    boolean fileUploaded = false;

                    for(MultipartFile file : files) {
                        if (!file.isEmpty()) {
                            if (!path.exists()) {
                                path.mkdirs();
                            }
                            String fileName = file.getOriginalFilename();
                            Path filePath = Paths.get(path.getAbsolutePath(), fileName);
                            Files.write(filePath, file.getBytes());
                            fileUploaded = true;
                        }
                    }
                    if (fileUploaded) {
                        file_path = path.getAbsolutePath();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            File[] fileslist = path.listFiles();
            if(fileslist != null && fileslist.length > 0) {
                file_path = path.getAbsolutePath();
            } else {
                if(path.exists()) {
                    path.delete();
                }
            }
            this.eventService.eventModify(event, eventForm.getTitle(), eventForm.getContent(), file_path);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 게시글이 없습니다.");
        }
        return String.format("redirect:/event/detail/%s", no);
    }

    @GetMapping("/file/findList/{no}")
    @ResponseBody
    public List<String> eventFindList(@PathVariable("no") int no) {
        Event event = this.eventService.getEvent(no);
        List<String> fileNames = new ArrayList<>();
        if (event.getFile_path() != null && !event.getFile_path().isEmpty()) {
            File path = new File(event.getFile_path());
            File[] files = path.listFiles();
            if(files != null) {
                for(File file : files) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }

    @GetMapping("/file/remove")
    public ResponseEntity<String> fileRemove(@RequestParam("filename") String filename , @RequestParam("no") int no, Principal principal) {
        Event event = this.eventService.getEvent(no);
        if(event != null) {
            File path = new File(event.getFile_path() + "/" + filename);
            if (path.exists() && path.delete()) {
                return ResponseEntity.ok("업로드한 파일을 삭제했습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 삭제에 실패했습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{no}")
    public String eventDelete(Principal principal, @PathVariable("no") int no) {
        Event event = this.eventService.getEvent(no);
        if(event != null) {
            if(!event.getAuthor().getId().equals(principal.getName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 게시글이 없습니다.");
        }
        this.eventService.eventDelete(event);
        return "redirect:/event/list";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/modify/{no}")
    public String eventCommentModify(EventCommentForm eventCommentForm, @PathVariable("no") int no, Principal principal) {
        EventComment eventComment = this.eventService.getEventComment(no);
        if (!eventComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        eventCommentForm.setContent(eventComment.getContent());
        return "/cafeEvent/reply_modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/modify/{no}")
    public String eventCommentModify(@Valid EventCommentForm eventCommentForm, @PathVariable("no") Integer no, Principal principal) {
        EventComment eventComment = this.eventService.getEventComment(no);
        if (!eventComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.eventService.eventCommentModify(eventComment, eventCommentForm.getContent());
        return String.format("redirect:/event/detail/%s", eventComment.getEvent().getNo());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/delete/{no}")
    public String eventCommentDelete(Principal principal, @PathVariable("no") int no) {
        EventComment eventComment = this.eventService.getEventComment(no);
        if (!eventComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.eventService.eventCommentDelete(eventComment);
        return String.format("redirect:/event/detail/%s", eventComment.getEvent().getNo());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/add/{no}")
    public String addVote(@PathVariable("no") Integer no, Principal principal) {
        Event event = this.eventService.getEvent(no);
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        this.eventService.addVote(event, user);

        return String.format("redirect:/event/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/remove/{no}")
    public String removeVote(@PathVariable("no") Integer no, Principal principal) {
        Event event = this.eventService.getEvent(no);
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        this.eventService.removeVote(event, user);

        return String.format("redirect:/event/detail/%s", no);
    }
}
