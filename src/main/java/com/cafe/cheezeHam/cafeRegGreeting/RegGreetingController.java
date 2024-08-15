package com.cafe.cheezeHam.cafeRegGreeting;

import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeNotice.NoticeRepository;
import com.cafe.cheezeHam.cafeQna.QnaComment;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import com.cafe.cheezeHam.cafeUser.CafeUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reg_greeting")
public class RegGreetingController {

    private final NoticeRepository noticeRepository;
    private final CafeUserService cafeUserService;
    private final RegGreetingService regGreetingService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String regGreetingCreate(RegGreetingForm regGreetingForm, Model model) {return "cafeRegGreeting/regGreeting_form";}

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String regGreetingCreate(@Valid RegGreetingForm regGreetingForm, BindingResult bindingResult, Principal principal) throws IOException {
        if (bindingResult.hasErrors()) {
            return "cafeRegGreeting/regGreeting_form";
        }
        CafeUser user = this.cafeUserService.getUser(principal.getName());


        this.regGreetingService.create(regGreetingForm.getTitle(), regGreetingForm.getContent(), user);
        return "redirect:/reg_greeting/list";
    }

    @GetMapping("/list")
    public String regGreetingList(Model model, @RequestParam(value ="page" , defaultValue = "0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                  @RequestParam(value = "pageSize", defaultValue = "15") int pageSize, @RequestParam(value = "hiddenNotice", defaultValue = "false") boolean hiddenNotice, @RequestParam(value = "field", defaultValue = "0") int field
            , @RequestParam(value = "sort_value", defaultValue = "") String sort_value) {

        List<Notice> importantNotice = noticeRepository.findImportantNoticeList();
        Page<RegGreeting> paging = null;

        if (sort_value.equals("asc")) {
            paging = this.regGreetingService.getRegGreetingAsc(page, pageSize, field, keyword);
        } else if (sort_value.equals("desc")) {
            paging = this.regGreetingService.getRegGreetingDesc(page, pageSize, field, keyword);
        } else {
            paging = this.regGreetingService.getRegGreetings(page, pageSize, field, keyword);
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

        return "cafeRegGreeting/regGreeting_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("detail/{no}")
    public String detail (Model model, @RequestParam(value = "sort", defaultValue = "") String sort, @PathVariable("no") Integer no, Principal principal){
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        RegGreeting regGreeting = this.regGreetingService.getRegGreeting(no);

        if (regGreeting != null) {
            boolean hasPrevious = this.regGreetingService.hasRegGreeting(no - 1);
            boolean hasNext = this.regGreetingService.hasRegGreeting(no + 1);
            boolean isChecked = this.regGreetingService.isChecked(regGreeting, user);

            if (sort.equals("asc")) {
                List<RegComment> commentList = this.regGreetingService.findAllByNoOrderByRegDateAsc(no);
                model.addAttribute("commentList", commentList);
            } else if (sort.equals("desc")) {
                List<RegComment> commentList = this.regGreetingService.findAllByNoOrderByRegDateDesc(no);
                model.addAttribute("commentList", commentList);
            } else {
                this.regGreetingService.increaseViewCount(no);
                List<RegComment> commentList = this.regGreetingService.getRegComments(no);
                model.addAttribute("commentList", commentList);
            }

        model.addAttribute("hasPrevious", hasPrevious);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("isChecked", isChecked);
        model.addAttribute("regGreeting", regGreeting);
        return "cafeRegGreeting/reg_greeting_detail";
      }
        return "redirect:/reg_greeting/list";
    }



    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/modify/{no}")
    public String regGreetingModify(RegGreetingForm regGreetingForm, @PathVariable("no") int no, Principal principal){

        RegGreeting regGreeting = this.regGreetingService.getRegGreeting(no);
        if(regGreeting != null){
            if(!regGreeting.getAuthor().getId().equals(principal.getName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 게시글이 없습니다.");
        }

        regGreetingForm.setContent(regGreeting.getContent());
        regGreetingForm.setTitle(regGreeting.getTitle());
        return "/cafeRegGreeting/regGreeting_modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{no}")
    public String regGreetingModify(@Valid RegGreetingForm regGreetingForm, Principal principal, @PathVariable("no") int no) {

        RegGreeting regGreeting = this.regGreetingService.getRegGreeting(no);

        if (regGreeting != null) {
            if (!regGreeting.getAuthor().getId().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 게시글이 없습니다.");
        }
        this.regGreetingService.regGreetingmodify(regGreeting , regGreetingForm.getTitle(), regGreetingForm.getContent());
        return String.format("redirect:/reg_greeting/detail/%s", no);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{no}")
    public String regGreetingDelete(Principal principal, @PathVariable("no") int no) {
        RegGreeting regGreeting = this.regGreetingService.getRegGreeting(no);
        if(regGreeting != null) {
            if(!regGreeting.getAuthor().getId().equals(principal.getName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 게시글이 없습니다.");
        }
        this.regGreetingService.regGreetingDelete(regGreeting);
        return "redirect:/reg_greeting/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/add/{no}")
    public String addVote(@PathVariable("no") Integer no, Principal principal) {
        RegGreeting regGreeting = this.regGreetingService.getRegGreeting(no);
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        this.regGreetingService.addVote(regGreeting, user);

        return String.format("redirect:/reg_greeting/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/remove/{no}")
    public String removeVote(@PathVariable("no") Integer no, Principal principal) {
        RegGreeting regGreeting = this.regGreetingService.getRegGreeting(no);
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        this.regGreetingService.removeVote(regGreeting, user);

        return String.format("redirect:/reg_greeting/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/create/{no}")
    public String createComment(@PathVariable("no") Integer no, @RequestParam(value = "content") String content, Principal principal){
        CafeUser user = this.cafeUserService.getUser(principal.getName());

        RegGreeting regGreeting = this.regGreetingService.getRegGreeting(no);
        this.regGreetingService.createRegGreetingComment(regGreeting, content, user);

        return String.format("redirect:/reg_greeting/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/modify/{no}")
    public String regCommentModify(RegCommentForm regCommentForm, @PathVariable("no") int no, Principal principal){
        RegComment regComment = this.regGreetingService.getRegComment(no);
        if (!regComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        regCommentForm.setContent(regComment.getContent());
        return "/cafeRegGreeting/regComment_modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/modify/{no}")
    public String regCommentModify(@Valid RegCommentForm regCommentForm, @PathVariable("no") Integer no, Principal principal) {
        RegComment regComment = this.regGreetingService.getRegComment(no);
        if (!regComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.regGreetingService.regCommentModify(regComment, regCommentForm.getContent());
        return String.format("redirect:/reg_greeting/detail/%s", regComment.getRegGreeting().getNo());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/delete/{no}")
    public String regCommentDelete(@PathVariable("no") int no, Principal principal) {
        RegComment regComment = this.regGreetingService.getRegComment(no);
        if (!regComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.regGreetingService.regCommentDelete(regComment);
        return String.format("redirect:/reg_greeting/detail/%s", regComment.getRegGreeting().getNo());
    }

}
