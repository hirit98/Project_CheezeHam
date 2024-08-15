package com.cafe.cheezeHam.cafeFree;

import com.cafe.cheezeHam.FileData;
import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeNotice.NoticeService;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import com.cafe.cheezeHam.cafeUser.CafeUserService;
import jakarta.validation.Valid;
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
@RequestMapping("/free")
public class FreeController {

    private final FreeService freeService;
    private final NoticeService noticeService;
    private final CafeUserService cafeUserService;

    @Value("${upload.dir}/free/")
    private String uploadDir;

    private void createUploadDir() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String freeCreate(FreeForm freeForm) {return "cafeFree/free_form";}

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String freeCreate(@Valid FreeForm freeForm, BindingResult bindingResult, @RequestParam("files") List<MultipartFile> files, Principal principal) {
        if(bindingResult.hasErrors()) {
            return "cafeFree/free_form";
        }
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        String file_path = null;

        if(files != null && !files.isEmpty()) {
            try {
                createUploadDir();

                int max_no = this.freeService.getMaxNo() + 1;
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
        this.freeService.create(freeForm.getTitle(), freeForm.getContent(), file_path, user);
        return "redirect:/free/list";
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                       @RequestParam(value = "pageSize", defaultValue = "15") int pageSize, @RequestParam(value = "hiddenNotice", defaultValue = "false") boolean hiddenNotice,
                       @RequestParam(value = "field", defaultValue = "0") int field, @RequestParam(value = "sort_value", defaultValue = "") String sort_value) {

        List<Notice> importantNotice = this.noticeService.getImportantNotices();
        Page<Free> paging;

        if (sort_value.equals("asc")) {
            paging = this.freeService.getFreesAsc(page, pageSize, field, keyword);
        } else if (sort_value.equals("desc")) {
            paging = this.freeService.getFreesDesc(page, pageSize, field, keyword);
        } else {
            paging = this.freeService.getFreeBoards(page, pageSize, field, keyword);
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

        return "cafeFree/free_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/detail/{no}")
    public String freeDetail(@PathVariable("no") int no, @RequestParam(value = "sort", defaultValue = "") String sort, Model model, Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        Free free = this.freeService.getFree(no);

        if (free != null) {
            boolean hasPrevious = this.freeService.hasFree(no - 1);
            boolean hasNext = this.freeService.hasFree(no + 1);
            boolean isChecked = this.freeService.isChecked(free, user);

            if (sort.equals("asc")) {
                List<FreeComment> commentList = freeService.findAllByNoOrderByRegDateAsc(no);
                model.addAttribute("commentList", commentList);
            } else if (sort.equals("desc")){
                List<FreeComment> commentList = freeService.findAllByNoOrderByRegDateDesc(no);
                model.addAttribute("commentList", commentList);
            } else {
                this.freeService.increaseViewCount(no);
                List<FreeComment> commentList = freeService.getFreeComments(no);
                model.addAttribute("commentList", commentList);
            }

            if (free.getFile_path() != null && !free.getFile_path().isEmpty()) {
                File path = new File(free.getFile_path());
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
            model.addAttribute("isChecked", isChecked);
            model.addAttribute("free", free);

            return "cafeFree/free_detail";
        }
        return "redirect:/free/list";
    }

    /*@GetMapping("/Comment_ASC_DESC/{no}")
    public String getFreeComment_ASC_DESC(@PathVariable("no") int no, @RequestParam(value = "sort", defaultValue = "desc") String sort, Model model, Principal principal) {
        List<FreeComment> commentList;

        CafeUser user = this.cafeUserService.getUser(principal.getName());
        Free free = this.freeService.getFree(no);

        if ("asc".equals(sort)) {
            commentList = freeService.findAllByNoOrderByRegDateAsc(no);
        } else {
            commentList = freeService.findAllByNoOrderByRegDateDesc(no);
        }

        boolean hasPrevious = this.freeService.hasFree(no - 1);
        boolean hasNext = this.freeService.hasFree(no + 1);
        boolean isChecked = this.freeService.isChecked(free, user);

        model.addAttribute("hasPrevious", hasPrevious);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("isChecked", isChecked);
        model.addAttribute("free", free);
        model.addAttribute("commentList", commentList);
        return "cafeFree/free_detail";
    }*/

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
        Free free = this.freeService.getFree(no);
        this.freeService.createFreeComment(free, content, user);

        return String.format("redirect:/free/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{no}")
    public String freemodify(FreeForm freeForm, Model model, Principal principal, @PathVariable("no") int no) {
        Free free = this.freeService.getFree(no);
        if(free != null) {
            if(!free.getAuthor().getId().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "수정할 게시글을 찾을수 없습니다.");
        }

        model.addAttribute("free", free);
        freeForm.setTitle(free.getTitle());
        freeForm.setContent(free.getContent());

        return "/cafeFree/free_modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{no}")
    public String freeModify(@Valid FreeForm freeForm, @PathVariable("no") int no, Principal principal, @RequestParam("files") List<MultipartFile> files) {
        Free free = this.freeService.getFree(no);
        if(free != null) {
            if(!free.getAuthor().getId().equals(principal.getName())) {
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
            this.freeService.freeModify(free, freeForm.getTitle(), freeForm.getContent(), file_path);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 게시글이 존재하지 않습니다.");
        }
        return String.format("redirect:/free/detail/%s", no);
    }

    @GetMapping("/file/findList/{no}")
    @ResponseBody
    public List<FileData> freeFindList(@PathVariable("no") int no) {
        Free free = this.freeService.getFree(no);
        File path = new File(free.getFile_path());
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
    public ResponseEntity<String> fileRemove(@RequestParam("filename") String filename, @RequestParam("no") int no, Principal principal) {
        Free free = this.freeService.getFree(no);
        if(free != null) {
            File path = new File(free.getFile_path() + "/" + filename);
            if (path.exists() && path.delete()) {
                return ResponseEntity.ok("업로드한 파일을 삭제했습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 삭제에 실패했습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{no}")
    public String freeDelete(Principal principal, @PathVariable("no") int no) {
        Free free = this.freeService.getFree(no);
        if (free != null) {
            if (!free.getAuthor().getId().equals(principal.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
            }
            File path = new File(free.getFile_path());
            if (path.exists()) {
                if (!path.delete()) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.");
                }
            }
            this.freeService.freeDelete(free);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 게시글이 존재하지 않습니다.");
        }
        return "redirect:/free/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/modify/{no}")
    public String freeCommentModify(FreeCommentForm freeCommentForm, Model model, @PathVariable("no") int no, Principal principal) {
        FreeComment freeComment = this.freeService.getFreeComment(no);
        if (!freeComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        freeCommentForm.setContent(freeComment.getContent());
        return "/cafeFree/freeComment_modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/modify/{no}")
    public String freeCommentModify(@Valid FreeCommentForm freeCommentForm, @PathVariable("no") Integer no, Principal principal) {
        FreeComment freeComment = this.freeService.getFreeComment(no);
        if (!freeComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.freeService.freeCommentModify(freeComment, freeCommentForm.getContent());
        return String.format("redirect:/free/detail/%s", freeComment.getFree().getNo());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/delete/{no}")
    public String freeCommentDelete(Principal principal, @PathVariable("no") int no) {
        FreeComment freeComment = this.freeService.getFreeComment(no);
        if (!freeComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.freeService.freeCommentDelete(freeComment);
        return String.format("redirect:/free/detail/%s", freeComment.getFree().getNo());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/add/{no}")
    public String addVote(@PathVariable("no") Integer no, Principal principal) {
        Free free = this.freeService.getFree(no);
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        this.freeService.addVote(free, user);

        return String.format("redirect:/free/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/remove/{no}")
    public String removeVote(@PathVariable("no") Integer no, Principal principal) {
        Free free = this.freeService.getFree(no);
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        this.freeService.removeVote(free, user);

        return String.format("redirect:/free/detail/%s", no);
    }
}
