package com.cafe.cheezeHam;

import com.cafe.cheezeHam.cafeAccuse.AccuseService;
import com.cafe.cheezeHam.cafeBoast.BoastService;
import com.cafe.cheezeHam.cafeCatBuy.CatBuyService;
import com.cafe.cheezeHam.cafeEvent.EventService;
import com.cafe.cheezeHam.cafeFree.FreeService;
import com.cafe.cheezeHam.cafeHamBuy.HamBuyService;
import com.cafe.cheezeHam.cafeNotice.NoticeService;
import com.cafe.cheezeHam.cafeQna.QnaService;
import com.cafe.cheezeHam.cafeRegGreeting.RegGreetingService;
import com.cafe.cheezeHam.cafeSuggest.SuggestService;
import com.cafe.cheezeHam.cafeUser.CafeUser;
import com.cafe.cheezeHam.cafeUser.CafeUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Fail.fail;

@SpringBootTest
class CheezeHamApplicationTests {

	@Autowired
	private CafeUserService cafeUserService;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private EventService eventService;
	@Autowired
	private RegGreetingService regGreetingService;
	@Autowired
	private BoastService boastService;
	@Autowired
	private FreeService freeService;
	@Autowired
	private QnaService qnaService;
	@Autowired
	private CatBuyService catBuyService;
	@Autowired
	private HamBuyService hamBuyService;
	@Autowired
	private AccuseService accuseService;
	@Autowired
	private SuggestService suggestService;

	@Test
	void contextLoads() {
	}

