package edu.kh.project.myPage.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MyPageMapper {

	public int updateInfo(Member updateMember);

	
	/** 프로필 이미지 수정
	 * @param loginMember
	 * @return result
	 */
	public int updateProfileImage(Member loginMember) ;


	public String pwCheck(Member loginMember) ;


	public int memberSecession(Member loginMember);


	public int updatePw(Member loginMember);
	
	
	/** 회원 비밀번호 조회
	 * @param memberNo
	 * @return encPw
	 */
	public String selectEncPw(int memberNo);


	/** 비밀번호 변경
	 * @param newPw
	 * @param memberNo
	 * @return result
	 */
	public int changePw(Member member);


	/** 회원 탈퇴 DAO
	 * @param memberNo
	 * @return result
	 */
	public int secession(int memberNo);
}
