package com.cafe.cheezeHam.cafeWrite;

import com.cafe.cheezeHam.cafeBoast.BoastService;
import com.cafe.cheezeHam.cafeCatBuy.CatBuyService;
import com.cafe.cheezeHam.cafeEvent.EventService;
import com.cafe.cheezeHam.cafeFree.FreeService;
import com.cafe.cheezeHam.cafeHamBuy.HamBuyService;
import com.cafe.cheezeHam.cafeNotice.NoticeService;
import com.cafe.cheezeHam.cafeQna.QnaService;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import com.cafe.cheezeHam.cafeUser.CafeUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cafeWrite")
public class CafeWriteController {

    private final CafeUserService cafeUserService;
    private final BoastService boastService;
    private final FreeService freeService;
    private final QnaService qnaService;
    private final CatBuyService catBuyService;
    private final HamBuyService hamBuyService;

    @Value("${upload.dir}")
    private String uploadDir;

    private void createUploadDir() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/writePage")
    public String writePage(CafeWriteForm cafeWriteForm) {return "cafeWrite/main_write_page";}

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/writePage")
    public String writePage(@Valid CafeWriteForm cafeWriteForm, @RequestParam("type") String type, @RequestParam("files") List<MultipartFile> files, Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());

        Map<String, String> typeToDirMap = Map.of(
                "햄스터", "boast",
                "고양이", "boast",
                "자유", "free",
                "질문답변", "qna",
                "햄스터장터", "ham",
                "고양이장터", "cat"
        );

        String dirName = typeToDirMap.get(type);
        if (dirName == null) {
            return "redirect:/";
        }

        int maxNo = getMaxNoByType(type) + 1;
        File path = new File(uploadDir + dirName + "/" + maxNo);

        String filePath = handleFileUpload(files, path);

        if ("햄스터".equals(type) || "고양이".equals(type)) {
            this.boastService.create(cafeWriteForm.getTitle(), cafeWriteForm.getContent(), type, filePath, user);
        } else {
            createPostByType(type, cafeWriteForm, filePath, user);
        }

        return "redirect:/" + dirName + "/list";
    }

    private int getMaxNoByType(String type) {
        return switch (type) {
            case "햄스터", "고양이" -> this.boastService.getMaxNo();
            case "자유" -> this.freeService.getMaxNo();
            case "질문답변" -> this.qnaService.getMaxNo();
            case "햄스터장터" -> this.hamBuyService.getMaxNo();
            case "고양이장터" -> this.catBuyService.getMaxNo();
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        };
    }

    private void createPostByType(String type, CafeWriteForm cafeWriteForm, String filePath, CafeUser user) {
        switch (type) {
            case "자유" -> this.freeService.create(cafeWriteForm.getTitle(), cafeWriteForm.getContent(), filePath, user);
            case "질문답변" -> this.qnaService.create(cafeWriteForm.getTitle(), cafeWriteForm.getContent(), filePath, user);
            case "햄스터장터" -> this.hamBuyService.create(cafeWriteForm.getTitle(), cafeWriteForm.getContent(), filePath, user);
            case "고양이장터" -> this.catBuyService.create(cafeWriteForm.getTitle(), cafeWriteForm.getContent(), filePath, user);
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private String handleFileUpload(List<MultipartFile> files, File path) {
        if (files == null || files.isEmpty()) {
            return null;
        }
        try {
            createUploadDir();
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
            }
            if (fileUploaded) {
                return path.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
