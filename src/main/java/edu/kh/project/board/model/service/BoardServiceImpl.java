package edu.kh.project.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dao.BoardMapper;
import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.Pagination;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper mapper;

	/**
	 * 게시판 종류 조회 
	 */
	@Override
	public List<Map<String, Object>> selectBoardTypeList() {
		return mapper.selectBoardTypeList();
	}
	
	// 게시판 목록 조회 
	@Override
	public Map<String, Object> selectBoardList(int boardCode, int cp) {

		// 1. 특정 게시판의 삭제되지 않은 게시글 수 조회 
		int listCount = mapper.getListCount(boardCode);
		
		// 2. 1번 조회 결과 + cp를 이용해서 Pagination 객체 생성
		// -> 내부 필드가 모두 계산되어 초기화됨
		Pagination pagination = new Pagination(listCount, cp);
		
		
		 // RowBounds 객체
		 // - MyBatis에서 페이징처리를 위해 제공하는 객체
		 // - offset만큼 건너뛰고 그 다음 지정된 행 개수(limi) 만큼 조회
		
		 // 1) offset계산
		 int offset = (pagination.getCurrentPage()-1) * pagination.getLimit();
		 // 1페이지는 0만큼 건너튀고 조회
		 // 2페이지는 10만큼 건너뛰고 조회 
		 // 3페이지는 20만큼 건너뛰고 조회 
		
		
		 // 2) RowBounds 객체 생성
		 RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());
		
		
		
		 // 3. 특정 게시판에서 
		 // 현재 페이지에 해당하는 부분에 대한 게시글 목록 조회
		 // 어떤 게시판에서(boardCode)
		 // 몇 페이지(pagination.currentPage)에 대한
		 // 게시글 몇 개 (pagination.limit)인지 조회
		 // 1page -> 100~91
		 // 2page -> 90~81
		 // 3page -> 80~71		
		  List<Board> boardList = mapper.selectBoardList(boardCode, rowBounds);
		
		  
		  // 4. pagination , boardList를 Map에 담아서 반환
		  Map<String, Object> map = new HashMap<String, Object>();
		  map.put("pagination", pagination);
		  map.put("boardList", boardList);
		  
		return map;
	}

	// 게시글 상세조회
	@Override
	public Board selectBoardList(Map<String, Object> map) {
		
		Board board = mapper.selectBoard(map);
		
		return board;
	}

	// 좋아요 여부 확인 서비스
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int boardLikeCheck(Map<String, Object> map) {
		
		
		int result = mapper.boardLikeCheck(map);
		
		
		return result;
	}

	/**
	 * 조회수 증가 서비스
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int updateReadCount(int boardNo) {
		return mapper.updateReadCount(boardNo);
	}

	/**
	 *	좋아요 수정 서비스
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int updateLike(Map<String, Integer> paramMap) {
		
		int check = (int)paramMap.get("check"); 
		
		System.out.println("check :" + check);
		
		int result = 0;
		
		if(check == 0) {
			result = mapper.addLike(paramMap);

		}else {
			result = mapper.delLike(paramMap);
		}
		
		if(result == 0) return -1;
		
		return mapper.countBoardLike(paramMap.get("boardNo"));
	}

	/**
	 * 게시글 검색 목록조회 (위에 그냥 목록조회랑 기능 동일함)
	 */
	@Override
	public Map<String, Object> selectBoardList(Map<String, Object> paramMap, int cp) {
		
		// 1. 특정 게시판의 삭제되지 않고, 검색 조건이 일치하는 게시글 수 조회 
		int listCount = mapper.getSearchListCount(paramMap);
		
		// 2. 1번 조회 결과 + cp를 이용해서 Pagination 객체 생성
		// -> 내부 필드가 모두 계산되어 초기화됨
		Pagination pagination = new Pagination(listCount, cp);
		
		// RowBounds 객체
		// - MyBatis에서 페이징처리를 위해 제공하는 객체
		// - offset만큼 건너뛰고 그 다음 지정된 행 개수(limi) 만큼 조회
		
		// 1) offset계산
		int offset = (pagination.getCurrentPage()-1) * pagination.getLimit();
		// 1페이지는 0만큼 건너튀고 조회
		// 2페이지는 10만큼 건너뛰고 조회 
		// 3페이지는 20만큼 건너뛰고 조회 
		
		
		// 2) RowBounds 객체 생성
		RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());
		
		 // 3. 특정 게시판에서 
		 // 현재 페이지에 해당하는 부분에 대한 게시글 목록 조회
		 // 단, 검색 조건이 일치하는 글만 
		  List<Board> boardList = mapper.selectSearchBoardList(paramMap, rowBounds);
		
		  
		  // 4. pagination , boardList를 Map에 담아서 반환
		  Map<String, Object> map = new HashMap<String, Object>();
		  map.put("pagination", pagination);
		  map.put("boardList", boardList);
		
		return map;
	}

	/**
	 * DB이미지 목록 조회 
	 */
	@Override
	public List<String> selectImageList() {
		return mapper.selectImageList();
	}
	
	
	
	
	
}
