package com.cafe.cheezeHam.cafeNotice;

import com.cafe.cheezeHam.FileData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import com.cafe.cheezeHam.cafeUser.CafeUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final CafeUserService cafeUserService;

    @Value("${upload.dir}/notice/")
    private String uploadDir;

    private void createUploadDir() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String noticeCreate(NoticeForm noticeForm) {return "cafeNotice/notice_form";}

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String noticeCreate(@Valid NoticeForm noticeForm, BindingResult bindingResult, @RequestParam("files") List<MultipartFile> files, @RequestParam(value = "important", required = false) String important, Principal principal) {
        if(bindingResult.hasErrors()) {
            return "cafeNotice/notice_form";
        }
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        String file_path = null;

        if(files != null && !files.isEmpty()) {
            try {
                createUploadDir();

                int max_no = this.noticeService.getMaxNo() + 1;
                File path = new File(uploadDir  + max_no);
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
        if (important != null && important.equals("on")) {
            this.noticeService.createImportant(noticeForm.getTitle(), noticeForm.getContent(), file_path, user);
        } else {
            this.noticeService.create(noticeForm.getTitle(), noticeForm.getContent(), file_path, user);
        }
        return "redirect:/notice/list";
    }

    @GetMapping("/list")
    public String noticeList(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                             @RequestParam(value = "pageSize", defaultValue = "15") int pageSize, @RequestParam(value = "hiddenNotice", defaultValue = "false") boolean hiddenNotice,
                             @RequestParam(value = "field", defaultValue = "0") int field, @RequestParam(value = "sort_value", defaultValue = "") String sort_value) {
        List<Notice> importantNotice = this.noticeService.getImportantNotices();

        Page<Notice> paging;

        if (sort_value.equals("asc")) {
            paging = this.noticeService.getNoticesAsc(page, pageSize, field, keyword);
        } else if (sort_value.equals("desc")) {
            paging = this.noticeService.getNoticesDesc(page, pageSize, field, keyword);
        } else {
            paging = this.noticeService.getNotices(page, pageSize, field, keyword);
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

        return "cafeNotice/notice_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{no}")
    public String noticeDetail(@PathVariable("no") int no, @RequestParam(value = "sort", defaultValue = "") String sort, Model model, Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        Notice notice = this.noticeService.getNotice(no);

        if (notice != null) {
            boolean hasPrevious = this.noticeService.hasNotice(no - 1);
            boolean hasNext = this.noticeService.hasNotice(no + 1);
            boolean isChecked = this.noticeService.isChecked(notice, user);

            if (sort.equals("asc")) {
                List<NoticeComment> commentList = this.noticeService.findAllByNoOrderByRegDateAsc(no);
                model.addAttribute("commentList", commentList);
            } else if (sort.equals("desc")) {
                List<NoticeComment> commentList = this.noticeService.findAllByNoOrderByRegDateDesc(no);
                model.addAttribute("commentList", commentList);
            } else {
                this.noticeService.increaseViewCount(no);
                List<NoticeComment> commentList = this.noticeService.getNoticeComments(no);
                model.addAttribute("commentList", commentList);
            }

            // 업로드 파일 List 가져오기
            if (notice.getFile_path() != null && !notice.getFile_path().isEmpty()) {
            /*File path = new File(notice.getFile_path());
            File[] files = path.listFiles();
            List<String> fileNames = new ArrayList<>();
            if(files != null) {
                for(File file : files) {
                    fileNames.add(file.getName());
                }
            }*/
                File path = new File(notice.getFile_path());
                File[] files = path.listFiles();
                List<FileData> fileNames = new ArrayList<>();
                if (files != null) {
                    for (File file : files) {
                        try {
                            String encodedFilename = URLEncoder.encode(file.getName(), "UTF-8").replace("+", "%20");
                            String decodedFilename = URLDecoder.decode(encodedFilename, "UTF-8");
                            fileNames.add(new FileData(encodedFilename, decodedFilename));
                        } catch (IOException e) {
                            throw new RuntimeException("URL Encoding/Decoding failed!", e);
                        }
                    }
                }
                model.addAttribute("files", fileNames);
            }
            model.addAttribute("hasPrevious", hasPrevious);
            model.addAttribute("hasNext", hasNext);
            model.addAttribute("notice", notice);
            model.addAttribute("isChecked", isChecked);

            return "/cafeNotice/notice_detail";
        }
        return "redirect:/notice/list";
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
    @PostMapping("/comment/create/{no}")
    public String createComment(@PathVariable("no") int no, @RequestParam(value = "content") String content, Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        Notice notice = this.noticeService.getNotice(no);
        this.noticeService.createNoticeComment(notice, content, user);

        return String.format("redirect:/notice/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{no}")
    public String noticeModify(NoticeForm noticeForm, Model model, Principal principal, @PathVariable("no") int no) {
        Notice notice = this.noticeService.getNotice(no);
        if(notice != null) {
            if(!notice.getAuthor().getId().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 게시글이 없습니다.");
        }

        model.addAttribute("notice", notice);
        noticeForm.setTitle(notice.getTitle());
        noticeForm.setContent(notice.getContent());

        return "/cafeNotice/notice_modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{no}")
    public String noticeModify(@Valid NoticeForm noticeForm, @PathVariable("no") int no, Principal principal, @RequestParam("files") List<MultipartFile> files) {
        Notice notice = this.noticeService.getNotice(no);
        if(notice != null) {
            if(!notice.getAuthor().getId().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다..");
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
            this.noticeService.noticeModify(notice, noticeForm.getTitle(), noticeForm.getContent(), file_path);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 게시글이 존재하지 않습니다.");
        }
        return String.format("redirect:/notice/detail/%s", no);
    }

    @GetMapping("/file/findList/{no}")
    @ResponseBody
    public List<FileData> noticeFindList(@PathVariable("no") int no) {
        Notice notice = this.noticeService.getNotice(no);
        File path = new File(notice.getFile_path());
        File[] files = path.listFiles();
        List<FileData> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                try {
                    String encodedFilename = URLEncoder.encode(file.getName(), "UTF-8").replace("+", "%20");
                    String decodedFilename = URLDecoder.decode(encodedFilename, "UTF-8");
                    fileNames.add(new FileData(encodedFilename, decodedFilename));
                } catch (IOException e) {
                    throw new RuntimeException("URL Encoding/Decoding failed!", e);
                }
            }
        }
        return fileNames;
    }

    @GetMapping("/file/remove")
    public ResponseEntity<String> fileRemove(@RequestParam("filename") String filename , @RequestParam("no") int no, Principal principal) {
        Notice notice = this.noticeService.getNotice(no);
        if(notice != null) {
            File path = new File(notice.getFile_path() + "/" + filename);
            if (path.exists() && path.delete()) {
                return ResponseEntity.ok("업로드한 파일을 삭제했습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 삭제에 실패했습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{no}")
    public String noticeDelete(Principal principal, @PathVariable("no") int no) {
        Notice notice = this.noticeService.getNotice(no);
        if(notice != null) {
            if(!notice.getAuthor().getId().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
            }
            this.noticeService.noticeDelete(notice);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 게시글이 존재하지 않습니다/");
        }
        return "redirect:/notice/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/modify/{no}")
    public String noticeCommentModify(NoticeCommentForm noticeCommentForm, Model model, @PathVariable("no") int no, Principal principal) {
        NoticeComment noticeComment = this.noticeService.getNoticeComment(no);
        if (!noticeComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        noticeCommentForm.setContent(noticeComment.getContent());
        return "/cafeNotice/reply_modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/modify/{no}")
    public String noticeCommentModify(@Valid NoticeCommentForm noticeCommentForm, @PathVariable("no") Integer no, Principal principal) {
        NoticeComment noticeComment = this.noticeService.getNoticeComment(no);
        if (!noticeComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.noticeService.noticeCommentModify(noticeComment, noticeCommentForm.getContent());
        return String.format("redirect:/notice/detail/%s", noticeComment.getNotice().getNo());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/delete/{no}")
    public String noticeCommentDelete(Principal principal, @PathVariable("no") int no) {
        NoticeComment noticeComment = this.noticeService.getNoticeComment(no);
        if (!noticeComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.noticeService.noticeCommentDelete(noticeComment);
        return String.format("redirect:/notice/detail/%s", noticeComment.getNotice().getNo());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/add/{no}")
    public String addVote(@PathVariable("no") Integer no, Principal principal) {
        Notice notice = this.noticeService.getNotice(no);
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        this.noticeService.addVote(notice, user);

        return String.format("redirect:/notice/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/remove/{no}")
    public String removeVote(@PathVariable("no") Integer no, Principal principal) {
        Notice notice = this.noticeService.getNotice(no);
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        this.noticeService.removeVote(notice, user);

        return String.format("redirect:/notice/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/rule")
    public String getRule() {
        int no = this.noticeService.getNoticeRuleNo();

        return String.format("redirect:/notice/detail/%s", no);
    }
}
