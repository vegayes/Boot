package edu.kh.project.board.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.board.model.service.BoardService2;
import edu.kh.project.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/board2")
@SessionAttributes("{loginMember}")
public class BoardController2 {
	
	@Autowired
	private BoardService2 service; // 삽입, 수정, 삭제 
	
	@Autowired
	private BoardService boardService;  // (목록, 상세 조회 ) 수정에서 상세조회의 값을 가져오기 위해서 
	
	// js에서 넘어온 게시글 작성 화면 전환 
	
	/** 게시글 작성 화면 전환
	 * @return
	 */
	@GetMapping("/{boardCode:[0-9]+}/insert")
	public String boardInsert(@PathVariable("boardCode") int boardCode
				// @PathVariable : 주소 값 가져오기 + request scope에 값 올리기 
				// ???????????? request scope에 올라가면 다른 곳에서 올려놨으니까 안가져와도 되지 않나?
			) {
		
		return "board/boardWrite";
	}
	
	
	@PostMapping("/{boardCode:[0-9]+}/insert")
	public String boarInsert( 
			@PathVariable("boardCode") int boardCode,
			Board board, // 커맨드 객체 (필드에 파라미처 담겨있음!) 
			@RequestParam(value = "images", required = false) List<MultipartFile> images,
			@SessionAttribute("loginMember") Member loginMember,
			RedirectAttributes ra
			) throws IllegalStateException, IOException {
		
		// 파라미터 : 제목, 내용, 파일(0~5개)
		// 파일 저장 경로 : HttpSession
		// 세션 : 로그인한 회원의 번호
		// 리다이렉트 시 데이터 전달 : RedirectAttributes ra ( message )
		
		
		/*
		 * List<MultipartFile>
		 * - 업로드된 이미지가 없어도 List에 MultipartFile 요소는 존재함. 
		 * 
		 * - 단, 업로드된 이미지가 없는 MultipartFile의 요소는 파일크기 ( size )가 0
		 *   또는 파일명(getOriginalFileName()) "" 빈칸
		 */
		
		// 1. 로그인한 회원 번호를 얻어와 board에 Setting 
		board.setMemberNo(loginMember.getMemberNo());
		
		
		// 2. boardCode도 board에 Setting
		board.setBoardCode(boardCode);
		

		
		// 게시글 삽입 서비스 호출 후 삽입된 게시글 번호 반환 받기 		
		int boardNo = service.boardInsert(board, images);
		
		
		
		// 게시글 삽입 성공 시
		// -> 방금 삽입한 게시글의 상세 조회 페이지로 리다이렉트 
		// -> /board/{boardCode}/{boardNo}
		String message = null;
		String path = "redirect:";
		
		if(boardNo > 0) {
			message = "게시글이 등록 되었습니다.";
			path += "/board/" + boardCode + "/" + boardNo;
		}else {
			message = "게시글 등록이 실패되었습니다.";
			path += "insert";
		}
		
		
		ra.addFlashAttribute("message",message);
		
		return path;
	}
	
	
	
	/** 게시글 수정으로 화면 전환
	 * @param boardCode
	 * @return
	 */
	@GetMapping("/{boardCode}/{boardNo}/update")
	public String boardUpdate(@PathVariable("boardCode") int boardCode,
							  @PathVariable("boardNo") int boardNo, 
							  Model model
							  // Model : 데이터 전달용 객체 (기본 scope : requset)
							  ) {
		
		System.out.println("들어옴?");
		Map<String, Object> map = new HashMap<String , Object>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		// 한번 더 조회 
		Board board = boardService.selectBoardList(map);
		
		
		model.addAttribute("board", board);
		
		return "board/boardUpdate";
	}
	
	
	
	/** 게시글 수정
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	@PostMapping("/{boardCode}/{boardNo}/update")
	public String boardUpdate(Board board, // 커맨드 객체 (name  == 필드 ) 경우 필드에 파라미터 세팅) 
							  @PathVariable("boardCode") int boardCode,
							  @PathVariable("boardNo") int boardNo,
							  @RequestParam(value="cp", required =false, defaultValue = "1") int cp, // 쿼리스트링 유지
							  @RequestParam(value ="images", required=false) List<MultipartFile> images,
							  @RequestParam(value = "deleteList", required =false) String deleteList, // 삭제할 이미지 순서
							  RedirectAttributes ra // 리다이렉트 시 값 전달용 (message)
							  ) throws IllegalStateException, IOException{
		// 1) boardCode, boardNo를 커맨드 객체(board)에 세팅
		board.setBoardCode(boardCode);
		board.setBoardNo(boardNo);
		// board ( boardCode, boardNo, boardTitle, boardContent)

		
		// 3) 게시글 수정 서비스 호출
		int rowCount = service.boardUpdate(board, images, deleteList);
		
		// 4) 결과에 따라 message, path 설정
		String message = null;
		String path = "redirect:";
		
		if(rowCount > 0) {
			message =  "게시글이 수정되었습니다.";
			path += "/board/" + boardCode + "/" + boardNo + "?cp=" + cp;
		}else {
			message = "게시글 수정 실패";
			path += "update";
		}
		
		ra.addFlashAttribute("message", message);
		
		return path;
	}
	
	
	// 게시글 삭제
/*	
	@GetMapping("/{boardCode}/{boardNo}/delete")
	public String boardDelete() {
		
		// boardCode, boardNo 서비스로 넘겨야함.
		// map 으로 담아서 보내는 걸 추천
		
		// 결과값이 > 0 
		// 삭제되었습니다.  /board/{boardCode}
		
		// else
		// 삭제 실패      /board/{boardCode}/{boardNo}
		
		
	}
	
	*/
	
	
}
