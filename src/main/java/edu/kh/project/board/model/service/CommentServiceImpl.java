package edu.kh.project.board.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.board.model.dao.CommentMapper;
import edu.kh.project.board.model.dto.Comment;

@Service
public class CommentServiceImpl implements CommentService{

	@Autowired
	private CommentMapper dao;

	@Override
	public List<Comment> select(int boardNo) {
		return dao.select(boardNo);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public int insert(Comment comment) {
		return dao.insert(comment);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public int delete(int commentNo) {
		return dao.delete(commentNo);
	}

	@Override
	public int update(Comment comment) {
		return dao.update(comment);
	}





}
