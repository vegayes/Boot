package edu.kh.project.member.model.dao;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface AjaxMapper {
	 
	/**닉네임으로 전화번호 조회 
	 * @param nickname
	 * @return
	 */
	String selectMemberTel(String nickname);
	
	/** 이메일로 회원 정보 조회  
	 * @param email
	 * @return
	 */
	Member selectMember(String email);
	
	/** 이메일 중복검사
	 * @param email
	 * @return
	 */
	int checkEmail(String email);
	
	/** 닉네임 중복검사
	 * @param nickname
	 * @return
	 */
	int checkNickname(String nickname);
	

}
