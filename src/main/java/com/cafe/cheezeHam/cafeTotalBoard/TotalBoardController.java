package com.cafe.cheezeHam.cafeTotalBoard;

import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeNotice.NoticeService;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import com.cafe.cheezeHam.cafeUser.CafeUserService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/total")
public class TotalBoardController {

    private final NoticeService noticeService;
    private final TotalBoardService totalBoardService;
    private final CafeUserService cafeUserService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                       @RequestParam(value = "pageSize", defaultValue = "15") int pageSize, @RequestParam(value = "hiddenNotice", defaultValue = "false") boolean hiddenNotice, @RequestParam(value = "field", defaultValue = "0") int field) {
        List<Notice> importantNotice = this.noticeService.getImportantNotices();
        Page<TotalBoard> totalList = this.totalBoardService.searchTotalBoard(page, pageSize, field, keyword);

        int block = 10;
        int currentPage = totalList.getNumber() + 1;
        int totalPage = totalList.getTotalPages();
        int startBlock = (((currentPage - 1) / block) * block) + 1;
        int endBlock = startBlock + block - 1;
        if (endBlock > totalPage) {
            endBlock = totalPage;
        }

        model.addAttribute("field", field);
        model.addAttribute("keyword", keyword);
        model.addAttribute("hiddenNotice", hiddenNotice);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalList", totalList);
        model.addAttribute("importantNotice", importantNotice);
        model.addAttribute("startBlock", startBlock);
        model.addAttribute("endBlock", endBlock);

        return "cafeTotal/total_list";
    }

    @GetMapping("/count")
    @ResponseBody
    public String getCount() {
        long totalCount = this.totalBoardService.getTotalCount();

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

        return nf.format(totalCount);
    }

    @GetMapping("/userCnt")
    @ResponseBody
    public String getUserCnt() {
        long userCount = this.totalBoardService.getTotalUser();
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        return nf.format(userCount);
    }

    @GetMapping("/userPostCnt")
    @ResponseBody
    public String getAuthorPostCnt(Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());

        long userPostCount = this.totalBoardService.getAuthorTotalPostCount(user.getId());
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        return nf.format(userPostCount);
    }

    @GetMapping("/userCommentCnt")
    @ResponseBody
    public String getAuthorCommentCnt(Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());

        long userCommentCount = this.totalBoardService.getAuthorTotalCommentCount(user.getId());
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        return nf.format(userCommentCount);
    }

    @GetMapping("/userInfo")
    @ResponseBody
    public UserInfo userInfo(Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        UserInfo info = new UserInfo();
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        long userCommentCount = this.totalBoardService.getAuthorTotalCommentCount(user.getId());
        long userPostCount = this.totalBoardService.getAuthorTotalPostCount(user.getId());
        LocalDate userReg = LocalDate.from(user.getRegdate());

        info.setPostCnt(nf.format(userPostCount));
        info.setCommentCnt(nf.format(userCommentCount));
        info.setImages(user.getProfile());
        info.setRegdate(userReg.toString());

        return info;
    }

    @Getter
    @Setter
    public static class UserInfo {
        private String postCnt;
        private String commentCnt;
        private String images;
        private String regdate;
    }
}