	@Test
	void testCreateADMIN() {
		CafeUser admin = new CafeUser();
		admin.setId("admin");
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		admin.setPassword(passwordEncoder.encode("1234"));
		admin.setUsername("관리자");
		admin.setRegdate(LocalDateTime.now());
		admin.setROLE("ADMIN");
		admin.setGrade("관리자");
		admin.setProfile("/profile/남자.png");
		this.cafeUserService.createADMIN(admin);
	}
	@Test
	void testCreateUSER() {
		CafeUser user = new CafeUser();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode("1234"));
		user.setId("hong");
		user.setUsername("홍길동");
		user.setROLE("USER");
		user.setGrade("해씨");
		user.setRegdate(LocalDateTime.now());
		user.setBirthday("1990-01-01");
		user.setGender("male");
		user.setEmail("hong@gmail.com");
		user.setPhone("010-1234-1234");
		user.setProfile("/profile/남자.png");
		this.cafeUserService.create(user);
	}
	@Test
	void testCreateUSER2() {
		CafeUser user = new CafeUser();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode("1234"));
		user.setId("user1");
		user.setUsername("김유저");
		user.setROLE("USER");
		user.setGrade("해씨");
		user.setRegdate(LocalDateTime.now());
		user.setBirthday("2001-06-12");
		user.setGender("female");
		user.setEmail("user1@naver.com");
		user.setPhone("010-9999-1111");
		user.setProfile("/profile/여자.png");
		this.cafeUserService.create(user);
	}
	@Test
	void testCreateNotice() {
		for(int i = 1; i <= 15; i++) {
			String title = String.format("중요 공지사항[%03d]", i);
			String content = "중요\n중요\n중요\n중요\n중요\n";

			this.noticeService.createImportant(title, content, null, null);
		}
	}
	@Test
	void testCreateNormalNotice() {
		for(int i = 1; i <= 500; i++) {
			String title = String.format("일반 공지사항[%03d]", i);
			String content = "일반\n일반\n일반\n일반\n일반\n일반\n일반\n";

			this.noticeService.create(title, content, null,null);
		}
	}
	@Test
	void testCreateRuleNormalNotice() {
		String title = "카페규칙사항";
		String content = "카페규칙입니당!";

		this.noticeService.create(title, content, null,null);
	}
	@Test
	void testCreateEvent() throws IOException {
		for(int i = 1; i <= 500; i++) {
			String title = String.format("중요 이벤트[%03d]", i);
			String content = "이벤트\n이벤트\n이벤트\n이벤트\n이벤트\n";
			try {
				eventService.increaseViewCount(i);
				this.eventService.create(title, content, null, null);
			} catch (IOException e) {
				fail("IOException 발생: " + e.getMessage());
			}
		}
	}

	@Test
	void testCreateRegGreeting() throws IOException {
		for(int i = 1; i <= 600; i++) {
			String title = String.format("가입인사[%03d]", i);
			String content = " <h4>1. 가입한 동기는 무엇인가요?</h4>\n" +
					"<br>" +
					"     : 햄스터에 대해 공부하기 위해서 가입했습니다.\n" +
					"\n" + "<br>" +
					"     <h4>2. 가입시 열심히 활동하시는 것을 약속하시나?</h4>\n"  +
					"<br>" +
					"     : 네\n" +
					"\n" + "<br>" +
					"     <h4>3. cheezeham 카페를 알게 된 경로는 무엇인가요 ?</h4>\n" +
					"<br>" +
					"     : 인터넷에 햄스터를 검색해보다 알게되었습니다\n" +
					"\n" + "<br>" +
					"    <h4>4. 카페 규칙을 읽어보시고 어길시 강제 탈퇴하는 것에 동의하시나요?</h4>\n" + "<br>" +
					"     : 네\n" +
					"\n" + "<br>" +
					"     <h4>5. 마지막으로 반려동물 친구 사진과 가입인사 부탁드려요~</h4>\n" + "<br>" +
					"     : 안녕하세요 찍찍이를 키우고 있는 찍맘입니다~";;
			this.regGreetingService.create(title, content,null);
		}
	}
	@Test
	void testCreateBoast() {
		for (int i = 1; i <= 10; i++) {
			String title = String.format("우리고양이를 자랑합니다~[%03d]", i);
			String content = "우리 고양이는 잘 놀아요~";
			String type = "고양이";

			this.boastService.create(title, content, type, null, null);
		}
		for (int i = 1; i <= 10; i++) {
			String title = String.format("우리햄스터를 자랑합니다~[%03d]", i);
			String content = "우리 햄스터는 잘 놀아요~";
			String type = "햄스터";

			this.boastService.create(title, content, type, null, null);
		}
		for (int i = 1; i <= 20; i++) {
			String title = String.format("우리고양이를 자랑합니다~[%03d]", i);
			String content = "우리 고양이는 잘 놀아요~";
			String type = "고양이";

			this.boastService.create(title, content, type, null, null);
		}
		for (int i = 1; i <= 60; i++) {
			String title = String.format("우리햄스터를 자랑합니다~[%03d]", i);
			String content = "우리 햄스터는 잘 놀아요~";
			String type = "햄스터";

			this.boastService.create(title, content, type, null, null);
		}
		for (int i = 1; i <= 90; i++) {
			String title = String.format("우리고양이를 자랑합니다~[%03d]", i);
			String content = "우리 고양이는 잘 놀아요~";
			String type = "고양이";

			this.boastService.create(title, content, type, null, null);
		}
	}
	@Test
	void testCreateFreeBoards() {
		for(int i = 1; i <= 150; i++) {
			String title = String.format("자유 게시글[%03d].", i);
			String content = "자유\n자유\n자유\n자유\n자유\n자유\n자유\n";

			this.freeService.create(title, content, null,null);
		}
	}
	@Test
	void testCreateQna() {
		for(int i = 1; i <= 300; i++) {
			String title = String.format("질문답변[%03d]", i);
			String content = "질문합니다~";
			this.qnaService.create(title, content, null,null);
		}
	}
	@Test
	void testCreateCatBuy() {
		for(int i = 1; i <= 300; i++) {
			String title = String.format("고양이장터 장난감팔아요[%03d]", i);
			String content = "낚시대 사실래요? 사실분은 챗주세요";
			this.catBuyService.create(title, content, null, null);
		}
	}
	@Test
	void testCreateHamBuy() {
		for(int i = 1; i <= 300; i++) {
			String title = String.format("햄스터장터 간식팔아요[%03d]", i);
			String content = "해씨 사실래요? 사실분은 챗주세요";
			this.hamBuyService.create(title, content, null, null);
		}
	}
	@Test
	void testCreateAccuse() {
		for(int i = 1; i <= 300; i++) {
			String title = String.format("000을 신고합니다[%03d]", i);
			String content = "규칙을 여러번 지키지않았습니다.";
			this.accuseService.create(title, content, null, null);
		}
	}
	@Test
	void testCreateSuggest() {
		for(int i = 1; i <= 300; i++) {
			String title = String.format("000을 건의합니다![%03d]", i);
			String content = "카페에서 000과 000문제에 대해서 개선해주셨으면 좋겠습니다!";
			this.suggestService.create(title, content, null, null);
		}
	}
}
