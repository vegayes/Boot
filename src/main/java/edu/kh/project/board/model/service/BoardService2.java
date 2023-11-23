package edu.kh.project.board.model.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.board.model.dto.Board;

public interface BoardService2 {

	/** 게시글 삽입
	 * @param board
	 * @param images
	 * @param webPath
	 * @param filePath
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	int boardInsert(Board board, List<MultipartFile> images) throws IllegalStateException, IOException;

	/** 게시글 수정
	 * @param board
	 * @param images
	 * @param filePath
	 * @param deleteList
	 * @param deleteList2 
	 * @return
	 */
	int boardUpdate(Board board, List<MultipartFile> images, String deleteList)throws IllegalStateException, IOException;
}
