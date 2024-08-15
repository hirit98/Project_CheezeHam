package com.cafe.cheezeHam.cafeSuggest;

import com.cafe.cheezeHam.FileData;
import com.cafe.cheezeHam.cafeAccuse.Accuse;
import com.cafe.cheezeHam.cafeAccuse.AccuseComment;
import com.cafe.cheezeHam.cafeNotice.Notice;
import com.cafe.cheezeHam.cafeNotice.NoticeRepository;
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
@RequestMapping("/suggest")
public class SuggestController {

    private final SuggestCommentRepository suggestCommentRepository;
    private final CafeUserService cafeUserService;
    private final SuggestService suggestService;
    private final SuggestRepository suggestRepository;
    private final NoticeRepository noticeRepository;

    @Value("${upload.dir}/suggest/")
    private String uploadDir;

    private void createUploadDir() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String suggestCreate(Model model, SuggestForm suggestForm){
        return "cafeSuggest/suggest_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String suggestCreate(Model model, SuggestForm suggestForm, @RequestParam("files") List<MultipartFile> files, Principal principal) throws IOException {
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        String file_path = null;
        if(files != null && !files.isEmpty()) {
            try {
                createUploadDir();

                int max_no = this.suggestService.getMaxNo() + 1;
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

        this.suggestService.create(suggestForm.getTitle(), suggestForm.getContent(), file_path, user);
        return "redirect:/suggest/list";
    }



    @GetMapping("/list")
    public String suggestList(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                             @RequestParam(value = "pageSize", defaultValue = "15") int pageSize, @RequestParam(value = "hiddenNotice", defaultValue = "false") boolean hiddenNotice, @RequestParam(value = "field", defaultValue = "0") int field
            , @RequestParam(value = "sort_value", defaultValue = "") String sort_value){
        List<Notice> importantNotice = noticeRepository.findImportantNoticeList();
        Page<Suggest> paging = null;

        if (sort_value.equals("asc")) {
            paging = this.suggestService.getSuggestsAsc(page, pageSize, field, keyword);
        } else if (sort_value.equals("desc")) {
            paging = this.suggestService.getSuggestsDesc(page, pageSize, field, keyword);
        } else {
            paging = this.suggestService.getSuggests(page, pageSize, field, keyword);
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

        return "cafeSuggest/suggest_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/detail/{no}")
    public String detail(Model model, @PathVariable("no") Integer no, @RequestParam(value = "sort",defaultValue = "") String sort, Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        Suggest suggest = this.suggestService.getSuggest(no);

        if (suggest != null) {
            boolean hasPrevious = this.suggestService.hasSuggest(no - 1);
            boolean hasNext = this.suggestService.hasSuggest(no + 1);
            boolean isChecked = this.suggestService.isChecked(suggest, user);

            if (sort.equals("asc")) {
                List<SuggestComment> commentList = this.suggestService.findAllByNoOrderByRegDateAsc(no);
                model.addAttribute("commentList", commentList);
            } else if (sort.equals("desc")) {
                List<SuggestComment> commentList = this.suggestService.findAllByNoOrderByRegDateDesc(no);
                model.addAttribute("commentList", commentList);
            } else {
                this.suggestService.increaseViewCount(no);
                List<SuggestComment> commentList = this.suggestService.getSuggestComments(no);
                model.addAttribute("commentList", commentList);
            }

            // 업로드 파일 List 가져오기
            if (suggest.getFile_path() != null && !suggest.getFile_path().isEmpty()) {
            /*File path = new File(notice.getFile_path());
            File[] files = path.listFiles();
            List<String> fileNames = new ArrayList<>();
            if(files != null) {
                for(File file : files) {
                    fileNames.add(file.getName());
                }
            }*/
                File path = new File(suggest.getFile_path());
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
            model.addAttribute("suggest", suggest);
            model.addAttribute("isChecked", isChecked);

            return "/cafeSuggest/suggest_detail";
        }
        return "redirect:/suggest/list";
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

    @GetMapping("/file/findList/{no}")
    @ResponseBody
    public List<FileData> suggestFindList(@PathVariable("no") int no) {
        Suggest suggest = this.suggestService.getSuggest(no);
        File path = new File(suggest.getFile_path());
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
        Suggest suggest = this.suggestService.getSuggest(no);
        if(suggest != null) {
            File path = new File(suggest.getFile_path() + "/" + filename);
            if (path.exists() && path.delete()) {
                return ResponseEntity.ok("업로드한 파일을 삭제했습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 삭제에 실패했습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/modify/{no}")
    public String suggestModify(SuggestForm suggestForm, Model model, @PathVariable("no") int no, Principal principal){

        Suggest suggest = this.suggestService.getSuggest(no);
        if(suggest != null) {
            if(!suggest.getAuthor().getId().equals(principal.getName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 게시글이 없습니다.");
        }

        model.addAttribute("suggest", suggest);
        suggestForm.setContent(suggest.getContent());
        suggestForm.setTitle(suggest.getTitle());
        return "cafeSuggest/suggest_modify_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/modify/{no}")
    public String suggestModify(@Valid SuggestForm suggestForm, Principal principal, @PathVariable("no") int no,  @RequestParam("files") List<MultipartFile> files){
        Suggest suggest = this.suggestService.getSuggest(no);

        if(suggest != null) {
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
            this.suggestService.suggestModify(suggest, suggestForm.getTitle(), suggestForm.getContent(), file_path);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 게시글이 없습니다.");
        }
        return String.format("redirect:/suggest/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{no}")
    public String suggestDelete(Principal principal, @PathVariable("no") int no) {
        Suggest suggest = this.suggestService.getSuggest(no);
        if(suggest != null) {
            if(!suggest.getAuthor().getId().equals(principal.getName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제할 게시글이 없습니다.");
        }
        this.suggestService.suggestDelete(suggest);
        return "redirect:/suggest/list";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/comment/create/{no}")
    public String createComment(@PathVariable("no") Integer no,@RequestParam(value="content") String content, Principal principal){
        CafeUser user = this.cafeUserService.getUser(principal.getName());

        Suggest suggest = this.suggestService.getSuggest(no);
        this.suggestService.createSuggestComment(suggest, content, user);

        return String.format("redirect:/suggest/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/modify/{no}")
    public String suggestCommentModify(SuggestCommentForm suggestCommentForm, @PathVariable("no") int no, Principal principal) {
        SuggestComment suggestComment = this.suggestService.getSuggestComment(no);
        if (!suggestComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        suggestCommentForm.setContent(suggestComment.getContent());
        return "/cafeSuggest/reply_modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/modify/{no}")
    public String suggestCommentModify(@Valid SuggestCommentForm suggestCommentForm, @PathVariable("no") Integer no, Principal principal) {
        SuggestComment suggestComment = this.suggestService.getSuggestComment(no);
        if (!suggestComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.suggestService.suggestCommentModify(suggestComment, suggestCommentForm.getContent());
        return String.format("redirect:/suggest/detail/%s", suggestComment.getSuggest().getNo());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/delete/{no}")
    public String suggestCommentDelete(Principal principal, @PathVariable("no") int no) {
        SuggestComment suggestComment = this.suggestService.getSuggestComment(no);
        if (!suggestComment.getCafeUser().getId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.suggestService.suggestCommentDelete(suggestComment);
        return String.format("redirect:/suggest/detail/%s", suggestComment.getSuggest().getNo());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/add/{no}")
    public String addVote(@PathVariable("no") Integer no, Principal principal) {
        Suggest suggest = this.suggestService.getSuggest(no);
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        this.suggestService.addVote(suggest, user);

        return String.format("redirect:/suggest/detail/%s", no);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/remove/{no}")
    public String removeVote(@PathVariable("no") Integer no, Principal principal) {
        Suggest suggest = this.suggestService.getSuggest(no);
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        this.suggestService.removeVote(suggest, user);



        return String.format("redirect:/suggest/detail/%s", no);
    }
}
