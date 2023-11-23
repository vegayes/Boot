package edu.kh.project.board.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImage;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SessionAttributes({"loginMember"})
@RequestMapping("/board")
@Controller
public class BoardController {
	@Autowired
	private BoardService service;

	/* 목록 조회 : /board/1?cp=1 ( cp : current Page(현재 페이지) )
	 * 상세 조회 : /board/1/1500?cp=1
	 * 
	 * << 컨트롤러 따로 생성 >> 
	 * 1) 삽입 : /board2/1/insert
	 * 2) 수정 : /board2/1/1500/update
	 * 3) 삭제 : /board2/1/1500/delete
	 * 
	 */
	
	
	/* ******************** @PathVariable 사용 시 문제점과 해결법 *********************
	 * 
	 * 문제점 : 요청 주소와 @PathVariable로 가져다 쓸 주소의 레벨이 같다면 (ex) board/1 , board/이거)
	 * 			구분하지 않고 모두 매핑되는 문제가 발생
	 * 
	 * 해결방법 : @PathVariable 지정 시 정규 표현식 사용 
	 * {키:정규표현식}
	 * 
	 * 
	 * @PathVariable : URL 경로에 있는 값을 매개변수로 이용할 수 있게하는 어노테이션
	 * 	+ request scope에 세팅
	 * 
	 * 
	 * 	ex)   / board/1      /board?code=1
	 * 	사용하는 용도의 차이ㅏ
	 * 
	 *  - 자원(resource) 구분 / 식별
	 *  ex) git.com/eunseo
	 *  ex) github.com/testUser
	 *  ex) /board/1 -> 1번 (공지사항) 게시판
	 *  ex) /borad/2 -> 2번 (자유 게시판) 게시판
	 *  
	 *  query string을 사용하는 경우
	 *  * 검색 , 정렬, 필터링
	 *  ex) search.naver.com?query=날씨
	 *  ex) search.naver.com?query=종로맛집
	 *  ==> URL은 동일한데 검색한 내용만 다름
	 *  
	 *  ex) /board2/insert?code=1
	 *  ex) /board2/insert?code=2
	 *  --> 삽입이라는 공통된 동작 수행
	 *  	단, code에 따라 어디에 삽입할지 지정(필터링)
	 *  
	 *  ex) /board/list?order=recent (최신순)
	 *  ex) /board/list?order=most (인기순)
	 * 
	 * 
	 */
	
	// 게시글 목록 조회 
	@GetMapping("/{boardCode:[0-9]+}")  // 정규표현식 : 1자리 이상 숫자 
	public String selectBoardList(@PathVariable("boardCode") int boardCode, 
								  @RequestParam(value="cp", required=false, defaultValue = "1") int cp /*현재 페이지*/,
								  Model model,
								  @RequestParam Map<String,Object> paramMap) {
		
		
		// boardCode 확인
//		System.out.println("boardCode : " + boardCode);
		
		
		if(paramMap.get("key") == null) { // 검색어가 없을 때 (검색 X) 
			
			// 게시글 목록 조회 서비스 호출
			Map<String, Object> map = service.selectBoardList(boardCode, cp);
			
			// 조회 결과를 request scope에 세팅 후 forward
			model.addAttribute("map", map);
				
		}else { // 검색어가 있을 떄 ( 검색 O) 
			
			 paramMap.put("boardCode", boardCode);
			 
			 Map<String,Object> map = service.selectBoardList(paramMap, cp); // 적용
			
			 model.addAttribute("map", map);
			
			
		}
		
		

		
		return "board/boardList";
	}
	
	
	// @PathVariable : 주소에 지정된 부분을 변수에 저장 + request scope 저장
	
