package com.cafe.cheezeHam.cafeUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class CafeUserController {

    private final CafeUserService cafeUserService;
    private final PasswordEncoder passwordEncoder;

    @Value("src/main/resources/static/profile/")
    private String profileDir;

    private void createDir() {
        File file = new File(profileDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/signup")
    public String signup(CafeUserCreateForm cafeUserCreateForm) {return "signup_form";}

    @PostMapping("/signup")
    public String signup(@Valid CafeUserCreateForm cafeUserCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }
      /*
        boolean error = false;
        String id = cafeUserCreateForm.getId();
        if (id.length() < 3 || id.length() > 25) {
            bindingResult.rejectValue("id", "id.length", "아이디는 3자 이상 25자 이하여야 합니다.");
            error = true;
        }
        String password = cafeUserCreateForm.getPassword();
        if (password.length() < 4 || id.length() > 16){
            bindingResult.rejectValue("password", "password.length", "비밀번호는 4자 이상 16자 이하여야 합니다.");
            error = true;
        }
        String username = cafeUserCreateForm.getUsername();
        if (username.length() < 4 || username.length() > 16){
            bindingResult.rejectValue("username", "username.length", "사용자명은 3자 이상 25자 이하여야 합니다.");
            error = true;
        }

        String email = cafeUserCreateForm.getEmail();
        if (!EmailValid.isValidEmail(email)) {
            bindingResult.rejectValue("email", "emailInvalid", "유효하지 않은 이메일 형식입니다.");
            error = true;
        }
        String birthday = cafeUserCreateForm.getBirthday();
        if (!isValidBirthdays.isValidBirthday(birthday)){
            bindingResult.rejectValue("birthday", "birthdayInvalid", "유효하지 않은 생년월일 형식입니다.");
            error = true;
        }
        String phone = cafeUserCreateForm.getPhone();
        if (!isValidPhoneNumbers.isValidPhoneNumber(phone)){
            bindingResult.rejectValue("phone", "phoneInvalid", "유효하지 않은 전화번호 형식입니다.");
            error = true;
        }

        if(error){
            return "signup_form";
        }


        );*/
        try {
            CafeUser user = getUser(cafeUserCreateForm);
            this.cafeUserService.create(user);
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        return "redirect:/";
    }

    private static CafeUser getUser(CafeUserCreateForm cafeUserCreateForm) {
        CafeUser user = new CafeUser();
        user.setId(cafeUserCreateForm.getId());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(cafeUserCreateForm.getPassword()));
        user.setUsername(cafeUserCreateForm.getUsername());
        user.setEmail(cafeUserCreateForm.getEmail());
        user.setGender(cafeUserCreateForm.getGender());
        if (cafeUserCreateForm.getGender().equals("male")) {
            user.setProfile("/profile/남자.png");
        } else {
            user.setProfile("/profile/여자.png");
        }
        user.setBirthday(cafeUserCreateForm.getBirthday());
        user.setPhone(cafeUserCreateForm.getPhone());
        user.setRegdate(LocalDateTime.now());
        user.setROLE("USER");
        user.setGrade("해씨");
        return user;
    }

    @PostMapping("/check")
    @ResponseBody
    public String checkLogin(HttpServletRequest request) {
        String username = null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            username = (String) session.getAttribute("username");
        }
        return username != null ? username : null;
    }

    @GetMapping("/myInfo")
    public ResponseEntity<String> myInfo_checkLogin(HttpSession session) {
        if (session.getAttribute("username") == null) {
            // 로그인 상태가 아니면 403 상태 코드 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인 후 이용 가능합니다");
        }
        // 로그인 상태인 경우 정상 메시지 반환
        return ResponseEntity.ok("Authorized");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/update")
    public String update(Model model, Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());

        model.addAttribute("user", user);
        return "myInfoModify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String update(@RequestParam("id") String id, @RequestParam("pwd") String pwd, @RequestParam("phone") String phone,
                         @RequestParam("profile") MultipartFile profile, Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());

        if (user.getId().equals(id)) {
            if (!profile.isEmpty()) {
                try {
                    createDir();
                    File path = new File(profileDir + user.getId());
                    System.out.println("저장 경로: " + path.getAbsolutePath());
                    if (!path.exists()) {
                        path.mkdirs();
                    }
                    String fileName = profile.getOriginalFilename();
                    Path filePath = Paths.get(path.getAbsolutePath(), fileName);
                    Files.write(filePath, profile.getBytes());
                    user.setProfile("/profile/" + user.getId() + "/" + fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            user.setPassword(passwordEncoder.encode(pwd));
            user.setPhone(phone);
            this.cafeUserService.modifyCafeUser(user);
        }

        return "redirect:/user/update";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/resign")
    public String deleteUser() {return "resign_form";}

    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/resign")
    @ResponseBody
    public boolean deleteUser(@RequestParam("password") String password, Principal principal) {
        CafeUser user = this.cafeUserService.getUser(principal.getName());
        if (passwordEncoder.matches(password, user.getPassword())) {
            this.cafeUserService.deleteCafeUser(user.getId());
            return true;
        }
        return false;
    }

    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    @GetMapping("/list")
    public String userList(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,
                           @RequestParam(value = "type", defaultValue = "user") String type) {

        Page<CafeUser> paging = this.cafeUserService.getAllUsers(page, 10, keyword);

        if (type.equals("user") && !keyword.isEmpty()) {
            paging = cafeUserService.getCafeUsers(page, 10, keyword);
        }
        if (type.equals("admin")) {
            paging = this.cafeUserService.getCafeAdmin(page, 10, keyword);
        }

        int block = 10;
        int currentPage = paging.getNumber() + 1;
        int totalPage = paging.getTotalPages();

        int startBlock = (((currentPage - 1) / block) * block) + 1;
        int endBlock = startBlock + block - 1;
        if (endBlock > totalPage) {
            endBlock = totalPage;
        }

        model.addAttribute("startBlock", startBlock);
        model.addAttribute("endBlock", endBlock);
        model.addAttribute("keyword", keyword);
        model.addAttribute("paging", paging);
        model.addAttribute("type", type);

        return "user_list";
    }

    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    @GetMapping("/find/{no}")
    @ResponseBody
    public CafeUser find(@PathVariable("no") int no) {
        return this.cafeUserService.getUserByNo(no);
    }

    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    @DeleteMapping("/remove/{no}")
    public ResponseEntity<?> remove(@PathVariable("no") int no) {
        try {
            CafeUser user = this.cafeUserService.getUserByNo(no);
            this.cafeUserService.deleteCafeUser(user.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
    @PostMapping("/modify/{no}")
    public ResponseEntity<?> modify(@PathVariable("no") int no, @RequestParam("grade") String grade) {
        try {
            CafeUser user = this.cafeUserService.getUserByNo(no);
            user.setGrade(grade);
            if(grade.equals("관리자")) {
                user.setROLE("ADMIN");
            } else {
                user.setROLE("USER");
            }
            this.cafeUserService.modifyCafeUser(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }


}