package edu.kh.project.board.model.service;

import java.util.List;
import java.util.Map;

import edu.kh.project.board.model.dto.Board;

public interface BoardService  {

	/** 게시판 종류 조회
	 * @return boardTypeList
	 */
	List<Map<String, Object>> selectBoardTypeList();

	
	// 게시글 목록조회 
	Map<String, Object> selectBoardList(int boardCode, int cp);

	// 게시글 상세 조회
	Board selectBoardList(Map<String, Object> map);

	// 좋아요 여부 확인 서비스
	int boardLikeCheck(Map<String, Object> map);


	/** 조회수 증가 서비스
	 * @param boardNo
	 * @return
	 */
	int updateReadCount(int boardNo);


	int updateLike(Map<String, Integer> paramMap);


	/** 게시글 검색 목록조회
	 * @param paramMap
	 * @param cp
	 * @return
	 */
	Map<String, Object> selectBoardList(Map<String, Object> paramMap, int cp);


	/** DB 파일 목록 조회 
	 * @return
	 */
	List<String> selectImageList();

	
}