	// 게시글 상세 조회
	// PathVariable 사용
	@GetMapping("/{boardCode}/{boardNo}")
	public String boardDetail(
						@PathVariable("boardCode") int boardCode,
						@PathVariable("boardNo") int boardNo,
						Model model, // 데이터 전달용 객체 
						RedirectAttributes ra, // 리다이렉트 시 데이터 전달 객체
						@SessionAttribute(value = "loginMember", required =false) Member loginMember,
						// 세션에서 loginMember를 얻어오는데 없으면 null, 있으면 회원정보 저장
						
						// 쿠키를 이용한 조회 수 증가에서 사용
						HttpServletRequest req,
						HttpServletResponse resp
			) throws ParseException {
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		
		
		// 게시글 상세 조회 서비스 호출
		Board board = service.selectBoardList(map);
		
		// null이 나오는 경우
		// -> 주소창에 게시글 번호를 아무거나 넣은 경우 
		// -> 삭제된 게시글인 경우
		
		String path = null;
		
		if(board != null) { // 조회 결과가 있을 경우
			
			// 좋아요
			
				// 회원번호를 얻어와야 함. (1) / 게시물 번호 (2) / 게시글 종류 (3)
				// 좋아요 여부 확인 서비스 호출
				// 결과값을 통해 분기처리
					// 누른적이 있을 경우 처리
			
			// -------------------------------------
			// 현재 로그인 상태인 경우
			// 로그인한 회원이 해당 게시글에 좋아요를 눌렀는지 확인
			
			//로그인 상태인 경우
			if(loginMember != null) {
				
				System.out.println("로그인됨");
				
				// 회원정보를 얻어와야 함.
				// map(boardCode, boardNo, memberNo)
				map.put("memberNo", loginMember.getMemberNo());
				
				
				System.out.println("회원정보 얻어왔음 : " + loginMember.getMemberNo());
				
				// 좋아요 여부 확인 서비스 호출
				int result = service.boardLikeCheck(map);
				
				System.out.println("좋아요 여부 확인 Controller result : " + result);
				
				// 결과값을 통해 분기처리
				// 누른적이 있을 경우 처리
				// "likeCheck"
				if(result > 0) model.addAttribute("likeCheck", "on");
				
			}
			
			
			
			// --------------------------------------------------------------
			
			// 쿠키를 이용한 조회 수 증가 처리
			// 1) 비회원 또는 로그인한 회원의 글이 아닌 경우
			if(loginMember == null || loginMember.getMemberNo() != board.getMemberNo()) {
				
				// 2) 쿠키 얻어옴
				Cookie c = null;
				
				// 요청에 담겨있는 모든 쿠키 얻어오기
				Cookie[] cookies = req.getCookies();
				
				if(cookies != null) { // 쿠키가 존재할 경우 
					// 쿠키 중 "readBoardNo"라는 쿠키를 찾아서 c에 대입
					
					for(Cookie cookie : cookies) {
						if(cookie.getName().equals("readBoardNo")) {
							c = cookie;
							break;
						}
					}
					
				}
				
				
				// 3) 기존 쿠키가 없거나 ( c== null) 
				//    존재는 하나 현재 게시글 번호가 쿠키에 저장되지 않은 경우 (해당 게시글 본적 없음)
				int result = 0;
				
				if(c == null) {
					// 쿠키가 존재 X -> 하나 새로 생성
					c = new Cookie("readBoardNo", "|" + boardNo + "|");
					
					// 조회수 증가 서비스 호출
					result = service.updateReadCount(boardNo);
					
				}else {
					// 현재 게시글 번호가 쿠키에 있는지 확인
					
					// Cookie.getValue() : 쿠키에 저장된 모든 값을 읽어옴 -> String으로 반환
					
					// String.indexOf("문자열")
					// : 찾는 문자열이 String 몇번 인덱스에 존재하는지 반환
					// 단, 없으면 -1 반환
					
					if(c.getValue().indexOf( "|" + boardNo + "|") == -1) {
						// readBoardNo : |1987||2000||1955| -- > c.getValue
						// 1998이면 -1 값 반환 , 2000이면 1 반환 ( 인덱스 값 ) , 1955이면 2반환 
						
						// 현재 게시글 번호가 쿠키에 없다면 
						// 기존 값에 게시글 번호 추가해서 다시 세팅 
						c.setValue(c.getValue() + "|" + boardNo + "|");
						
						// 조회수 증가 서비스 호출
						result = service.updateReadCount(boardNo);
					}
					
					
				}
				
				// 3) 종료
				if(result > 0) {
					board.setReadCount(board.getReadCount() + 1);
					// 조회된 board 조회 수와 DB 조회 수 동기화
					
					// 적용 경로 설정
					c.setPath("/"); //  "/" 이하 경로 요청 시 쿠키 서버로 전달
					
					// 수명 지정
					Calendar cal = Calendar.getInstance(); // 싱글톤 패턴
					cal.add(cal.DATE, 1); // 현재 Date에서 1일 추가하겠다.
					
					// 날짜 표기법 변경 객체 (DB의 TO_CHAR()와 비슷)
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					
					// java.util.Date
					Date a = new Date(); // 현재 시간
					
					Date temp = new Date(cal.getTimeInMillis()); // 내일 (24시간 후)
					// 2023-05-11 12:16:10
					
					Date b = sdf.parse(sdf.format(temp)); // 내일 0시 0분 0초
					
					
					// 내일 0시 0분 0초 - 현재 시간
					long diff = (b.getTime()  -  a.getTime()) / 1000; 
					// -> 내일 0시 0분 0초까지 남은 시간을 초단위로 반환
					
					c.setMaxAge((int)diff); // 수명 설정
					
					resp.addCookie(c); // 응답 객체를 이용해서
									   // 클라이언트에게 전달
				}

			}
			
			// ---------------------------------------------------------------
			
			
			
			
			
			path = "board/boardDetail"; // forward 할 jsp 경로
			
			System.out.println("게시글의 회원정보 얻어왔음 : " + board.getMemberNo());
			model.addAttribute("board",board);
			
			
			
			// 게시글에 이미지가 있을 경우
//			if(!board.getImageList().isEmpty()) {
			if(board.getImageList().size() > 0) {
				BoardImage thumbnail = null;
				
				// 0번 인덱스 이미지의 순서가 0인경우 == 썸네일
				if(board.getImageList().get(0).getImageOrder() == 0) {
					thumbnail = board.getImageList().get(0);
				}
				
				model.addAttribute("thumbnail", thumbnail); // 썸네일 없으면 null 
				
				// 삼항 연산자 사용하여 thumbnail이 null이 아니면 start = 1 null이면 start = 0을 반환함. 
				model.addAttribute("start", thumbnail != null ? 1:0);
			}
			
			
			
		}else { // 조회 결과가 없을 경우
			
			path = "redirect:/board/" + boardCode;
			// 게시판 첫 페이지로 리다이렉트 
			ra.addFlashAttribute("message", "해당 게시글이 존재하지 않습니다.");
		}
		
		
		return path;
	}
	
	
	
	
	// 좋아요 진행
	@PostMapping("/like")
	@ResponseBody
	public int updateLike(@RequestBody Map<String, Integer> paramMap) {
	
		System.out.println("좋아요 확인 : " + paramMap);
		
		return service.updateLike(paramMap);
	}
	
	
	
	
}
