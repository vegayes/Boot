package edu.kh.project.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.board.model.dto.Board;

@Mapper
public interface BoardMapper {

	List<Map<String, Object>> selectBoardTypeList();
	
	// 특정 게시판의 삭제되지 않은 게시판 갯수 확인
	public int getListCount(int boardCode);

	// 특정 게시판에서 현재 페이지에 해당하는 부분에 대한 게시글 목록 조회
	public List<Board> selectBoardList(int boardCode, RowBounds rowBounds);

	// 게시글 상세 조회
	public Board selectBoardList(Map<String, Object> map) ;

	/** 좋아요 여부 확인 DAO
	 * @param map
	 * @return
	 */
	public int boardLikeCheck(Map<String, Object> map);

	/** 조회수 증가 DAO
	 * @param boardNo
	 * @return
	 */
	public int updateReadCount(int boardNo);

	public int addLike(Map<String, Integer> paramMap);

	public int delLike(Map<String, Integer> paramMap);

	public int countBoardLike(Integer boardNo);

	/** 검색한 목록 조회의 개수 확인 (페이지네이션 만들기 위해서 얻어와야 함.)
	 * @param paramMap
	 * @return
	 */
	public int getSearchListCount(Map<String, Object> paramMap);

	/** 검색한 목록 조회 
	 * @param pagination
	 * @param paramMap
	 * @return
	 */
	public List<Board> selectSearchBoardList(Map<String, Object> paramMap, RowBounds rowBounds) ;

	public List<String> selectImageList() ;
	
	
	
	

}
