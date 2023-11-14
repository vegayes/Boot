package edu.kh.project.myPage.model.service;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;

public interface MyPageService {

	/** 회원 정보 수정 서비스
	 * @param updateMember
	 * @return result
	 */
	int updateInfo(Member updateMember);

	int updateProfile(MultipartFile profileImage, Member loginMember) throws Exception;

	int updatePw(Member loginMember);

	String pwCheck(Member loginMember);

	int memberSecession(Member loginMember);
	
	
	/** 비밀번호 변경 서비스
	 * @param currentPw
	 * @param newPw
	 * @param memberNo
	 * @return result
	 */
	int changePw(String currentPw, String newPw, int memberNo);


	/** 회원 탈퇴 서비스
	 * @param memberPw
	 * @param memberNo
	 * @return result
	 */
	int secession(String memberPw, int memberNo);

}
