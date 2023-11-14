package edu.kh.project.myPage.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.service.MyPageService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SessionAttributes({"loginMember"})
// 1) Model에 세팅된 값의 key와 {} 작성된 값이 일치하면 session scope로 이동
// 2) Session으로 올려둔 값을 해당 클래스에서 얻어와 사용 가능하게함
//	-> @SessionAttribute(key)로 사용
@RequestMapping("/myPage")
@Controller
public class MyPageController {
	
	@Autowired
	private MyPageService service;
	
	@Autowired // bean으로 등록된 객체 중 타입이 일치하는 객체를 DI
	private BCryptPasswordEncoder bcrypt;

	
	
	// 내 정보 페이지로 이동
	@GetMapping("/info")
	public String info() {
		return "myPage/myPage-info";
	}
	
	// 프로필 페이지 이동
	@GetMapping("/profile")
	public String profile() {
		return "myPage/myPage-profile";
	}
	
	
	// 비밀번호 변경 페이지 이동
	@GetMapping("/changePw")
	public String changePw() {
		return "myPage/myPage-changePw";
	}
	
	// 탈퇴 페이지 이동
	@GetMapping("/secession")
	public String secession() {
		return "myPage/myPage-secession";
	}
	
	
	// 회원 정보 수정
	@PostMapping("/info")
	public String updateInfo(@SessionAttribute("loginMember") Member loginMember,
							Member updateMember,
							String[] memberAddress,
							RedirectAttributes ra) {
		
		/*
		 * @SessionAttribute("loginMember") Member loginMember
		 *  : Session에서 얻어온 "loginMember"에 해당하는 객체를
		 *    매개변수 Member loginMember에 저장
		 * 
		 * Member updateMember
		 * : 수정할 닉네임, 전화번호 담긴 커맨드 객체
		 * 
		 * 
		 * String[] memberAddress 
		 * : name="memberAddress"인 input 3개의 값(주소)
		 * 
		 * 
		 * RedirectAttributes ra : 리다이렉트 시 값 전달용 객체
		 * 
		 * */
		
		
		// 주소 하나로 합치자 (a^^^b^^^c)
		if(updateMember.getMemberAddress().equals(",,")) {
			updateMember.setMemberAddress(null);
		}else {
			// updateMember 에 주소문자열 세팅
			String addr = String.join("^^^", memberAddress);
			updateMember.setMemberAddress(addr);
		}
		
		// 로그인한 회원의 번호를 updateMember에 세팅
		updateMember.setMemberNo( loginMember.getMemberNo() );
		
		
		// DB 회원 정보 수정 (update) 서비스 호출
		int result = service.updateInfo(updateMember);
		
		String message = null;
		
		// 결과값으로 성공
		if(result > 0) {
			// -> 성공 시 Session에 로그인된 회원 정보도 수정(동기화)
			loginMember.setMemberNickname( updateMember.getMemberNickname() );
			loginMember.setMemberTel( updateMember.getMemberTel() );
			loginMember.setMemberAddress( updateMember.getMemberAddress() );
			
			message = "회원 정보 수정 성공";
			
			
		} else {
			// 실패에 따른 처리 

			message = "회원 정보 수정 실패";
			
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:info"; // 상대경로 (/myPage/info)
	}
	
	
	/* MultipartFile : input type="file"로 제출된 파일을 저장한 객체
	 * 
	 * [ 제공하는 메서드 ]
	 * - transferTo() : 파일을 지정된 경로에 저장(메모리 -> HDD/SSD) 
	 * - getOriginalFileName() : 파일 원본명
	 * - getSize() : 파일 크기
	 * 
	 * 
	 * */
	
	
	
	// 프로필 이미지 수정
	@PostMapping("/profile")
	public String updateProfile(
			@RequestParam("profileImage") MultipartFile profileImage // 업로드 파일
			, HttpSession session // 세션 객체
			, @SessionAttribute("loginMember") Member loginMember
			, RedirectAttributes ra // 리다이렉 시 메세지 전달
			) throws Exception{
		
		
		// 프로필 이미지 수정 서비스 호출
		int result = service.updateProfile(profileImage, loginMember);
		
		
		String message = null;
		if(result > 0) message = "프로필 이미지가 변경되었습니다";
		else			message = "프로필 변경 실패";
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:profile";
	}
	
	// 비밀번호 변경 (선)
	@PostMapping("/changePw")
	public String changePw(String currentPw, String newPw
		,@SessionAttribute("loginMember") Member loginMember
		,RedirectAttributes ra) {
		
		// 로그인한 회원 번호(DB에서 어떤 회원을 조회, 수정하는지 알아야 되니까)
		int memberNo = loginMember.getMemberNo();
		
		// 비밀번호 변경 서비스 호출
		int result = service.changePw(currentPw, newPw, memberNo);
		
		String path = "redirect:";
		String message = null;
		
		if(result > 0) { // 변경 성공
			message = "비밀번호가 변경 되었습니다.";
			path += "info";
			
		}else { // 변경 실패
			message = "현재 비밀번호가 일치하지 않습니다.";
			path += "changePw";
		}
		
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
	// 비밀번호 변경
//	@PostMapping("/changePw")
	public String updatePw(@SessionAttribute("loginMember") Member loginMember,
							String currentPw, String newPw,
							RedirectAttributes ra) {
		
		System.out.println("비밀번호 변경 : " + currentPw);
		System.out.println("비밀번호 변경 new : " + newPw);

		// 비밀번호 암호화 (Bcrypt) 후 다시 inputMember 세팅
		newPw = bcrypt.encode(newPw);		
		
		String pw = service.pwCheck(loginMember);
		
	    if (bcrypt.matches(currentPw, pw)) {
	    	System.out.println("비밀번호 일치, 변경준비");
	    	
	    	loginMember.setMemberPw(newPw);
	    	
	    	int result = service.updatePw(loginMember);

	    	if(result > 0) {
	    		System.out.println("비밀번호 변경 O");
	    	}else {
	    		System.out.println(":비밀번호 변경 X");
	    	}
	   
	    }else {
	    	System.out.println("비밀번호 불일치, 변경준비");
	    }
		
		
		
		
		return "redirect:changePw";
	}
	
	// 회원 탈퇴 (선) 
	@PostMapping("/secession")
	public String secession(String memberPw
			,@SessionAttribute("loginMember") Member loginMember
			,SessionStatus status
			,HttpServletResponse resp
			,RedirectAttributes ra) {
		
		// String memberPw : 입력한 비밀번호
		// SessionStatus status : 세션 관리 객체
		// HttpServletResponse resp : 서버 -> 클라이언트 응답하는 방법 제공 객체
		// RedirectAttributes ra : 리다이렉트 시 request로 값 전달하는 객체
		
		// 1. 로그인한 회원의 회원 번호 얻어오기
		// @SessionAttribute("loginMember") Member loginMember
		int memberNo = loginMember.getMemberNo();
		
		// 2. 회원 탈퇴 서비스 호출
		//	- 비밀번호가 일치하면 MEMBER_DEL_FL -> 'Y'로 바꾸고 1 반환
		//  - 비밀번호가 일치하지 않으면 -> 0 반환
		int result = service.secession(memberPw, memberNo);
		
		String path = "redirect:";
		String message = null;
		
		// 3. 탈퇴 성공 시
		if(result > 0) {
			// - message : 탈퇴 되었습니다
			message = "탈퇴 되었습니다";
			
			// - 메인 페이지로 리다이렉트
			path += "/";
			
			// - 로그아웃 
			status.setComplete();
			
			// + 쿠키 삭제
			Cookie cookie = new Cookie("saveId", ""); 
			// 같은 쿠기가 이미 존재하면 덮어쓰기된다
			
			cookie.setMaxAge(0); // 0초 생존 -> 삭제
			cookie.setPath("/"); // 요청 시 쿠기가 첨부되는 경로
			resp.addCookie(cookie); // 요청 객체를 통해서 클라이언트에게 전달
									// -> 클라이언트 컴퓨터에 파일로 생성
			
		}
		
		// 4. 탈퇴 실패 시
		else {
			// - message : 현재 비밀번호가 일치하지 않습니다
			message = "현재 비밀번호가 일치하지 않습니다";
			
			// - 회원 탈퇴 페이지로 리다이렉트
			path += "secession";
		}
		
		ra.addFlashAttribute("message",message);
		
		return path;
	}
		
	
	// 탈퇴
//	@PostMapping("/secession")
	public String memberSecession(@SessionAttribute("loginMember") Member loginMember, String memberPw,
									SessionStatus status/*HttpSession session*/,
									RedirectAttributes ra,
									HttpServletResponse resp) {
		
		String path = "redirect:";
	    // DB에서 사용자의 암호화된 비밀번호를 가져옵니다.
		String pw = service.pwCheck(loginMember);
		
	    if (bcrypt.matches(memberPw, pw)) {
	    	
	    	int result = service.memberSecession(loginMember);
	    	
	    	if(result > 0) {
		        System.out.println("비밀번호 일치, 회원 탈퇴 완료");
		        
		        Cookie cookie = new Cookie("saveId", null); // 삭제할 쿠키를 생성하고 값을 null로 설정
		        cookie.setMaxAge(0); // 쿠키의 수명을 0으로 설정하여 삭제
		        cookie.setPath("/"); // 쿠키의 경로를 설정 (루트 경로로 설정하면 모든 경로에서 쿠키 삭제)
		        resp.addCookie(cookie);
		        status.setComplete();
		       
		        
		        path += "/";
	    		
	    	}else {
	    		 path += "/secession";
	    	}
	        
	    } else {
	        System.out.println("비밀번호 불일치, 회원 탈퇴 실패");
	        
	        path += "/secession";
	    }

	    return path;
	}

	
	
	//@ExceptionHandler(Exception.class)
	public String exceptionHandler(Exception e, Model model) {
		
		// Exception e : 예외 정보를 담고있는 객체
		// Model model : 데이터 전달용 객체 (request scope 기본)
		
		e.printStackTrace(); // 예외 내용/발생 메서드 확인
		
		model.addAttribute("e", e); // 예외 발생 시 forward되는 페이지로 e를 전달함. (request scope도 가능하다)
		
		
		// 누구에 의해서?
		// View Resolver의 prefix, suffix를 붙여 JSP 경로를 만든것
		//return "/WEB-INF/views/common/error.jsp";
		return "common/error";	
	}
	
	
	
	
	
	
	
	
}
