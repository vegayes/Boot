package edu.kh.project.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Comment;
import edu.kh.project.board.model.service.CommentService;
import edu.kh.project.member.model.dto.Member;

// @Controller + @ResponseBody

@RestController // 요청/응답 처리 (단, 모든 요청 응답은 비동기)
				// => REST API를 구축하기 위한 Controller 
public class CommentController {
	
	@Autowired
	private CommentService service;

	// 댓글 목록 조회 								// json 통신 시 한글깨짐 방지
	@GetMapping(value = "/comment", produces = "application/json; charset=UTF-8")
	public List<Comment> select(int boardNo) {
		System.out.println("댓글 조회 비동기 :" +  boardNo);
				
		
		return service.select(boardNo);
		// 동기 시 return : forward / redirect
		// 비동기 시 return : 값 자체
	}
	
	
	// 댓글 삽입
	@GetMapping(value = "/commnet/insert", produces = "application/json; charset=UTF-8" )	
	public int insert(String commentContent, int boardNo,
					 Comment comment,
					@SessionAttribute(value = "loginMember", required =false) Member loginMember) {
		
		
		System.out.println("commentContent" + commentContent);
		System.out.println("boardNo" + boardNo);
		
		System.out.println("댓글 삽입 로그인된 회원 번호 :" + loginMember.getMemberNo());
		
		
		comment.setBoardNo(boardNo);
		comment.setCommentContent(commentContent);
		comment.setMemberNo(loginMember.getMemberNo());
		
		
		return service.insert(comment);
	}
	
	
	
	
	// 댓글 삭제
	// 선택된 댓글이 삭제 된지 
	@GetMapping(value = "/commnet/delete", produces = "application/json; charset=UTF-8" )	
	public int delete(int commentNo) {
		
		System.out.println("commentNo = " + commentNo);
		
		return service.delete(commentNo);
	}
	
	
	// 댓글 수정
	@GetMapping(value="/commnet/update", produces = "application/json; charset=UTF-8" )	
	public int update(int commentNo, String commentContent, Comment comment) {
		
		System.out.println("commentNo 누름");
		
		System.out.println(commentNo);
		System.out.println(commentContent);
		
//		service.update(commentNo);
		
		comment.setCommentNo(commentNo);
		comment.setCommentContent(commentContent);
		
		
		return service.update(comment);
	}
	
	// 답글 추가
	@GetMapping(value = "/commnet/reply", produces = "application/json; charset=UTF-8" )	
	public int reply(@SessionAttribute(value = "loginMember", required =false) Member loginMember,
			String commentContent, int parentNo , int boardNo, Comment comment) {
		
		System.out.println("답글 parentNo" + parentNo);
		System.out.println("보드 boardNo : "+boardNo);
		
		comment.setCommentContent(commentContent);
		comment.setParentNo(parentNo);
		comment.setMemberNo(loginMember.getMemberNo());
		comment.setBoardNo(boardNo);

		return service.insert(comment);
//		return 0;
	}
	
}
